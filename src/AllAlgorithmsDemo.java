/**
 * Comprehensive demo showcasing all 4 algorithms implemented in the project:
 * 1. Dijkstra - Single-source shortest paths
 * 2. A* - Heuristic pathfinding  
 * 3. Floyd-Warshall - All-pairs shortest paths
 * 4. Hungarian Algorithm - Optimal assignment problem
 */

import algorithms.*;
import models.*;
import services.CrimeAssignmentService;
import java.util.*;
import java.sql.Timestamp;

public class AllAlgorithmsDemo {
    
    public static void main(String[] args) {
        System.out.println("=== ALL 4 ALGORITHMS DEMONSTRATION ===\n");
        
        // Initialize city map
        CityMap cityMap = new CityMap("dummy.csv"); // Will create default map
        PathfindingService pathfindingService = new PathfindingService(cityMap);
        
        System.out.println("1. DIJKSTRA ALGORITHM");
        System.out.println("=====================");
        demonstrateDijkstra(pathfindingService);
        
        System.out.println("\n2. A* ALGORITHM");
        System.out.println("===============");
        demonstrateAStar(pathfindingService);
        
        System.out.println("\n3. FLOYD-WARSHALL ALGORITHM");
        System.out.println("===========================");
        demonstrateFloydWarshall(pathfindingService);
        
        System.out.println("\n4. HUNGARIAN ALGORITHM");
        System.out.println("======================");
        demonstrateHungarianAlgorithm();
        
        System.out.println("\n=== ALL ALGORITHMS SUCCESSFULLY DEMONSTRATED ===");
    }
    
    private static void demonstrateDijkstra(PathfindingService pathfindingService) {
        System.out.println("Finding shortest path from node 0 to node 8 using Dijkstra...");
        
        PathfindingService.PathResult result = pathfindingService.calculateShortestPath(0, 8);
        
        System.out.println("Algorithm used: " + result.getAlgorithm());
        System.out.println("Path found: " + result.getPath());
        System.out.println("Distance: " + String.format("%.2f", result.getDistance()));
        System.out.println("Computation time: " + result.getComputationTime() + "ms");
        System.out.println("Path valid: " + result.isValidPath());
    }
    
    private static void demonstrateAStar(PathfindingService pathfindingService) {
        System.out.println("Finding shortest path from node 0 to node 8 using A*...");
        
        // Force A* by using BALANCED strategy (which should prefer A*)
        PathfindingService.PathResult result = pathfindingService.calculateShortestPath(0, 8, 
            PathfindingService.OptimizationStrategy.BALANCED);
        
        System.out.println("Algorithm used: " + result.getAlgorithm());
        System.out.println("Path found: " + result.getPath());
        System.out.println("Distance: " + String.format("%.2f", result.getDistance()));
        System.out.println("Computation time: " + result.getComputationTime() + "ms");
        System.out.println("Nodes explored: " + result.getNodesExplored());
    }
    
    private static void demonstrateFloydWarshall(PathfindingService pathfindingService) {
        System.out.println("Computing all-pairs shortest paths using Floyd-Warshall...");
        
        FloydWarshall floydWarshall = pathfindingService.calculateAllPairsShortestPaths();
        
        System.out.println("Floyd-Warshall matrix computed successfully!");
        
        // Test a few specific paths
        System.out.println("Testing specific paths from Floyd-Warshall:");
        for (int start = 0; start <= 2; start++) {
            for (int end = 6; end <= 8; end++) {
                PathfindingService.PathResult result = 
                    pathfindingService.getPathFromFloydWarshall(floydWarshall, start, end);
                
                System.out.printf("  Path %d→%d: distance=%.2f, path=%s\\n", 
                    start, end, result.getDistance(), result.getPath());
            }
        }
    }
    
    private static void demonstrateHungarianAlgorithm() {
        System.out.println("Solving optimal assignment problem using Hungarian Algorithm...");
        
        // Create sample police units
        List<Unit> units = Arrays.asList(
            createUnit(1, 0, "PATROL"),     // Unit 1 at location 0
            createUnit(2, 3, "PATROL"),     // Unit 2 at location 3  
            createUnit(3, 6, "EMERGENCY")   // Unit 3 at location 6
        );
        
        // Create sample crimes
        List<Crime> crimes = Arrays.asList(
            createCrime(1, 2, "THEFT", "MEDIUM"),      // Crime 1 at location 2
            createCrime(2, 5, "ASSAULT", "HIGH"),      // Crime 2 at location 5
            createCrime(3, 8, "BURGLARY", "LOW")       // Crime 3 at location 8
        );
        
        // Use assignment service with Hungarian Algorithm
        CrimeAssignmentService assignmentService = new CrimeAssignmentService();
        List<Assignment> assignments = assignmentService.assignUnitsToCrimes(units, crimes);
        
        System.out.println("Optimal assignments found:");
        for (Assignment assignment : assignments) {
            System.out.printf("  Unit %d → Crime %d (cost: %.2f)\\n",
                assignment.getUnitId(), assignment.getCrimeId(), assignment.getResponseTime());
        }
        
        // Also demonstrate direct Hungarian Algorithm usage
        System.out.println("\\nDirect Hungarian Algorithm demonstration:");
        demonstrateDirectHungarian();
    }
    
    private static void demonstrateDirectHungarian() {
        // Create a cost matrix (3 units × 3 crimes)
        double[][] costMatrix = {
            {2.5, 4.0, 6.5},  // Unit 1 costs to crimes 1,2,3
            {3.0, 1.5, 5.0},  // Unit 2 costs to crimes 1,2,3  
            {5.5, 3.5, 2.0}   // Unit 3 costs to crimes 1,2,3
        };
        
        int[] unitIds = {1, 2, 3};
        int[] crimeIds = {1, 2, 3};
        
        List<HungarianAlgorithm.Assignment> result = 
            HungarianAlgorithm.solve(costMatrix, unitIds, crimeIds);
        
        System.out.println("Direct Hungarian Algorithm result:");
        double totalCost = 0.0;
        for (HungarianAlgorithm.Assignment assignment : result) {
            System.out.println("  " + assignment);
            totalCost += assignment.getCost();
        }
        System.out.printf("Total optimal cost: %.2f\\n", totalCost);
    }
    
    // Helper methods to create test objects
    private static Unit createUnit(int id, int location, String type) {
        Unit unit = new Unit();
        unit.setUnitId(id);
        unit.setCurrentLocationId(location);
        unit.setCapabilities(type);  // Using capabilities instead of unitType
        unit.setStatus("AVAILABLE");
        return unit;
    }
    
    private static Crime createCrime(int id, int location, String type, String severity) {
        return new Crime(id, location, new Timestamp(System.currentTimeMillis()), 
                        severity, type, "ACTIVE");
    }
}
