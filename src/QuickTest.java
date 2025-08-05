import algorithms.Dijkstra;
import algorithms.AStar;
import models.CityMapNode;
import java.util.*;

public class QuickTest {
    public static void main(String[] args) {
        System.out.println("=== Quick Algorithm Test ===");
        
        // Create a simple test graph
        Map<Integer, CityMapNode> testGraph = createTestGraph();
        
        System.out.println("Test Graph Created:");
        for (CityMapNode node : testGraph.values()) {
            System.out.println("Node " + node.getNodeId() + " at (" + 
                node.getX() + ", " + node.getY() + ")");
            for (CityMapNode.Edge edge : node.getAdjacentEdges()) {
                System.out.println("  -> Node " + edge.getDestinationNode() + 
                    " (weight: " + edge.getBaseWeight() + ")");
            }
        }
        
        // Test Dijkstra
        System.out.println("\nTesting Dijkstra from 0 to 3:");
        List<Integer> dijkstraPath = Dijkstra.findShortestPath(testGraph, 0, 3);
        System.out.println("Path: " + dijkstraPath);
        
        // Test A*
        System.out.println("\nTesting A* from 0 to 3:");
        AStar.AStarResult astarResult = AStar.findPath(testGraph, 0, 3, AStar.EUCLIDEAN_HEURISTIC);
        System.out.println("Path: " + astarResult.getPath());
        System.out.println("Distance: " + astarResult.getTotalDistance());
    }
    
    private static Map<Integer, CityMapNode> createTestGraph() {
        Map<Integer, CityMapNode> graph = new HashMap<>();
        
        // Create nodes
        CityMapNode node0 = new CityMapNode(0, 0.0, 0.0);
        CityMapNode node1 = new CityMapNode(1, 10.0, 0.0);
        CityMapNode node2 = new CityMapNode(2, 0.0, 10.0);
        CityMapNode node3 = new CityMapNode(3, 10.0, 10.0);
        
        // Add edges
        node0.addEdge(1, 10.0, "road");
        node0.addEdge(2, 10.0, "road");
        node1.addEdge(0, 10.0, "road");
        node1.addEdge(3, 10.0, "road");
        node2.addEdge(0, 10.0, "road");
        node2.addEdge(3, 10.0, "road");
        node3.addEdge(1, 10.0, "road");
        node3.addEdge(2, 10.0, "road");
        
        graph.put(0, node0);
        graph.put(1, node1);
        graph.put(2, node2);
        graph.put(3, node3);
        
        return graph;
    }
}
