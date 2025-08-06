import algorithms.Dijkstra;
import models.CityMapNode;
import java.util.*;

public class DebugDijkstra {
    public static void main(String[] args) {
        System.out.println("=== DEBUG DIJKSTRA ===");
        
        // Load city map
        CityMap cityMap = new CityMap("city_map.csv");
        Map<Integer, CityMapNode> nodes = cityMap.getAllNodes();
        
        System.out.println("Available nodes: " + nodes.keySet());
        
        // Test direct connection from 0 to 3
        System.out.println("\nTesting Dijkstra from 0 to 3:");
        List<Integer> path = Dijkstra.findShortestPath(nodes, 0, 3);
        System.out.println("Returned path: " + path);
        
        // Test with different nodes
        System.out.println("\nTesting Dijkstra from 0 to 1:");
        path = Dijkstra.findShortestPath(nodes, 0, 1);
        System.out.println("Returned path: " + path);
        
        // Check if nodes exist
        System.out.println("\nNode existence check:");
        System.out.println("Node 0 exists: " + (nodes.get(0) != null));
        System.out.println("Node 3 exists: " + (nodes.get(3) != null));
    }
}
