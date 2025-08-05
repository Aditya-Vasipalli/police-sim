package database;

import models.CityMapNode;
import java.util.*;
import java.sql.*;

/**
 * Data Access Object for loading city map data from database
 * Works with CityMapNode model to build the graph structure
 */
public class CityMapDAO {
    private Connection connection;

    public CityMapDAO(Connection connection) {
        this.connection = connection;
    }
    
    /**
     * Load all nodes from database and build node map
     * @return Map of nodeId to CityMapNode objects
     */
    public Map<Integer, CityMapNode> loadNodesFromDatabase() throws SQLException {
        Map<Integer, CityMapNode> nodes = new HashMap<>();
        
        String query = "SELECT node_id, x_coord, y_coord, connected_to, travel_time, road_type FROM CityMap";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            int nodeId = rs.getInt("node_id");
            double x = rs.getDouble("x_coord");
            double y = rs.getDouble("y_coord");
            int connectedTo = rs.getInt("connected_to");
            double travelTime = rs.getDouble("travel_time");
            String roadType = rs.getString("road_type");

            // Create or get the source node
            CityMapNode node = nodes.computeIfAbsent(nodeId, 
                id -> new CityMapNode(id, x, y));
            
            // Add edge to connected node
            if (connectedTo != nodeId) { // Avoid self-loops
                node.addEdge(connectedTo, travelTime, roadType != null ? roadType : "street");
            }
        }

        rs.close();
        stmt.close();
        return nodes;
    }
    
    /**
     * Get all unique node IDs from database
     */
    public List<Integer> getAllNodeIds() throws SQLException {
        Set<Integer> nodeIds = new HashSet<>();
        
        String query = "SELECT DISTINCT node_id FROM CityMap UNION SELECT DISTINCT connected_to FROM CityMap";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        
        while (rs.next()) {
            nodeIds.add(rs.getInt(1));
        }
        
        rs.close();
        stmt.close();
        return new ArrayList<>(nodeIds);
    }
    
    /**
     * Get neighbors of a specific node from database
     */
    public List<CityMapNode> getNeighbors(int nodeId) throws SQLException {
        List<CityMapNode> neighbors = new ArrayList<>();
        
        String query = "SELECT connected_to, x_coord, y_coord FROM CityMap WHERE node_id = ?";
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, nodeId);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            int connectedId = rs.getInt("connected_to");
            double x = rs.getDouble("x_coord");
            double y = rs.getDouble("y_coord");
            neighbors.add(new CityMapNode(connectedId, x, y));
        }
        
        rs.close();
        pstmt.close();
        return neighbors;
    }
    
    /**
     * Insert a new edge into the database
     */
    public void insertEdge(int fromNode, double fromX, double fromY, 
                          int toNode, double toX, double toY, 
                          double travelTime, String roadType) throws SQLException {
        String query = "INSERT INTO CityMap (node_id, x_coord, y_coord, connected_to, travel_time, road_type) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(query);
        
        pstmt.setInt(1, fromNode);
        pstmt.setDouble(2, fromX);
        pstmt.setDouble(3, fromY);
        pstmt.setInt(4, toNode);
        pstmt.setDouble(5, travelTime);
        pstmt.setString(6, roadType);
        
        pstmt.executeUpdate();
        pstmt.close();
    }
    
    /**
     * Close database connection
     */
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
