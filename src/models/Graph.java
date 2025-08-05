package models;

import java.util.*;

// This class stores the city map as a graph data structure
public class Graph {
    // Each node (location) has a list of connected edges (roads)
    private Map<Integer, List<Edge>> adjacencyList;

    public Graph() {
        // Initialize with empty adjacency list
        adjacencyList = new HashMap<>();
    }

    public void addEdge(int from, int to, double weight) {
        // Create bidirectional connection between two nodes
        adjacencyList.putIfAbsent(from, new ArrayList<>());
        adjacencyList.putIfAbsent(to, new ArrayList<>());

        adjacencyList.get(from).add(new Edge(to, weight));  // from -> to
        adjacencyList.get(to).add(new Edge(from, weight));  // to -> from (bidirectional)
    }

    public List<Edge> getNeighbors(int node) {
        // Return all edges connected to this node
        return adjacencyList.getOrDefault(node, new ArrayList<>());
    }

    public boolean containsNode(int node) {
        // Check if the node exists in the graph
        return adjacencyList.containsKey(node);
    }

    public Map<Integer, List<Edge>> getAllEdges() {
        // Return the complete adjacency list
        return adjacencyList;
    }
}
