import models.CityMapNode;
import java.util.*;

public class DebugPathfinding {
    public static void main(String[] args) {
        System.out.println("=== DEBUG PATHFINDING ===");
        
        // Load city map and inspect
        CityMap cityMap = new CityMap("city_map.csv");
        Map<Integer, CityMapNode> nodes = cityMap.getAllNodes();
        
        System.out.println("Inspecting node 0:");
        CityMapNode node0 = nodes.get(0);
        if (node0 != null) {
            System.out.println("Node 0 position: (" + node0.getX() + ", " + node0.getY() + ")");
            System.out.println("Adjacent edges:");
            for (CityMapNode.Edge edge : node0.getAdjacentEdges()) {
                System.out.println("  -> Node " + edge.getDestinationNode() + 
                    ", base weight: " + edge.getBaseWeight() + 
                    ", dynamic weight: " + edge.getDynamicWeight(1.0));
            }
        }
        
        System.out.println("\nTesting PathfindingService distance calculation:");
        PathfindingService service = new PathfindingService(cityMap);
        PathfindingService.PathResult result = service.calculateShortestPath(0, 3);
        System.out.println("Path from 0 to 3: " + result.getPath());
        System.out.println("Distance: " + result.getDistance());
        
        // Manual distance calculation
        List<Integer> path = result.getPath();
        if (path.size() >= 2) {
            double manualDistance = 0.0;
            for (int i = 0; i < path.size() - 1; i++) {
                CityMapNode current = nodes.get(path.get(i));
                CityMapNode next = nodes.get(path.get(i + 1));
                System.out.println("From node " + path.get(i) + " to " + path.get(i + 1));
                
                if (current != null && next != null) {
                    for (CityMapNode.Edge edge : current.getAdjacentEdges()) {
                        if (edge.getDestinationNode() == next.getNodeId()) {
                            double edgeWeight = edge.getDynamicWeight(current.getTrafficMultiplier());
                            manualDistance += edgeWeight;
                            System.out.println("  Edge weight: " + edgeWeight);
                            break;
                        }
                    }
                }
            }
            System.out.println("Manual total distance: " + manualDistance);
        }
    }
}
