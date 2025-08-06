package services;

import models.*;
import java.util.*;

/**
 * Service for balancing police unit distribution across the city
 * Finds idle units and repositions them for better coverage
 * 
 * Note: This is a simplified version that works with basic data structures
 * In a full implementation, it would integrate with CityMap and PathfindingService
 */
public class UnitBalancerService {
    private Map<Integer, List<Integer>> cityGraph; // Simple adjacency list
    private Map<Integer, UnitInfo> units;
    private Map<Integer, List<Crime>> activeCrimes;
    
    // Configuration parameters
    private static final double MIN_COVERAGE_DISTANCE = 5.0; // km
    private static final int MAX_UNITS_PER_AREA = 3;
    
    public UnitBalancerService() {
        this.cityGraph = new HashMap<>();
        this.units = new HashMap<>();
        this.activeCrimes = new HashMap<>();
    }
    
    /**
     * Initialize with city graph data
     */
    public void setCityGraph(Map<Integer, List<Integer>> cityGraph) {
        this.cityGraph = cityGraph;
    }
    
    /**
     * Main method to balance unit distribution
     * Analyzes current unit positions and repositions idle units
     */
    public void balanceUnits() {
        List<UnitInfo> idleUnits = findIdleUnits();
        List<Integer> underservedAreas = identifyUnderservedAreas();
        List<Integer> overservedAreas = identifyOverservedAreas();
        
        if (idleUnits.isEmpty() && underservedAreas.isEmpty()) {
            return; // No balancing needed
        }
        
        // Strategy 1: Move idle units to underserved areas
        redistributeIdleUnits(idleUnits, underservedAreas);
        
        // Strategy 2: Redistribute units from overserved to underserved areas
        redistributeFromOverserved(overservedAreas, underservedAreas);
        
        // Strategy 3: Predictive positioning based on crime patterns
        predictivePositioning();
    }
    
    /**
     * Find all idle (available) police units
     */
    private List<UnitInfo> findIdleUnits() {
        List<UnitInfo> idleUnits = new ArrayList<>();
        
        for (UnitInfo unit : units.values()) {
            if ("Available".equalsIgnoreCase(unit.status) || 
                "Idle".equalsIgnoreCase(unit.status)) {
                idleUnits.add(unit);
            }
        }
        
        return idleUnits;
    }
    
    /**
     * Identify areas with insufficient police coverage
     */
    private List<Integer> identifyUnderservedAreas() {
        List<Integer> underservedAreas = new ArrayList<>();
        Map<Integer, Integer> areaCoverage = calculateAreaCoverage();
        
        // Check each area in the city graph
        for (Integer nodeId : cityGraph.keySet()) {
            int unitsInArea = areaCoverage.getOrDefault(nodeId, 0);
            
            // Consider area underserved if:
            // 1. No units within coverage distance
            // 2. High crime activity but low unit presence
            if (unitsInArea == 0 || 
                (hasHighCrimeActivity(nodeId) && unitsInArea < 2)) {
                underservedAreas.add(nodeId);
            }
        }
        
        return underservedAreas;
    }
    
    /**
     * Identify areas with excessive police coverage
     */
    private List<Integer> identifyOverservedAreas() {
        List<Integer> overservedAreas = new ArrayList<>();
        Map<Integer, List<UnitInfo>> areaUnits = groupUnitsByArea();
        
        for (Map.Entry<Integer, List<UnitInfo>> entry : areaUnits.entrySet()) {
            int nodeId = entry.getKey();
            List<UnitInfo> unitsInArea = entry.getValue();
            
            // Consider overserved if more than max units and low crime activity
            if (unitsInArea.size() > MAX_UNITS_PER_AREA && 
                !hasHighCrimeActivity(nodeId)) {
                overservedAreas.add(nodeId);
            }
        }
        
        return overservedAreas;
    }
    
    /**
     * Calculate coverage for each area based on unit proximity
     */
    private Map<Integer, Integer> calculateAreaCoverage() {
        Map<Integer, Integer> coverage = new HashMap<>();
        
        for (Integer nodeId : cityGraph.keySet()) {
            int unitsInRange = 0;
            
            for (UnitInfo unit : units.values()) {
                if ("Available".equalsIgnoreCase(unit.status)) {
                    double distance = calculateSimpleDistance(unit.currentLocation, nodeId);
                    
                    if (distance <= MIN_COVERAGE_DISTANCE) {
                        unitsInRange++;
                    }
                }
            }
            
            coverage.put(nodeId, unitsInRange);
        }
        
        return coverage;
    }
    
    /**
     * Simple distance calculation (placeholder for pathfinding)
     */
    private double calculateSimpleDistance(int from, int to) {
        if (from == to) return 0.0;
        // Simple heuristic: assume 1 unit distance per node difference
        return Math.abs(from - to) * 1.5; // Assume 1.5 km per node difference
    }
    
    /**
     * Group units by their current area/region
     */
    private Map<Integer, List<UnitInfo>> groupUnitsByArea() {
        Map<Integer, List<UnitInfo>> areaUnits = new HashMap<>();
        
        for (UnitInfo unit : units.values()) {
            int location = unit.currentLocation;
            areaUnits.computeIfAbsent(location, k -> new ArrayList<>()).add(unit);
        }
        
        return areaUnits;
    }
    
    /**
     * Check if an area has high crime activity
     */
    private boolean hasHighCrimeActivity(int nodeId) {
        List<Crime> crimesInArea = activeCrimes.getOrDefault(nodeId, new ArrayList<>());
        
        // Consider high activity if more than 2 active crimes
        // or recent crime pattern indicates hotspot
        return crimesInArea.size() > 2 || isHistoricalHotspot(nodeId);
    }
    
    /**
     * Check if location is a historical crime hotspot
     */
    private boolean isHistoricalHotspot(int nodeId) {
        // In a real implementation, this would check historical crime data
        // For now, simulate some hotspot areas
        return nodeId % 7 == 0; // Simple simulation
    }
    
    /**
     * Redistribute idle units to underserved areas
     */
    private void redistributeIdleUnits(List<UnitInfo> idleUnits, List<Integer> underservedAreas) {
        if (idleUnits.isEmpty() || underservedAreas.isEmpty()) {
            return;
        }
        
        // Sort underserved areas by priority (most critical first)
        underservedAreas.sort((a, b) -> {
            int crimeCountA = activeCrimes.getOrDefault(a, new ArrayList<>()).size();
            int crimeCountB = activeCrimes.getOrDefault(b, new ArrayList<>()).size();
            return Integer.compare(crimeCountB, crimeCountA); // Descending order
        });
        
        // Assign closest idle units to underserved areas
        for (int i = 0; i < Math.min(idleUnits.size(), underservedAreas.size()); i++) {
            UnitInfo unit = idleUnits.get(i);
            int targetArea = underservedAreas.get(i);
            
            repositionUnit(unit, targetArea);
        }
    }
    
    /**
     * Redistribute units from overserved to underserved areas
     */
    private void redistributeFromOverserved(List<Integer> overservedAreas, 
                                          List<Integer> underservedAreas) {
        if (overservedAreas.isEmpty() || underservedAreas.isEmpty()) {
            return;
        }
        
        Map<Integer, List<UnitInfo>> areaUnits = groupUnitsByArea();
        
        for (Integer overservedArea : overservedAreas) {
            List<UnitInfo> unitsInArea = areaUnits.get(overservedArea);
            if (unitsInArea == null || unitsInArea.size() <= MAX_UNITS_PER_AREA) {
                continue;
            }
            
            // Find available units to move
            List<UnitInfo> availableToMove = new ArrayList<>();
            for (UnitInfo unit : unitsInArea) {
                if ("Available".equalsIgnoreCase(unit.status)) {
                    availableToMove.add(unit);
                }
            }
            
            // Move excess units to underserved areas
            int excessUnits = Math.min(availableToMove.size(), 
                                     unitsInArea.size() - MAX_UNITS_PER_AREA);
            
            for (int i = 0; i < excessUnits && !underservedAreas.isEmpty(); i++) {
                UnitInfo unitToMove = availableToMove.get(i);
                int targetArea = findClosestUnderservedArea(unitToMove, underservedAreas);
                
                if (targetArea != -1) {
                    repositionUnit(unitToMove, targetArea);
                    underservedAreas.remove(Integer.valueOf(targetArea));
                }
            }
        }
    }
    
    /**
     * Predictive positioning based on time patterns and crime forecasting
     */
    private void predictivePositioning() {
        // Get current time context
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        
        // Predict high-risk areas based on time patterns
        List<Integer> predictedHotspots = predictCrimeHotspots(hour, dayOfWeek);
        
        // Position available units near predicted hotspots
        List<UnitInfo> availableUnits = findIdleUnits();
        
        for (int i = 0; i < Math.min(availableUnits.size(), predictedHotspots.size()); i++) {
            UnitInfo unit = availableUnits.get(i);
            int hotspot = predictedHotspots.get(i);
            
            // Only reposition if not already well-positioned
            double currentDistance = calculateSimpleDistance(unit.currentLocation, hotspot);
            
            if (currentDistance > MIN_COVERAGE_DISTANCE) {
                repositionUnit(unit, hotspot);
            }
        }
    }
    
    /**
     * Predict crime hotspots based on time patterns
     */
    private List<Integer> predictCrimeHotspots(int hour, int dayOfWeek) {
        List<Integer> hotspots = new ArrayList<>();
        
        // Simple time-based prediction model
        // In reality, this would use machine learning or statistical models
        
        if (hour >= 18 && hour <= 23) { // Evening hours
            // Commercial areas and entertainment districts
            hotspots.addAll(getCommercialAreas());
        } else if (hour >= 22 || hour <= 4) { // Late night/early morning
            // High-crime residential areas
            hotspots.addAll(getHighCrimeResidentialAreas());
        } else if (hour >= 7 && hour <= 9) { // Rush hour
            // Transit hubs and busy intersections
            hotspots.addAll(getTransitAreas());
        }
        
        // Weekend vs weekday patterns
        if (dayOfWeek == Calendar.FRIDAY || dayOfWeek == Calendar.SATURDAY) {
            hotspots.addAll(getEntertainmentDistricts());
        }
        
        return hotspots;
    }
    
    /**
     * Find the closest underserved area to a unit
     */
    private int findClosestUnderservedArea(UnitInfo unit, List<Integer> underservedAreas) {
        if (underservedAreas.isEmpty()) {
            return -1;
        }
        
        int closestArea = underservedAreas.get(0);
        double shortestDistance = calculateSimpleDistance(unit.currentLocation, closestArea);
        
        for (int area : underservedAreas) {
            double distance = calculateSimpleDistance(unit.currentLocation, area);
            
            if (distance < shortestDistance) {
                shortestDistance = distance;
                closestArea = area;
            }
        }
        
        return closestArea;
    }
    
    /**
     * Reposition a unit to a target location
     */
    private void repositionUnit(UnitInfo unit, int targetLocation) {
        if (unit.currentLocation == targetLocation) {
            return; // Already at target
        }
        
        // Calculate travel time and distance using simple calculation
        double distance = calculateSimpleDistance(unit.currentLocation, targetLocation);
        double travelTime = distance / 40.0; // Assume 40 km/h average speed
        
        // Update unit status
        unit.status = "Repositioning";
        unit.targetLocation = targetLocation;
        unit.estimatedArrival = System.currentTimeMillis() + (long)(travelTime * 60 * 1000);
        
        System.out.println("Repositioning Unit " + unit.unitId + 
                          " from Node " + unit.currentLocation + 
                          " to Node " + targetLocation + 
                          " (Distance: " + String.format("%.2f", distance) + " km, " +
                          "ETA: " + String.format("%.1f", travelTime) + " min)");
    }
    
    /**
     * Update unit information
     */
    public void updateUnit(int unitId, String status, int location) {
        UnitInfo unit = units.get(unitId);
        if (unit == null) {
            unit = new UnitInfo(unitId, status, location);
            units.put(unitId, unit);
        } else {
            unit.status = status;
            unit.currentLocation = location;
        }
    }
    
    /**
     * Update active crimes information
     */
    public void updateActiveCrimes(Map<Integer, List<Crime>> activeCrimes) {
        this.activeCrimes = new HashMap<>(activeCrimes);
    }
    
    // Helper methods to get area types (simplified implementation)
    private List<Integer> getCommercialAreas() {
        List<Integer> areas = new ArrayList<>();
        for (Integer nodeId : cityGraph.keySet()) {
            if (nodeId % 5 == 0) areas.add(nodeId); // Simulate commercial areas
        }
        return areas;
    }
    
    private List<Integer> getHighCrimeResidentialAreas() {
        List<Integer> areas = new ArrayList<>();
        for (Integer nodeId : cityGraph.keySet()) {
            if (nodeId % 7 == 0) areas.add(nodeId); // Simulate high-crime areas
        }
        return areas;
    }
    
    private List<Integer> getTransitAreas() {
        List<Integer> areas = new ArrayList<>();
        for (Integer nodeId : cityGraph.keySet()) {
            if (nodeId % 11 == 0) areas.add(nodeId); // Simulate transit hubs
        }
        return areas;
    }
    
    private List<Integer> getEntertainmentDistricts() {
        List<Integer> areas = new ArrayList<>();
        for (Integer nodeId : cityGraph.keySet()) {
            if (nodeId % 13 == 0) areas.add(nodeId); // Simulate entertainment areas
        }
        return areas;
    }
    
    /**
     * Get current unit distribution statistics
     */
    public BalancingStats getBalancingStats() {
        BalancingStats stats = new BalancingStats();
        
        stats.totalUnits = units.size();
        stats.idleUnits = (int) units.values().stream()
            .filter(u -> "Available".equalsIgnoreCase(u.status)).count();
        stats.busyUnits = stats.totalUnits - stats.idleUnits;
        
        Map<Integer, Integer> coverage = calculateAreaCoverage();
        stats.underservedAreas = (int) coverage.values().stream()
            .filter(count -> count == 0).count();
        stats.overservedAreas = (int) coverage.values().stream()
            .filter(count -> count > MAX_UNITS_PER_AREA).count();
        
        return stats;
    }
    
    // Inner classes
    public static class UnitInfo {
        public int unitId;
        public String status;
        public int currentLocation;
        public int targetLocation;
        public long estimatedArrival;
        
        public UnitInfo(int unitId, String status, int currentLocation) {
            this.unitId = unitId;
            this.status = status;
            this.currentLocation = currentLocation;
            this.targetLocation = currentLocation;
            this.estimatedArrival = 0;
        }
    }
    
    public static class BalancingStats {
        public int totalUnits;
        public int idleUnits;
        public int busyUnits;
        public int underservedAreas;
        public int overservedAreas;
        
        @Override
        public String toString() {
            return String.format("Units: %d total (%d idle, %d busy), Areas: %d underserved, %d overserved",
                totalUnits, idleUnits, busyUnits, underservedAreas, overservedAreas);
        }
    }
}
