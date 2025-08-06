import models.CityMapNode;
import java.util.*;

public class FinalVerification {
    public static void main(String[] args) {
        System.out.println("=== FINAL PROJECT VERIFICATION ===\n");
        
        boolean allPassed = true;
        
        // Test 1: CityMap Loading
        System.out.println("1. Testing CityMap Loading...");
        try {
            CityMap cityMap = new CityMap("city_map.csv");
            System.out.println("   ✅ CityMap loaded: " + cityMap.getTotalNodes() + " nodes, " + cityMap.getTotalEdges() + " edges");
        } catch (Exception e) {
            System.out.println("   ❌ CityMap failed: " + e.getMessage());
            allPassed = false;
        }
        
        // Test 2: PathfindingService
        System.out.println("\n2. Testing PathfindingService...");
        try {
            CityMap cityMap = new CityMap("city_map.csv");
            PathfindingService service = new PathfindingService(cityMap);
            PathfindingService.PathResult result = service.calculateShortestPath(0, 3);
            
            if (result.getPath().isEmpty()) {
                System.out.println("   ❌ PathfindingService returning empty paths");
                allPassed = false;
            } else if (result.getDistance() == 0.0) {
                System.out.println("   ❌ PathfindingService returning zero distances");
                allPassed = false;
            } else {
                System.out.println("   ✅ PathfindingService working: path=" + result.getPath() + ", distance=" + result.getDistance());
            }
        } catch (Exception e) {
            System.out.println("   ❌ PathfindingService failed: " + e.getMessage());
            allPassed = false;
        }
        
        // Test 3: Algorithms
        System.out.println("\n3. Testing Algorithms...");
        try {
            CityMap cityMap = new CityMap("city_map.csv");
            Map<Integer, CityMapNode> nodes = cityMap.getAllNodes();
            
            // Test Dijkstra
            List<Integer> dijkstraPath = algorithms.Dijkstra.findShortestPath(nodes, 0, 3);
            if (dijkstraPath.isEmpty()) {
                System.out.println("   ❌ Dijkstra returning empty paths");
                allPassed = false;
            } else {
                System.out.println("   ✅ Dijkstra working: " + dijkstraPath);
            }
            
            // Test A*
            algorithms.AStar.AStarResult astarResult = algorithms.AStar.findPath(nodes, 0, 3, algorithms.AStar.EUCLIDEAN_HEURISTIC);
            if (astarResult.getPath().isEmpty()) {
                System.out.println("   ❌ A* returning empty paths");
                allPassed = false;
            } else {
                System.out.println("   ✅ A* working: " + astarResult.getPath());
            }
        } catch (Exception e) {
            System.out.println("   ❌ Algorithms failed: " + e.getMessage());
            allPassed = false;
        }
        
        // Test 4: Data Structures
        System.out.println("\n4. Testing Data Structures...");
        try {
            // Test Min-Heap
            algorithms.Dijkstra.MinHeap heap = new algorithms.Dijkstra.MinHeap();
            heap.insert(1, 5.0);
            heap.insert(2, 3.0);
            if (heap.extractMin().distance != 3.0) {
                System.out.println("   ❌ Min-Heap not working correctly");
                allPassed = false;
            } else {
                System.out.println("   ✅ Min-Heap working");
            }
            
            // Test LRU Cache
            PathfindingService.LRUCache<String, String> cache = new PathfindingService.LRUCache<>(2);
            cache.put("a", "1");
            cache.put("b", "2");
            cache.put("c", "3"); // Should evict "a"
            if (cache.containsKey("a")) {
                System.out.println("   ❌ LRU Cache not evicting correctly");
                allPassed = false;
            } else {
                System.out.println("   ✅ LRU Cache working");
            }
        } catch (Exception e) {
            System.out.println("   ❌ Data Structures failed: " + e.getMessage());
            allPassed = false;
        }
        
        System.out.println("\n=== VERIFICATION RESULT ===");
        if (allPassed) {
            System.out.println("🎉 ALL TESTS PASSED - PROJECT COMPLETE! 🎉");
        } else {
            System.out.println("❌ Some tests failed - check implementations");
        }
    }
}
