import models.CityMapNode;
import java.util.*;

public class DebugService {
    public static void main(String[] args) {
        System.out.println("=== DEBUG PATHFINDING SERVICE ===");
        
        // Load city map
        CityMap cityMap = new CityMap("city_map.csv");
        
        // Create pathfinding service
        PathfindingService service = new PathfindingService(cityMap);
        
        System.out.println("Testing service...");
        PathfindingService.PathResult result = service.calculateShortestPath(0, 3);
        
        System.out.println("PathResult details:");
        System.out.println("  Path: " + result.getPath());
        System.out.println("  Distance: " + result.getDistance());
        System.out.println("  Algorithm: " + result.getAlgorithm());
        
        // Test direct Dijkstra call vs service call
        Map<Integer, CityMapNode> nodes = cityMap.getAllNodes();
        List<Integer> directPath = algorithms.Dijkstra.findShortestPath(nodes, 0, 3);
        System.out.println("\nDirect Dijkstra path: " + directPath);
        
        // Check if nodes exist in service
        System.out.println("\nChecking service internals...");
        System.out.println("City map has " + nodes.size() + " nodes");
    }
}
