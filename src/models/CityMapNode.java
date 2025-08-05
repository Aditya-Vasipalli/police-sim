package models;

import java.util.ArrayList;
import java.util.List;

public class CityMapNode {
    private int nodeId;
    private double x, y; // Coordinates for A* heuristic
    private List<Edge> adjacentEdges;
    private double trafficMultiplier; // Dynamic traffic conditions
    
    public CityMapNode(int nodeId, double x, double y) {
        this.nodeId = nodeId;
        this.x = x;
        this.y = y;
        this.adjacentEdges = new ArrayList<>();
        this.trafficMultiplier = 1.0; // Default no traffic
    }
    
    public static class Edge {
        private int destinationNode;
        private double baseWeight;
        private String roadType; // highway, street, alley
        
        public Edge(int destinationNode, double baseWeight, String roadType) {
            this.destinationNode = destinationNode;
            this.baseWeight = baseWeight;
            this.roadType = roadType;
        }
        
        public double getDynamicWeight(double trafficMultiplier) {
            return baseWeight * trafficMultiplier;
        }
        
        // Getters and setters
        public int getDestinationNode() { return destinationNode; }
        public double getBaseWeight() { return baseWeight; }
        public String getRoadType() { return roadType; }
        public void setBaseWeight(double baseWeight) { this.baseWeight = baseWeight; }
    }
    
    public void addEdge(int destinationNode, double weight, String roadType) {
        adjacentEdges.add(new Edge(destinationNode, weight, roadType));
    }
    
    public double getEuclideanDistance(CityMapNode other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }
    
    // Getters and setters
    public int getNodeId() { return nodeId; }
    public double getX() { return x; }
    public double getY() { return y; }
    public List<Edge> getAdjacentEdges() { return adjacentEdges; }
    public double getTrafficMultiplier() { return trafficMultiplier; }
    public void setTrafficMultiplier(double trafficMultiplier) { this.trafficMultiplier = trafficMultiplier; }
}
