package database;

import models.Graph;
import models.Edge;
import java.sql.*;

// This class loads the map from a database
public class CityMapDAO {
    private Connection connection;

    public CityMapDAO(Connection connection) {
        this.connection = connection; // TODO: save DB connection
    }

    public Graph loadGraphFromDatabase() throws SQLException {
        Graph graph = new Graph(); // TODO: empty map

        String query = "SELECT node_id, connected_to, travel_time FROM CityMap";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            int from = rs.getInt("node_id");
            int to = rs.getInt("connected_to");
            double weight = rs.getDouble("travel_time");

            graph.addEdge(from, to, weight); // TODO: add to map
        }

        return graph; // TODO: return finished map
    }
}
