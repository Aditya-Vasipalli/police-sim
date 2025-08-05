// CityMap.java
// Module 1: City Map & Environment
// Responsible for loading and managing the city graph (adjacency list)
// Enhanced to support pathfinding algorithms

import models.CityMapNode;
import java.util.*;
import java.io.*;

public class CityMap {
    
    private Map<Integer, CityMapNode> nodes;
    private int totalNodes;
    private int totalEdges;
    private String mapFilename;
    
    // Graph statistics
    private double avgDegree;
    private int maxDegree;
    private double networkDensity;
    
    public CityMap(String mapFile) {
        this.mapFilename = mapFile;
        this.nodes = new HashMap<>();
        this.totalNodes = 0;
        this.totalEdges = 0;
        
        loadCityMapFromFile(mapFile);
        calculateGraphStatistics();
    }
    
    /**
     * Load city map from CSV file
     * Expected format: nodeId1,x1,y1,nodeId2,x2,y2,weight,roadType
     */
    private void loadCityMapFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    try {
                        int nodeId1 = Integer.parseInt(parts[0].trim());
                        double x1 = Double.parseDouble(parts[1].trim());
                        double y1 = Double.parseDouble(parts[2].trim());
                        
                        int nodeId2 = Integer.parseInt(parts[3].trim());
                        double x2 = Double.parseDouble(parts[4].trim());
                        double y2 = Double.parseDouble(parts[5].trim());
                        
                        double weight = Double.parseDouble(parts[6].trim());
                        String roadType = parts.length > 7 ? parts[7].trim() : "street";
                        
                        // Create or get nodes
                        CityMapNode node1 = nodes.computeIfAbsent(nodeId1, 
                            id -> new CityMapNode(id, x1, y1));
                        CityMapNode node2 = nodes.computeIfAbsent(nodeId2, 
                            id -> new CityMapNode(id, x2, y2));
                        
                        // Add bidirectional edges (assuming undirected graph)
                        node1.addEdge(nodeId2, weight, roadType);
                        node2.addEdge(nodeId1, weight, roadType);
                        
                        totalEdges += 2; // Count both directions
                        
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number format in line: " + line);
                    }
                }
            }
            
            totalNodes = nodes.size();
            System.out.println("Loaded city map: " + totalNodes + " nodes, " + totalEdges + " edges");
            
        } catch (IOException e) {
            System.err.println("Error loading city map from " + filename + ": " + e.getMessage());
            createDefaultMap(); // Create a simple default map for testing
        }
    }
    
    /**
     * Create a simple default map for testing when file loading fails
     */
    private void createDefaultMap() {
        System.out.println("Creating default test map...");
        
        // Create a simple 3x3 grid
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int nodeId = i * 3 + j;
                nodes.put(nodeId, new CityMapNode(nodeId, i * 10.0, j * 10.0));
            }
        }
        
        // Add edges to form a grid
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int nodeId = i * 3 + j;
                CityMapNode node = nodes.get(nodeId);
                
                // Right neighbor
                if (j < 2) {
                    int rightNeighbor = i * 3 + (j + 1);
                    node.addEdge(rightNeighbor, 10.0, "street");
                    totalEdges++;
                }
                
                // Bottom neighbor
                if (i < 2) {
                    int bottomNeighbor = (i + 1) * 3 + j;
                    node.addEdge(bottomNeighbor, 10.0, "street");
                    totalEdges++;
                }
            }
        }
        
        totalNodes = nodes.size();
        System.out.println("Created default map: " + totalNodes + " nodes, " + totalEdges + " edges");
    }
    
    /**
     * Calculate graph statistics for analysis
     */
    private void calculateGraphStatistics() {
        if (nodes.isEmpty()) return;
        
        int totalDegree = 0;
        maxDegree = 0;
        
        for (CityMapNode node : nodes.values()) {
            int degree = node.getAdjacentEdges().size();
            totalDegree += degree;
            maxDegree = Math.max(maxDegree, degree);
        }
        
        avgDegree = (double) totalDegree / totalNodes;
        networkDensity = (double) totalEdges / (totalNodes * (totalNodes - 1));
        
        System.out.printf("Graph Statistics: Avg Degree=%.2f, Max Degree=%d, Density=%.4f%n", 
            avgDegree, maxDegree, networkDensity);
    }
    
    /**
     * Get neighbors of a specific node
     */
    public List<Integer> getNeighbors(int nodeId) {
        CityMapNode node = nodes.get(nodeId);
        if (node == null) return new ArrayList<>();
        
        List<Integer> neighbors = new ArrayList<>();
        for (CityMapNode.Edge edge : node.getAdjacentEdges()) {
            neighbors.add(edge.getDestinationNode());
        }
        
        return neighbors;
    }
    
    /**
     * Get edge weight between two nodes
     */
    public double getEdgeWeight(int fromNode, int toNode) {
        CityMapNode node = nodes.get(fromNode);
        if (node == null) return Double.MAX_VALUE;
        
        for (CityMapNode.Edge edge : node.getAdjacentEdges()) {
            if (edge.getDestinationNode() == toNode) {
                return edge.getDynamicWeight(node.getTrafficMultiplier());
            }
        }
        
        return Double.MAX_VALUE; // No direct edge
    }
    
    /**
     * Get base edge weight (without traffic multiplier)
     */
    public double getBaseEdgeWeight(int fromNode, int toNode) {
        CityMapNode node = nodes.get(fromNode);
        if (node == null) return Double.MAX_VALUE;
        
        for (CityMapNode.Edge edge : node.getAdjacentEdges()) {
            if (edge.getDestinationNode() == toNode) {
                return edge.getBaseWeight();
            }
        }
        
        return Double.MAX_VALUE;
    }
    
    /**
     * Check if two nodes are connected
     */
    public boolean areConnected(int nodeId1, int nodeId2) {
        return getEdgeWeight(nodeId1, nodeId2) != Double.MAX_VALUE;
    }
    
    /**
     * Get all nodes in the map
     */
    public Map<Integer, CityMapNode> getAllNodes() {
        return new HashMap<>(nodes);
    }
    
    /**
     * Get node by ID
     */
    public CityMapNode getNode(int nodeId) {
        return nodes.get(nodeId);
    }
    
    /**
     * Get all node IDs
     */
    public Set<Integer> getNodeIds() {
        return new HashSet<>(nodes.keySet());
    }
    
    /**
     * Update traffic conditions for multiple nodes
     */
    public void updateTrafficConditions(Map<Integer, Double> trafficMultipliers) {
        for (Map.Entry<Integer, Double> entry : trafficMultipliers.entrySet()) {
            CityMapNode node = nodes.get(entry.getKey());
            if (node != null) {
                node.setTrafficMultiplier(entry.getValue());
            }
        }
    }
    
    /**
     * Find closest node to given coordinates
     */
    public int findClosestNode(double x, double y) {
        if (nodes.isEmpty()) return -1;
        
        int closestNode = -1;
        double minDistance = Double.MAX_VALUE;
        
        for (CityMapNode node : nodes.values()) {
            double distance = Math.sqrt(Math.pow(node.getX() - x, 2) + Math.pow(node.getY() - y, 2));
            if (distance < minDistance) {
                minDistance = distance;
                closestNode = node.getNodeId();
            }
        }
        
        return closestNode;
    }
    
    /**
     * Get nodes within a certain radius
     */
    public List<Integer> getNodesInRadius(double centerX, double centerY, double radius) {
        List<Integer> nodesInRadius = new ArrayList<>();
        
        for (CityMapNode node : nodes.values()) {
            double distance = Math.sqrt(Math.pow(node.getX() - centerX, 2) + Math.pow(node.getY() - centerY, 2));
            if (distance <= radius) {
                nodesInRadius.add(node.getNodeId());
            }
        }
        
        return nodesInRadius;
    }
    
    /**
     * Export map to DOT format for visualization
     */
    public void exportToDot(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("graph CityMap {");
            writer.println("  node [shape=circle];");
            
            Set<String> writtenEdges = new HashSet<>();
            
            for (CityMapNode node : nodes.values()) {
                // Write node with position
                writer.printf("  %d [pos=\"%.1f,%.1f!\"];%n", 
                    node.getNodeId(), node.getX(), node.getY());
                
                // Write edges
                for (CityMapNode.Edge edge : node.getAdjacentEdges()) {
                    String edgeKey1 = node.getNodeId() + "-" + edge.getDestinationNode();
                    String edgeKey2 = edge.getDestinationNode() + "-" + node.getNodeId();
                    
                    if (!writtenEdges.contains(edgeKey1) && !writtenEdges.contains(edgeKey2)) {
                        writer.printf("  %d -- %d [label=\"%.1f\"];%n", 
                            node.getNodeId(), edge.getDestinationNode(), edge.getBaseWeight());
                        writtenEdges.add(edgeKey1);
                    }
                }
            }
            
            writer.println("}");
        }
    }
    
    // Getters for statistics
    public int getTotalNodes() { return totalNodes; }
    public int getTotalEdges() { return totalEdges; }
    public double getAvgDegree() { return avgDegree; }
    public int getMaxDegree() { return maxDegree; }
    public double getNetworkDensity() { return networkDensity; }
    public String getMapFilename() { return mapFilename; }
    
    /**
     * Validate graph connectivity
     */
    public boolean isConnected() {
        if (nodes.isEmpty()) return true;
        
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        
        // Start BFS from any node
        int startNode = nodes.keySet().iterator().next();
        queue.offer(startNode);
        visited.add(startNode);
        
        while (!queue.isEmpty()) {
            int current = queue.poll();
            
            for (int neighbor : getNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }
        
        return visited.size() == totalNodes;
    }
    
    @Override
    public String toString() {
        return String.format("CityMap{nodes=%d, edges=%d, avgDegree=%.2f, connected=%s}", 
            totalNodes, totalEdges, avgDegree, isConnected());
    }
}
