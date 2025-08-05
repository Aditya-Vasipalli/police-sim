// Reporting.java
// Module 6: Reporting & Statistics with Advanced DSA
// Real-time response time tracking with sliding window algorithms
// Prefix sum arrays for fast statistical computations
// Quick sort for top-N performance reporting

import models.Stat;
import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Reporting {
    
    /**
     * Sliding Window for real-time response time tracking
     */
    public static class SlidingWindowStats {
        private final Deque<ResponseTimeEntry> window;
        private final int windowSizeMinutes;
        private double sum;
        private double sumSquares; // For variance calculation
        
        public static class ResponseTimeEntry {
            final LocalDateTime timestamp;
            final double responseTime;
            final String crimeType;
            final int unitId;
            
            public ResponseTimeEntry(double responseTime, String crimeType, int unitId) {
                this.timestamp = LocalDateTime.now();
                this.responseTime = responseTime;
                this.crimeType = crimeType;
                this.unitId = unitId;
            }
        }
        
        public SlidingWindowStats(int windowSizeMinutes) {
            this.window = new ArrayDeque<>();
            this.windowSizeMinutes = windowSizeMinutes;
            this.sum = 0.0;
            this.sumSquares = 0.0;
        }
        
        public synchronized void addResponseTime(double responseTime, String crimeType, int unitId) {
            LocalDateTime now = LocalDateTime.now();
            
            // Remove expired entries
            while (!window.isEmpty() && 
                   window.peekFirst().timestamp.isBefore(now.minusMinutes(windowSizeMinutes))) {
                ResponseTimeEntry expired = window.removeFirst();
                sum -= expired.responseTime;
                sumSquares -= expired.responseTime * expired.responseTime;
            }
            
            // Add new entry
            ResponseTimeEntry entry = new ResponseTimeEntry(responseTime, crimeType, unitId);
            window.addLast(entry);
            sum += responseTime;
            sumSquares += responseTime * responseTime;
        }
        
        public double getAverageResponseTime() {
            return window.isEmpty() ? 0.0 : sum / window.size();
        }
        
        public double getStandardDeviation() {
            if (window.size() < 2) return 0.0;
            
            double mean = getAverageResponseTime();
            double variance = (sumSquares / window.size()) - (mean * mean);
            return Math.sqrt(Math.max(0, variance));
        }
        
        public int getCount() {
            return window.size();
        }
        
        public Map<String, Double> getCrimeTypeBreakdown() {
            Map<String, List<Double>> crimeGroups = new HashMap<>();
            
            for (ResponseTimeEntry entry : window) {
                crimeGroups.computeIfAbsent(entry.crimeType, k -> new ArrayList<>())
                          .add(entry.responseTime);
            }
            
            Map<String, Double> breakdown = new HashMap<>();
            for (Map.Entry<String, List<Double>> entry : crimeGroups.entrySet()) {
                double avg = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                breakdown.put(entry.getKey(), avg);
            }
            
            return breakdown;
        }
    }
    
    /**
     * Prefix Sum Array for fast range queries on statistical data
     */
    public static class PrefixSumStats {
        private final List<Double> originalData;
        private final double[] prefixSums;
        private final double[] prefixSumSquares;
        private final LocalDateTime[] timestamps;
        
        public PrefixSumStats(List<Double> data, List<LocalDateTime> timestamps) {
            this.originalData = new ArrayList<>(data);
            this.prefixSums = new double[data.size() + 1];
            this.prefixSumSquares = new double[data.size() + 1];
            this.timestamps = timestamps.toArray(new LocalDateTime[0]);
            
            buildPrefixSums();
        }
        
        private void buildPrefixSums() {
            for (int i = 0; i < originalData.size(); i++) {
                double value = originalData.get(i);
                prefixSums[i + 1] = prefixSums[i] + value;
                prefixSumSquares[i + 1] = prefixSumSquares[i] + value * value;
            }
        }
        
        /**
         * Fast range sum query O(1)
         */
        public double getRangeSum(int start, int end) {
            if (start < 0 || end >= prefixSums.length || start > end) return 0.0;
            return prefixSums[end + 1] - prefixSums[start];
        }
        
        /**
         * Fast range average query O(1)
         */
        public double getRangeAverage(int start, int end) {
            if (start > end) return 0.0;
            double sum = getRangeSum(start, end);
            return sum / (end - start + 1);
        }
        
        /**
         * Fast range variance query O(1)
         */
        public double getRangeVariance(int start, int end) {
            if (start > end || start < 0 || end >= prefixSumSquares.length) return 0.0;
            
            int count = end - start + 1;
            double sum = getRangeSum(start, end);
            double sumSquares = prefixSumSquares[end + 1] - prefixSumSquares[start];
            
            double mean = sum / count;
            return (sumSquares / count) - (mean * mean);
        }
        
        /**
         * Find best performing time window
         */
        public int[] findBestPerformanceWindow(int windowSize) {
            if (windowSize > originalData.size()) return new int[]{0, 0};
            
            double bestAverage = Double.MAX_VALUE;
            int bestStart = 0;
            
            for (int i = 0; i <= originalData.size() - windowSize; i++) {
                double avg = getRangeAverage(i, i + windowSize - 1);
                if (avg < bestAverage) {
                    bestAverage = avg;
                    bestStart = i;
                }
            }
            
            return new int[]{bestStart, bestStart + windowSize - 1};
        }
    }
    
    /**
     * Quick Sort implementation for top-N performance reporting
     */
    public static class PerformanceRanking {
        
        public static class PerformanceEntry implements Comparable<PerformanceEntry> {
            final int unitId;
            final double avgResponseTime;
            final int totalCases;
            final double successRate;
            final String unitType;
            
            public PerformanceEntry(int unitId, double avgResponseTime, int totalCases, 
                                  double successRate, String unitType) {
                this.unitId = unitId;
                this.avgResponseTime = avgResponseTime;
                this.totalCases = totalCases;
                this.successRate = successRate;
                this.unitType = unitType;
            }
            
            @Override
            public int compareTo(PerformanceEntry other) {
                // Primary: success rate (descending)
                int successCompare = Double.compare(other.successRate, this.successRate);
                if (successCompare != 0) return successCompare;
                
                // Secondary: response time (ascending)
                return Double.compare(this.avgResponseTime, other.avgResponseTime);
            }
            
            public double getPerformanceScore() {
                // Weighted performance score
                return (successRate * 0.7) + ((1.0 / (1.0 + avgResponseTime)) * 0.3);
            }
        }
        
        /**
         * Quick Sort with optimizations for performance data
         */
        public static void quickSort(PerformanceEntry[] arr, int low, int high) {
            if (low < high) {
                // Use median-of-three pivot selection for better performance
                int pivotIndex = medianOfThreePivot(arr, low, high);
                swap(arr, pivotIndex, high);
                
                int partitionIndex = partition(arr, low, high);
                
                quickSort(arr, low, partitionIndex - 1);
                quickSort(arr, partitionIndex + 1, high);
            }
        }
        
        private static int medianOfThreePivot(PerformanceEntry[] arr, int low, int high) {
            int mid = low + (high - low) / 2;
            
            if (arr[mid].compareTo(arr[low]) < 0) swap(arr, low, mid);
            if (arr[high].compareTo(arr[low]) < 0) swap(arr, low, high);
            if (arr[high].compareTo(arr[mid]) < 0) swap(arr, mid, high);
            
            return mid;
        }
        
        private static int partition(PerformanceEntry[] arr, int low, int high) {
            PerformanceEntry pivot = arr[high];
            int i = low - 1;
            
            for (int j = low; j < high; j++) {
                if (arr[j].compareTo(pivot) <= 0) {
                    i++;
                    swap(arr, i, j);
                }
            }
            
            swap(arr, i + 1, high);
            return i + 1;
        }
        
        private static void swap(PerformanceEntry[] arr, int i, int j) {
            PerformanceEntry temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
        
        /**
         * Get top N performing units
         */
        public static List<PerformanceEntry> getTopPerformers(PerformanceEntry[] entries, int n) {
            PerformanceEntry[] copy = Arrays.copyOf(entries, entries.length);
            quickSort(copy, 0, copy.length - 1);
            
            List<PerformanceEntry> topPerformers = new ArrayList<>();
            for (int i = 0; i < Math.min(n, copy.length); i++) {
                topPerformers.add(copy[i]);
            }
            
            return topPerformers;
        }
    }
    
    // Main Reporting Class Implementation
    private final SlidingWindowStats responseTimeWindow;
    private final List<String> eventLog;
    private final Map<String, Integer> crimeTypeCounts;
    private final Map<Integer, List<Double>> unitResponseTimes;
    private final List<Double> allResponseTimes;
    private final List<LocalDateTime> responseTimestamps;
    
    // Performance tracking
    private long totalEvents;
    private long totalCrimesReported;
    private long totalCrimesResolved;
    
    public Reporting() {
        this.responseTimeWindow = new SlidingWindowStats(60); // 1-hour window
        this.eventLog = new ArrayList<>();
        this.crimeTypeCounts = new HashMap<>();
        this.unitResponseTimes = new HashMap<>();
        this.allResponseTimes = new ArrayList<>();
        this.responseTimestamps = new ArrayList<>();
        this.totalEvents = 0;
        this.totalCrimesReported = 0;
        this.totalCrimesResolved = 0;
    }
    
    /**
     * Log events with timestamp
     */
    public void logEvent(String event) {
        String timestampedEvent = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) 
                                + " - " + event;
        eventLog.add(timestampedEvent);
        totalEvents++;
    }
    
    /**
     * Record crime response data
     */
    public void recordCrimeResponse(String crimeType, int unitId, double responseTime, boolean resolved) {
        // Update sliding window
        responseTimeWindow.addResponseTime(responseTime, crimeType, unitId);
        
        // Update historical data
        allResponseTimes.add(responseTime);
        responseTimestamps.add(LocalDateTime.now());
        
        // Update crime type counts
        crimeTypeCounts.merge(crimeType, 1, Integer::sum);
        
        // Update unit performance
        unitResponseTimes.computeIfAbsent(unitId, k -> new ArrayList<>()).add(responseTime);
        
        // Update counters
        totalCrimesReported++;
        if (resolved) totalCrimesResolved++;
        
        // Log the event
        logEvent(String.format("Crime response: Type=%s, Unit=%d, Time=%.2f min, Resolved=%s", 
                crimeType, unitId, responseTime, resolved));
    }
    
    /**
     * Generate comprehensive performance report
     */
    public PerformanceReport generatePerformanceReport() {
        // Real-time metrics from sliding window
        double currentAvgResponseTime = responseTimeWindow.getAverageResponseTime();
        double currentStdDev = responseTimeWindow.getStandardDeviation();
        Map<String, Double> crimeTypeBreakdown = responseTimeWindow.getCrimeTypeBreakdown();
        
        // Historical analysis using prefix sums
        PrefixSumStats prefixStats = new PrefixSumStats(allResponseTimes, responseTimestamps);
        
        // Find best performance period (last 100 entries or all if less)
        int windowSize = Math.min(100, allResponseTimes.size());
        int[] bestWindow = windowSize > 0 ? prefixStats.findBestPerformanceWindow(windowSize) : new int[]{0, 0};
        
        // Unit performance ranking
        List<PerformanceRanking.PerformanceEntry> unitPerformances = calculateUnitPerformances();
        List<PerformanceRanking.PerformanceEntry> topPerformers = 
            PerformanceRanking.getTopPerformers(
                unitPerformances.toArray(new PerformanceRanking.PerformanceEntry[0]), 10);
        
        return new PerformanceReport(
            currentAvgResponseTime,
            currentStdDev,
            crimeTypeBreakdown,
            totalCrimesReported,
            totalCrimesResolved,
            (double) totalCrimesResolved / Math.max(1, totalCrimesReported),
            topPerformers,
            bestWindow,
            windowSize > 0 ? prefixStats.getRangeAverage(bestWindow[0], bestWindow[1]) : 0.0
        );
    }
    
    /**
     * Export detailed statistics to CSV
     */
    public void exportToCSV(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Timestamp,Event");
            
            for (String event : eventLog) {
                writer.println("\"" + event.replace("\"", "\"\"") + "\"");
            }
            
            // Add summary statistics
            writer.println("\nSUMMARY STATISTICS");
            writer.println("Total Events," + totalEvents);
            writer.println("Total Crimes Reported," + totalCrimesReported);
            writer.println("Total Crimes Resolved," + totalCrimesResolved);
            writer.println("Resolution Rate," + 
                String.format("%.2f%%", (double) totalCrimesResolved / Math.max(1, totalCrimesReported) * 100));
            writer.println("Current Avg Response Time," + 
                String.format("%.2f minutes", responseTimeWindow.getAverageResponseTime()));
        }
    }
    
    /**
     * Real-time dashboard data
     */
    public Map<String, Object> getDashboardMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        metrics.put("currentResponseTime", responseTimeWindow.getAverageResponseTime());
        metrics.put("responseTimeStdDev", responseTimeWindow.getStandardDeviation());
        metrics.put("activeCases", responseTimeWindow.getCount());
        metrics.put("resolutionRate", (double) totalCrimesResolved / Math.max(1, totalCrimesReported));
        metrics.put("crimeTypeBreakdown", responseTimeWindow.getCrimeTypeBreakdown());
        metrics.put("totalEventsToday", totalEvents);
        
        return metrics;
    }
    
    // Private helper methods
    
    private List<PerformanceRanking.PerformanceEntry> calculateUnitPerformances() {
        List<PerformanceRanking.PerformanceEntry> performances = new ArrayList<>();
        
        for (Map.Entry<Integer, List<Double>> entry : unitResponseTimes.entrySet()) {
            int unitId = entry.getKey();
            List<Double> responseTimes = entry.getValue();
            
            double avgResponseTime = responseTimes.stream()
                .mapToDouble(Double::doubleValue)
                .average().orElse(0.0);
            
            int totalCases = responseTimes.size();
            
            // Simple success rate calculation (could be enhanced with actual resolution data)
            double successRate = Math.max(0.5, 1.0 - (avgResponseTime / 30.0)); // Normalize to 30 min max
            
            performances.add(new PerformanceRanking.PerformanceEntry(
                unitId, avgResponseTime, totalCases, successRate, "Police Unit"));
        }
        
        return performances;
    }
    
    /**
     * Performance report data container
     */
    public static class PerformanceReport {
        public final double currentAvgResponseTime;
        public final double currentStdDev;
        public final Map<String, Double> crimeTypeBreakdown;
        public final long totalCrimesReported;
        public final long totalCrimesResolved;
        public final double resolutionRate;
        public final List<PerformanceRanking.PerformanceEntry> topPerformers;
        public final int[] bestPerformanceWindow;
        public final double bestWindowAverage;
        
        public PerformanceReport(double currentAvgResponseTime, double currentStdDev,
                               Map<String, Double> crimeTypeBreakdown, long totalCrimesReported,
                               long totalCrimesResolved, double resolutionRate,
                               List<PerformanceRanking.PerformanceEntry> topPerformers,
                               int[] bestPerformanceWindow, double bestWindowAverage) {
            this.currentAvgResponseTime = currentAvgResponseTime;
            this.currentStdDev = currentStdDev;
            this.crimeTypeBreakdown = crimeTypeBreakdown;
            this.totalCrimesReported = totalCrimesReported;
            this.totalCrimesResolved = totalCrimesResolved;
            this.resolutionRate = resolutionRate;
            this.topPerformers = topPerformers;
            this.bestPerformanceWindow = bestPerformanceWindow;
            this.bestWindowAverage = bestWindowAverage;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== POLICE RESPONSE PERFORMANCE REPORT ===\n");
            sb.append(String.format("Current Avg Response Time: %.2f minutes\n", currentAvgResponseTime));
            sb.append(String.format("Standard Deviation: %.2f minutes\n", currentStdDev));
            sb.append(String.format("Total Crimes: %d (Resolved: %d, Rate: %.1f%%)\n", 
                totalCrimesReported, totalCrimesResolved, resolutionRate * 100));
            
            sb.append("\nCrime Type Breakdown:\n");
            crimeTypeBreakdown.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue())
                .forEach(entry -> sb.append(String.format("  %s: %.2f min avg\n", 
                    entry.getKey(), entry.getValue())));
            
            sb.append("\nTop Performing Units:\n");
            for (int i = 0; i < Math.min(5, topPerformers.size()); i++) {
                PerformanceRanking.PerformanceEntry unit = topPerformers.get(i);
                sb.append(String.format("  #%d: Unit %d (%.2f min avg, %.1f%% success)\n",
                    i + 1, unit.unitId, unit.avgResponseTime, unit.successRate * 100));
            }
            
            return sb.toString();
        }
    }
}
