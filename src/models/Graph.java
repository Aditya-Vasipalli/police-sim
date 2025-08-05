package models;

import java.util.*;

// This class stores the full city as a graph
public class Graph {
    // TODO: every place (node) has a list of connected roads (edges)
    private Map<Integer, List<Edge>> adjacencyList;

    public Graph() {
        // TODO: start with empty map
        adjacencyList = new HashMap<>();
    }

    public void addEdge(int from, int to, double weight) {
        // TODO: connect two places with a road
        adjacencyList.putIfAbsent(from, new ArrayList<>());
        adjacencyList.putIfAbsent(to, new ArrayList<>());

        adjacencyList.get(from).add(new Edge(to, weight));  // one direction
        adjacencyList.get(to).add(new Edge(from, weight));  // TODO: both ways
    }

    public List<Edge> getNeighbors(int node) {
        // TODO: give list of connected roads
        return adjacencyList.getOrDefault(node, new ArrayList<>());
    }

    public boolean containsNode(int node) {
        // TODO: check if place exists
        return adjacencyList.containsKey(node);
    }

    public Map<Integer, List<Edge>> getAllEdges() {
        // TODO: give full city map
        return adjacencyList;
    }
}
