// PathfindingDemo.java
// Comprehensive demonstration of Pathfinding Service & Route Optimization
// Shows all DSA concepts: Dijkstra, A*, LRU Cache, Sliding Window, Prefix Sums, Quick Sort

import algorithms.Dijkstra;
import algorithms.AStar;
import models.CityMapNode;
import java.util.*;

public class PathfindingDemo {
    
    public static void main(String[] args) {
        System.out.println("=== POLICE RESPONSE TIME SIMULATION ===");
        System.out.println("Pathfinding Service & Route Optimization Demo\n");
        
        // 1. Initialize the city map
        System.out.println("1. INITIALIZING CITY MAP");
        CityMap cityMap = new CityMap("city_map.csv"); // Will use default if file not found
        System.out.println("City Map: " + cityMap);
        System.out.println("Graph Connected: " + cityMap.isConnected());
        System.out.println();
        
        // 2. Initialize pathfinding service
        System.out.println("2. INITIALIZING PATHFINDING SERVICE");
        PathfindingService pathfindingService = new PathfindingService(cityMap);
        System.out.println("Pathfinding Service initialized with LRU cache (capacity: 1000)");
        System.out.println();
        
        // 3. Initialize reporting system
        System.out.println("3. INITIALIZING REPORTING SYSTEM");
        Reporting reporting = new Reporting();
        System.out.println("Reporting system initialized with sliding window analytics");
        System.out.println();
        
        // 4. Demonstrate Dijkstra's Algorithm
        System.out.println("4. DIJKSTRA'S ALGORITHM DEMONSTRATION");
        demonstrateDijkstra(cityMap);
        System.out.println();
        
        // 5. Demonstrate A* Algorithm
        System.out.println("5. A* ALGORITHM DEMONSTRATION");
        demonstrateAStar(cityMap);
        System.out.println();
        
        // 6. Demonstrate PathfindingService with caching
        System.out.println("6. PATHFINDING SERVICE WITH LRU CACHE");
        demonstratePathfindingService(pathfindingService);
        System.out.println();
        
        // 7. Simulate police responses and demonstrate reporting
        System.out.println("7. POLICE RESPONSE SIMULATION");
        simulatePoliceResponses(pathfindingService, reporting, cityMap);
        System.out.println();
        
        // 8. Generate performance report
        System.out.println("8. PERFORMANCE ANALYTICS");
        generatePerformanceReport(reporting);
        System.out.println();
        
        // 9. Demonstrate traffic updates
        System.out.println("9. DYNAMIC TRAFFIC CONDITIONS");
        demonstrateTrafficUpdates(pathfindingService, cityMap);
        System.out.println();
        
        System.out.println("=== DEMONSTRATION COMPLETE ===");
    }
    
    private static void demonstrateDijkstra(CityMap cityMap) {
        Map<Integer, CityMapNode> nodeMap = cityMap.getAllNodes();
        
        if (nodeMap.size() < 2) {
            System.out.println("Insufficient nodes for Dijkstra demonstration");
            return;
        }
        
        List<Integer> nodeIds = new ArrayList<>(nodeMap.keySet());
        int sourceNode = nodeIds.get(0);
        int targetNode = nodeIds.get(Math.min(1, nodeIds.size() - 1));
        
        System.out.printf("Finding shortest path from node %d to node %d using Dijkstra%n", 
            sourceNode, targetNode);
        
        long startTime = System.nanoTime();
        List<Integer> path = Dijkstra.findShortestPath(nodeMap, sourceNode, targetNode);
        long endTime = System.nanoTime();
        
        System.out.printf("Path found: %s%n", path);
        System.out.printf("Computation time: %.3f ms%n", (endTime - startTime) / 1_000_000.0);
        
        // Demonstrate single-source all destinations
        System.out.printf("Computing all shortest paths from node %d%n", sourceNode);
        startTime = System.nanoTime();
        Dijkstra.DijkstraResult result = Dijkstra.findShortestPaths(nodeMap, sourceNode);
        endTime = System.nanoTime();
        
        System.out.printf("All paths computed in %.3f ms%n", (endTime - startTime) / 1_000_000.0);
        
        // Show distances to first few nodes
        int count = 0;
        for (int nodeId : nodeIds) {
            if (count++ >= 3) break;
            if (nodeId != sourceNode) {
                List<Integer> pathTo = result.getPath(nodeId);
                double distance = result.getDistance(nodeId);
                System.out.printf("  To node %d: distance=%.2f, path=%s%n", 
                    nodeId, distance, pathTo);
            }
        }
    }
    
    private static void demonstrateAStar(CityMap cityMap) {
        Map<Integer, CityMapNode> nodeMap = cityMap.getAllNodes();
        
        if (nodeMap.size() < 2) {
            System.out.println("Insufficient nodes for A* demonstration");
            return;
        }
        
        List<Integer> nodeIds = new ArrayList<>(nodeMap.keySet());
        int sourceNode = nodeIds.get(0);
        int targetNode = nodeIds.get(nodeIds.size() - 1);
        
        System.out.printf("Finding shortest path from node %d to node %d using A*%n", 
            sourceNode, targetNode);
        
        // Test different heuristics
        AStar.HeuristicFunction[] heuristics = {
            AStar.EUCLIDEAN_HEURISTIC,
            AStar.MANHATTAN_HEURISTIC,
            AStar.ZERO_HEURISTIC
        };
        
        String[] heuristicNames = {"Euclidean", "Manhattan", "Zero (Dijkstra-like)"};
        
        for (int i = 0; i < heuristics.length; i++) {
            System.out.printf("\nTesting %s heuristic:\n", heuristicNames[i]);
            
            long startTime = System.nanoTime();
            AStar.AStarResult result = AStar.findPath(nodeMap, sourceNode, targetNode, heuristics[i]);
            long endTime = System.nanoTime();
            
            System.out.printf("  Path: %s%n", result.getPath());
            System.out.printf("  Distance: %.2f%n", result.getTotalDistance());
            System.out.printf("  Nodes explored: %d%n", result.getNodesExplored());
            System.out.printf("  Computation time: %.3f ms%n", (endTime - startTime) / 1_000_000.0);
        }
        
        // Demonstrate adaptive A*
        System.out.println("\nDemonstrating Adaptive A*:");
        AStar.AdaptiveAStar adaptiveAStar = new AStar.AdaptiveAStar();
        
        for (int i = 0; i < 3; i++) {
            long startTime = System.nanoTime();
            AStar.AStarResult result = adaptiveAStar.findPathAdaptive(
                nodeMap, sourceNode, targetNode, AStar.EUCLIDEAN_HEURISTIC);
            long endTime = System.nanoTime();
            
            System.out.printf("  Iteration %d: %d nodes explored, %.3f ms%n", 
                i + 1, result.getNodesExplored(), (endTime - startTime) / 1_000_000.0);
        }
    }
    
    private static void demonstratePathfindingService(PathfindingService service) {
        List<Integer> nodeIds = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));
        
        System.out.println("Testing pathfinding service with caching:");
        
        // Make several pathfinding requests
        for (int i = 0; i < 5; i++) {
            int start = nodeIds.get(i % nodeIds.size());
            int end = nodeIds.get((i + 3) % nodeIds.size());
            
            long startTime = System.nanoTime();
            PathfindingService.PathResult result = service.calculateShortestPath(start, end);
            long endTime = System.nanoTime();
            
            System.out.printf("  Request %d: %d->%d, distance=%.2f, time=%.3f ms, algorithm=%s%n",
                i + 1, start, end, result.getDistance(), 
                (endTime - startTime) / 1_000_000.0, result.getAlgorithm());
        }
        
        // Repeat some requests to show cache hits
        System.out.println("\nRepeating requests to demonstrate cache hits:");
        for (int i = 0; i < 3; i++) {
            int start = nodeIds.get(i % nodeIds.size());
            int end = nodeIds.get((i + 3) % nodeIds.size());
            
            long startTime = System.nanoTime();
            PathfindingService.PathResult result = service.calculateShortestPath(start, end);
            long endTime = System.nanoTime();
            
            System.out.printf("  Cached request: %d->%d, time=%.3f ms%n",
                start, end, (endTime - startTime) / 1_000_000.0);
        }
        
        // Show cache statistics
        PathfindingService.PathfindingStats stats = service.getPerformanceStats();
        System.out.println("\nCache Statistics:");
        System.out.printf("  Total requests: %d%n", stats.totalRequests);
        System.out.printf("  Cache hits: %d%n", stats.cacheHits);
        System.out.printf("  Cache hit rate: %.2f%%%n", stats.cacheHitRate * 100);
        System.out.printf("  Average computation time: %.3f ms%n", stats.avgComputationTimeNs / 1_000_000.0);
        
        // Demonstrate alternative paths
        System.out.println("%nFinding alternative paths:");
        List<PathfindingService.PathResult> alternatives = service.calculateAlternativePaths(0, 8, 3);
        for (int i = 0; i < alternatives.size(); i++) {
            PathfindingService.PathResult alt = alternatives.get(i);
            System.out.printf("  Alternative %d: path=%s, distance=%.2f%n", 
                i + 1, alt.getPath(), alt.getDistance());
        }
    }
    
    private static void simulatePoliceResponses(PathfindingService pathfindingService, 
                                              Reporting reporting, CityMap cityMap) {
        Random random = new Random(42); // Fixed seed for reproducible results
        String[] crimeTypes = {"Theft", "Assault", "Burglary", "Traffic", "Domestic", "Vandalism"};
        List<Integer> nodeIds = new ArrayList<>(cityMap.getNodeIds());
        
        System.out.println("Simulating 20 police responses...");
        
        for (int i = 0; i < 20; i++) {
            // Random crime location and responding unit
            int crimeLocation = nodeIds.get(random.nextInt(nodeIds.size()));
            int unitLocation = nodeIds.get(random.nextInt(nodeIds.size()));
            String crimeType = crimeTypes[random.nextInt(crimeTypes.length)];
            
            // Calculate response path
            PathfindingService.PathResult path = pathfindingService.calculateShortestPath(
                unitLocation, crimeLocation);
            
            // Simulate response time (path distance + random factor)
            double responseTime = path.getDistance() * 0.5 + random.nextGaussian() * 2.0 + 5.0;
            responseTime = Math.max(1.0, responseTime); // Minimum 1 minute
            
            // Simulate resolution (80% success rate)
            boolean resolved = random.nextDouble() < 0.8;
            
            // Record the response
            reporting.recordCrimeResponse(crimeType, unitLocation, responseTime, resolved);
            
            System.out.printf("  Response %d: %s at node %d, Unit %d responds in %.1f min (%s)%n",
                i + 1, crimeType, crimeLocation, unitLocation, responseTime, 
                resolved ? "Resolved" : "Unresolved");
            
            // Add some delay between responses
            try {
                Thread.sleep(10); // Small delay to show time progression
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    private static void generatePerformanceReport(Reporting reporting) {
        System.out.println("Generating comprehensive performance report...");
        
        // Get real-time dashboard metrics
        Map<String, Object> dashboardMetrics = reporting.getDashboardMetrics();
        System.out.println("%nReal-time Dashboard Metrics:");
        for (Map.Entry<String, Object> entry : dashboardMetrics.entrySet()) {
            System.out.printf("  %s: %s%n", entry.getKey(), entry.getValue());
        }
        
        // Generate full performance report
        Reporting.PerformanceReport report = reporting.generatePerformanceReport();
        System.out.println("%n" + report.toString());
        
        // Demonstrate prefix sum calculations
        System.out.println("Demonstrating Prefix Sum Array calculations:");
        
        // Create sample response time data
        List<Double> sampleTimes = Arrays.asList(
            5.2, 7.1, 12.3, 4.8, 9.7, 6.5, 11.2, 8.9, 5.5, 10.1
        );
        List<java.time.LocalDateTime> timestamps = new ArrayList<>();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        for (int i = 0; i < sampleTimes.size(); i++) {
            timestamps.add(now.minusMinutes(sampleTimes.size() - i));
        }
        
        Reporting.PrefixSumStats prefixStats = new Reporting.PrefixSumStats(sampleTimes, timestamps);
        
        System.out.printf("  Range 0-4 average: %.2f minutes%n", prefixStats.getRangeAverage(0, 4));
        System.out.printf("  Range 5-9 average: %.2f minutes%n", prefixStats.getRangeAverage(5, 9));
        System.out.printf("  Range 0-4 variance: %.2f%n", prefixStats.getRangeVariance(0, 4));
        
        int[] bestWindow = prefixStats.findBestPerformanceWindow(5);
        System.out.printf("  Best 5-response window: indices %d-%d, avg=%.2f%n", 
            bestWindow[0], bestWindow[1], 
            prefixStats.getRangeAverage(bestWindow[0], bestWindow[1]));
        
        // Demonstrate sliding window statistics
        System.out.println("%nSliding Window Statistics (last 60 minutes):");
        Reporting.SlidingWindowStats windowStats = new Reporting.SlidingWindowStats(60);
        
        // Add some sample data
        String[] types = {"Theft", "Assault", "Traffic"};
        for (int i = 0; i < 10; i++) {
            windowStats.addResponseTime(5.0 + i, types[i % types.length], 100 + i);
        }
        
        System.out.printf("  Current average: %.2f minutes%n", windowStats.getAverageResponseTime());
        System.out.printf("  Standard deviation: %.2f%n", windowStats.getStandardDeviation());
        System.out.printf("  Total responses: %d%n", windowStats.getCount());
        
        Map<String, Double> breakdown = windowStats.getCrimeTypeBreakdown();
        System.out.println("  Crime type breakdown:");
        for (Map.Entry<String, Double> entry : breakdown.entrySet()) {
            System.out.printf("    %s: %.2f min avg%n", entry.getKey(), entry.getValue());
        }
    }
    
    private static void demonstrateTrafficUpdates(PathfindingService service, CityMap cityMap) {
        System.out.println("Demonstrating dynamic traffic condition updates:");
        
        List<Integer> nodeIds = new ArrayList<>(cityMap.getNodeIds());
        if (nodeIds.size() < 2) return;
        
        int start = nodeIds.get(0);
        int end = nodeIds.get(nodeIds.size() - 1);
        
        // Calculate initial path
        PathfindingService.PathResult initialPath = service.calculateShortestPath(start, end);
        System.out.printf("Initial path %d->%d: distance=%.2f%n", 
            start, end, initialPath.getDistance());
        
        // Update traffic conditions (simulate heavy traffic on some nodes)
        Map<Integer, Double> trafficUpdates = new HashMap<>();
        for (int i = 0; i < Math.min(3, nodeIds.size()); i++) {
            trafficUpdates.put(nodeIds.get(i), 2.5); // 2.5x normal travel time
        }
        
        System.out.printf("Applying traffic congestion to nodes: %s%n", trafficUpdates.keySet());
        service.updateTrafficConditions(trafficUpdates);
        
        // Calculate new path
        PathfindingService.PathResult updatedPath = service.calculateShortestPath(start, end);
        System.out.printf("Updated path %d->%d: distance=%.2f%n", 
            start, end, updatedPath.getDistance());
        
        double increase = ((updatedPath.getDistance() - initialPath.getDistance()) / 
                          initialPath.getDistance()) * 100;
        System.out.printf("Distance increase due to traffic: %.1f%%%n", increase);
        
        // Show cache was cleared
        PathfindingService.PathfindingStats stats = service.getPerformanceStats();
        System.out.printf("Cache size after traffic update: %d%n", stats.cacheSize);
    }
}
