package algorithms;

import models.Graph;
import models.Edge;
import java.util.*;

/**
 * Floyd-Warshall algorithm implementation for finding shortest paths between all pairs of nodes
 * Time complexity: O(V^3)
 * Space complexity: O(V^2)
 */
public class FloydWarshall {
    private double[][] distances;
    private int[][] next;
    private int nodeCount;
    private Map<Integer, Integer> nodeToIndex;
    private Map<Integer, Integer> indexToNode;
    
    /**
     * Constructor that initializes the Floyd-Warshall algorithm
     * @param graph The graph to process
     */
    public FloydWarshall(Graph graph) {
        initializeMatrices(graph);
        computeShortestPaths();
    }
    
    /**
     * Initialize distance and next matrices from the graph
     */
    private void initializeMatrices(Graph graph) {
        // Get all nodes from the graph
        Set<Integer> allNodes = new HashSet<>(graph.getAllEdges().keySet());
        for (List<Edge> edges : graph.getAllEdges().values()) {
            for (Edge edge : edges) {
                allNodes.add(edge.getDestination());
            }
        }
        
        nodeCount = allNodes.size();
        distances = new double[nodeCount][nodeCount];
        next = new int[nodeCount][nodeCount];
        nodeToIndex = new HashMap<>();
        indexToNode = new HashMap<>();
        
        // Create mapping between node IDs and matrix indices
        int index = 0;
        for (Integer nodeId : allNodes) {
            nodeToIndex.put(nodeId, index);
            indexToNode.put(index, nodeId);
            index++;
        }
        
        // Initialize distance matrix with infinity
        for (int i = 0; i < nodeCount; i++) {
            for (int j = 0; j < nodeCount; j++) {
                if (i == j) {
                    distances[i][j] = 0;
                    next[i][j] = i;
                } else {
                    distances[i][j] = Double.POSITIVE_INFINITY;
                    next[i][j] = -1;
                }
            }
        }
        
        // Fill in direct edges
        for (Map.Entry<Integer, List<Edge>> entry : graph.getAllEdges().entrySet()) {
            int fromNode = entry.getKey();
            int fromIndex = nodeToIndex.get(fromNode);
            
            for (Edge edge : entry.getValue()) {
                int toNode = edge.getDestination();
                int toIndex = nodeToIndex.get(toNode);
                double weight = edge.getWeight();
                
                distances[fromIndex][toIndex] = weight;
                next[fromIndex][toIndex] = toIndex;
            }
        }
    }
    
    /**
     * Compute shortest paths using Floyd-Warshall algorithm
     */
    private void computeShortestPaths() {
        // Floyd-Warshall main algorithm
        for (int k = 0; k < nodeCount; k++) {
            for (int i = 0; i < nodeCount; i++) {
                for (int j = 0; j < nodeCount; j++) {
                    if (distances[i][k] + distances[k][j] < distances[i][j]) {
                        distances[i][j] = distances[i][k] + distances[k][j];
                        next[i][j] = next[i][k];
                    }
                }
            }
        }
    }
    
    /**
     * Get shortest distance between two nodes
     * @param from Source node ID
     * @param to Destination node ID
     * @return Shortest distance, or Double.POSITIVE_INFINITY if no path exists
     */
    public double getShortestDistance(int from, int to) {
        if (!nodeToIndex.containsKey(from) || !nodeToIndex.containsKey(to)) {
            return Double.POSITIVE_INFINITY;
        }
        
        int fromIndex = nodeToIndex.get(from);
        int toIndex = nodeToIndex.get(to);
        return distances[fromIndex][toIndex];
    }
    
    /**
     * Get shortest path between two nodes
     * @param from Source node ID
     * @param to Destination node ID
     * @return List of node IDs representing the path, empty if no path exists
     */
    public List<Integer> getShortestPath(int from, int to) {
        if (!nodeToIndex.containsKey(from) || !nodeToIndex.containsKey(to)) {
            return new ArrayList<>();
        }
        
        int fromIndex = nodeToIndex.get(from);
        int toIndex = nodeToIndex.get(to);
        
        if (distances[fromIndex][toIndex] == Double.POSITIVE_INFINITY) {
            return new ArrayList<>(); // No path exists
        }
        
        List<Integer> path = new ArrayList<>();
        reconstructPath(fromIndex, toIndex, path);
        return path;
    }
    
    /**
     * Reconstruct path from next matrix
     */
    private void reconstructPath(int fromIndex, int toIndex, List<Integer> path) {
        if (next[fromIndex][toIndex] == -1) {
            return; // No path
        }
        
        path.add(indexToNode.get(fromIndex));
        if (fromIndex != toIndex) {
            reconstructPath(next[fromIndex][toIndex], toIndex, path);
        }
    }
    
    /**
     * Get all shortest distances as a matrix
     * @return 2D array of distances
     */
    public double[][] getAllDistances() {
        return distances.clone();
    }
    
    /**
     * Check if there's a path between two nodes
     * @param from Source node ID
     * @param to Destination node ID
     * @return true if path exists, false otherwise
     */
    public boolean hasPath(int from, int to) {
        return getShortestDistance(from, to) != Double.POSITIVE_INFINITY;
    }
    
    /**
     * Get the node mapping for external use
     * @return Map from node ID to matrix index
     */
    public Map<Integer, Integer> getNodeMapping() {
        return new HashMap<>(nodeToIndex);
    }
}
