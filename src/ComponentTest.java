// ComponentTest.java
// Individual component tests to verify all DSA implementations

import algorithms.Dijkstra;
import java.util.*;

public class ComponentTest {
    
    public static void main(String[] args) {
        System.out.println("=== COMPONENT TESTING ===\n");
        
        testMinHeap();
        testLRUCache();
        testSlidingWindow();
        testPrefixSum();
        testQuickSort();
        
        System.out.println("=== ALL TESTS COMPLETED ===");
    }
    
    private static void testMinHeap() {
        System.out.println("1. TESTING CUSTOM MIN-HEAP");
        
        Dijkstra.MinHeap heap = new Dijkstra.MinHeap();
        
        // Insert elements
        heap.insert(1, 10.0);
        heap.insert(2, 5.0);
        heap.insert(3, 15.0);
        heap.insert(4, 3.0);
        
        System.out.println("Inserted: (1,10.0), (2,5.0), (3,15.0), (4,3.0)");
        
        // Extract minimum elements
        List<Double> extracted = new ArrayList<>();
        while (!heap.isEmpty()) {
            Dijkstra.MinHeap.Node node = heap.extractMin();
            extracted.add(node.distance);
            System.out.printf("Extracted: Node %d with distance %.1f%n", node.nodeId, node.distance);
        }
        
        // Verify sorted order
        boolean sorted = true;
        for (int i = 1; i < extracted.size(); i++) {
            if (extracted.get(i) < extracted.get(i-1)) {
                sorted = false;
                break;
            }
        }
        
        System.out.printf("Min-heap maintains order: %s%n%n", sorted ? "PASS" : "FAIL");
    }
    
    private static void testLRUCache() {
        System.out.println("2. TESTING LRU CACHE");
        
        PathfindingService.LRUCache<String, String> cache = 
            new PathfindingService.LRUCache<>(3);
        
        // Add items
        cache.put("A", "Value A");
        cache.put("B", "Value B");
        cache.put("C", "Value C");
        
        System.out.println("Added: A, B, C (capacity: 3)");
        System.out.printf("Cache size: %d%n", cache.size());
        
        // Access B to make it recently used
        cache.get("B");
        System.out.println("Accessed B");
        
        // Add D - should evict A (least recently used)
        cache.put("D", "Value D");
        System.out.println("Added D - should evict A");
        
        boolean hasA = cache.containsKey("A");
        boolean hasB = cache.containsKey("B");
        boolean hasC = cache.containsKey("C");
        boolean hasD = cache.containsKey("D");
        
        System.out.printf("Has A: %s, Has B: %s, Has C: %s, Has D: %s%n", hasA, hasB, hasC, hasD);
        System.out.printf("LRU eviction works: %s%n%n", (!hasA && hasB && hasC && hasD) ? "PASS" : "FAIL");
    }
    
    private static void testSlidingWindow() {
        System.out.println("3. TESTING SLIDING WINDOW");
        
        Reporting.SlidingWindowStats window = new Reporting.SlidingWindowStats(5); // 5 minute window
        
        // Add response times over time
        double[] responseTimes = {5.0, 7.0, 3.0, 9.0, 4.0, 6.0, 8.0};
        String[] crimeTypes = {"Theft", "Assault", "Burglary", "Traffic", "Domestic", "Vandalism", "Theft"};
        
        for (int i = 0; i < responseTimes.length; i++) {
            window.addResponseTime(responseTimes[i], crimeTypes[i], 100 + i);
            System.out.printf("Added: %.1f min (%s), Window avg: %.2f%n", 
                responseTimes[i], crimeTypes[i], window.getAverageResponseTime());
        }
        
        System.out.printf("Final window count: %d%n", window.getCount());
        System.out.printf("Standard deviation: %.2f%n", window.getStandardDeviation());
        
        Map<String, Double> breakdown = window.getCrimeTypeBreakdown();
        System.out.println("Crime type breakdown:");
        breakdown.forEach((type, avg) -> System.out.printf("  %s: %.2f min%n", type, avg));
        
        System.out.printf("Sliding window tracking: %s%n%n", window.getCount() > 0 ? "PASS" : "FAIL");
    }
    
    private static void testPrefixSum() {
        System.out.println("4. TESTING PREFIX SUM ARRAYS");
        
        List<Double> data = Arrays.asList(2.0, 5.0, 3.0, 8.0, 1.0, 7.0, 4.0, 6.0);
        List<java.time.LocalDateTime> timestamps = new ArrayList<>();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        
        for (int i = 0; i < data.size(); i++) {
            timestamps.add(now.minusMinutes(data.size() - i));
        }
        
        Reporting.PrefixSumStats prefixStats = new Reporting.PrefixSumStats(data, timestamps);
        
        System.out.printf("Original data: %s%n", data);
        
        // Test range queries
        double sum0to3 = prefixStats.getRangeSum(0, 3);
        double avg0to3 = prefixStats.getRangeAverage(0, 3);
        double var0to3 = prefixStats.getRangeVariance(0, 3);
        
        // Verify manually
        double expectedSum = 2.0 + 5.0 + 3.0 + 8.0;
        double expectedAvg = expectedSum / 4.0;
        
        System.out.printf("Range [0-3] sum: %.1f (expected: %.1f)%n", sum0to3, expectedSum);
        System.out.printf("Range [0-3] avg: %.2f (expected: %.2f)%n", avg0to3, expectedAvg);
        System.out.printf("Range [0-3] variance: %.2f%n", var0to3);
        
        // Test best window finding
        int[] bestWindow = prefixStats.findBestPerformanceWindow(3);
        double bestAvg = prefixStats.getRangeAverage(bestWindow[0], bestWindow[1]);
        System.out.printf("Best 3-element window: [%d-%d] with avg %.2f%n", 
            bestWindow[0], bestWindow[1], bestAvg);
        
        boolean prefixCorrect = Math.abs(sum0to3 - expectedSum) < 0.001 && 
                               Math.abs(avg0to3 - expectedAvg) < 0.001;
        System.out.printf("Prefix sum calculations: %s%n%n", prefixCorrect ? "PASS" : "FAIL");
    }
    
    private static void testQuickSort() {
        System.out.println("5. TESTING QUICK SORT FOR PERFORMANCE RANKING");
        
        // Create test performance entries
        Reporting.PerformanceRanking.PerformanceEntry[] entries = {
            new Reporting.PerformanceRanking.PerformanceEntry(101, 8.5, 15, 0.75, "Unit"),
            new Reporting.PerformanceRanking.PerformanceEntry(102, 5.2, 22, 0.85, "Unit"),
            new Reporting.PerformanceRanking.PerformanceEntry(103, 12.1, 8, 0.60, "Unit"),
            new Reporting.PerformanceRanking.PerformanceEntry(104, 6.8, 18, 0.90, "Unit"),
            new Reporting.PerformanceRanking.PerformanceEntry(105, 9.3, 12, 0.70, "Unit")
        };
        
        System.out.println("Original order:");
        for (int i = 0; i < entries.length; i++) {
            System.out.printf("  Unit %d: %.1f min avg, %.0f%% success%n", 
                entries[i].unitId, entries[i].avgResponseTime, entries[i].successRate * 100);
        }
        
        // Sort using quick sort
        Reporting.PerformanceRanking.quickSort(entries, 0, entries.length - 1);
        
        System.out.println("After quick sort (by performance):");
        for (int i = 0; i < entries.length; i++) {
            System.out.printf("  #%d: Unit %d: %.1f min avg, %.0f%% success%n", 
                i + 1, entries[i].unitId, entries[i].avgResponseTime, entries[i].successRate * 100);
        }
        
        // Verify sorting (should be by success rate descending, then response time ascending)
        boolean sorted = true;
        for (int i = 1; i < entries.length; i++) {
            if (entries[i-1].compareTo(entries[i]) > 0) {
                sorted = false;
                break;
            }
        }
        
        // Test top N selection
        List<Reporting.PerformanceRanking.PerformanceEntry> top3 = 
            Reporting.PerformanceRanking.getTopPerformers(entries, 3);
        
        System.out.printf("Top 3 performers: Units %d, %d, %d%n", 
            top3.get(0).unitId, top3.get(1).unitId, top3.get(2).unitId);
        
        System.out.printf("Quick sort correctness: %s%n%n", sorted ? "PASS" : "FAIL");
    }
}
