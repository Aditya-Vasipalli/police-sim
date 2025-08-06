// PoliceManager.java
// Module 2: Police Management
// Manages police stations, units, and their states

// Expected Output Example:
// - Initializes police stations and units
// - Tracks and updates unit status
// - Example usage:
//   PoliceManager pm = new PoliceManager(cityMap);
//   pm.assignUnitToCrime(unitId, crimeId);
//   pm.updateUnitLocation(unitId, nodeId);
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import models.Unit;
import models.Assignment;
import models.Crime;
import algorithms.FloydWarshall;
import algorithms.HungarianAlgorithm;

public class PoliceManager {
    private CityMap cityMap;
    private Map<Integer, Unit> allUnits;
    private Map<Integer, Assignment> activeAssignments;
    private Map<Integer, Integer> crimeLocations; // crimeId -> locationId
    private Map<Integer, Set<Integer>> assignedUnits; // crimeId -> set of unitIds
    private List<Assignment> completedAssignments;
    private int totalAssignmentsMade = 0; // Track total assignments for statistics
    private List<Crime> pendingCrimes; // Queue for crimes waiting for available units
    private PathfindingService pathfindingService;
    private FloydWarshall floydWarshall;
    
    // Unit specialization constants
    private static final Map<String, String[]> CRIME_UNIT_MAPPING = new HashMap<>();
    
    static {
        // Bomb and explosive crimes
        CRIME_UNIT_MAPPING.put("BOMB", new String[]{"BOMB_SQUAD", "SWAT", "EMERGENCY"});
        CRIME_UNIT_MAPPING.put("EXPLOSIVE", new String[]{"BOMB_SQUAD", "SWAT"});
        CRIME_UNIT_MAPPING.put("TERRORISM", new String[]{"SWAT", "BOMB_SQUAD", "EMERGENCY"});
        
        // High-risk crimes
        CRIME_UNIT_MAPPING.put("HOSTAGE", new String[]{"SWAT", "HOSTAGE_NEGOTIATION", "EMERGENCY"});
        CRIME_UNIT_MAPPING.put("ARMED_ROBBERY", new String[]{"SWAT", "PATROL", "EMERGENCY"});
        CRIME_UNIT_MAPPING.put("ARMED", new String[]{"SWAT", "PATROL", "EMERGENCY"});
        CRIME_UNIT_MAPPING.put("ROBBERY", new String[]{"PATROL", "SWAT", "EMERGENCY"});
        CRIME_UNIT_MAPPING.put("RIOT", new String[]{"RIOT_CONTROL", "SWAT", "PATROL"});
        
        // Investigation crimes
        CRIME_UNIT_MAPPING.put("CYBER", new String[]{"CYBER_CRIME", "DETECTIVE"});
        CRIME_UNIT_MAPPING.put("FRAUD", new String[]{"DETECTIVE", "CYBER_CRIME", "PATROL"});
        CRIME_UNIT_MAPPING.put("MURDER", new String[]{"DETECTIVE", "SWAT", "EMERGENCY"});
        
        // Drug crimes
        CRIME_UNIT_MAPPING.put("DRUG", new String[]{"K9", "PATROL", "DETECTIVE"});
        CRIME_UNIT_MAPPING.put("NARCOTIC", new String[]{"K9", "PATROL", "DETECTIVE"});
        
        // Traffic crimes
        CRIME_UNIT_MAPPING.put("TRAFFIC", new String[]{"TRAFFIC", "PATROL"});
        CRIME_UNIT_MAPPING.put("DUI", new String[]{"TRAFFIC", "PATROL"});
        CRIME_UNIT_MAPPING.put("VEHICLE", new String[]{"TRAFFIC", "PATROL"});
        
        // Violent crimes
        CRIME_UNIT_MAPPING.put("ASSAULT", new String[]{"PATROL", "EMERGENCY", "SWAT"});
        CRIME_UNIT_MAPPING.put("DOMESTIC", new String[]{"PATROL", "EMERGENCY", "SWAT"});
        CRIME_UNIT_MAPPING.put("VIOLENCE", new String[]{"PATROL", "EMERGENCY", "SWAT"});
        
        // Property crimes
        CRIME_UNIT_MAPPING.put("BURGLARY", new String[]{"PATROL", "DETECTIVE", "EMERGENCY"});
        CRIME_UNIT_MAPPING.put("THEFT", new String[]{"PATROL", "DETECTIVE", "EMERGENCY"});
        CRIME_UNIT_MAPPING.put("VANDALISM", new String[]{"PATROL", "EMERGENCY"});
        
        // Emergency situations
        CRIME_UNIT_MAPPING.put("EMERGENCY", new String[]{"EMERGENCY", "PATROL", "SWAT"});
        
        // Default mapping for unmatched crimes
        CRIME_UNIT_MAPPING.put("DEFAULT", new String[]{"PATROL", "EMERGENCY"});
    }
    
    public PoliceManager(CityMap cityMap) {
        this(cityMap, null); // Use null for default initialization
    }
    
    public PoliceManager(CityMap cityMap, List<Integer> policeStationLocations) {
        this.cityMap = cityMap;
        this.allUnits = new HashMap<>();
        this.activeAssignments = new HashMap<>();
        this.assignedUnits = new HashMap<>();
        this.crimeLocations = new HashMap<>();
        this.completedAssignments = new ArrayList<>();
        this.pendingCrimes = new ArrayList<>();
        this.pathfindingService = new PathfindingService(cityMap);
        
        // Initialize algorithms
        initializeAlgorithms();
        
        // Initialize police units using specific station locations or optimal placement
        if (policeStationLocations != null && !policeStationLocations.isEmpty()) {
            initializePoliceUnitsAtStations(policeStationLocations);
        } else {
            initializePoliceUnitsOptimally();
        }
        System.out.println("PoliceManager initialized with " + allUnits.size() + " specialized units");
        
        // Print unit distribution
        printUnitDistribution();
    }
    
    /**
     * Initialize all pathfinding and optimization algorithms
     */
    private void initializeAlgorithms() {
        // Build graph from CityMap for algorithms
        models.Graph graph = new models.Graph();
        for (Integer nodeId : cityMap.getNodeIds()) {
            for (Integer neighbor : cityMap.getNeighbors(nodeId)) {
                double weight = cityMap.getEdgeWeight(nodeId, neighbor);
                graph.addEdge(nodeId, neighbor, weight);
            }
        }
        
        // Initialize Floyd-Warshall for strategic placement analysis
        this.floydWarshall = new FloydWarshall(graph);
        
        System.out.println("All pathfinding algorithms initialized successfully");
    }
    
    /**
     * Initialize police units at specific police station locations
     */
    private void initializePoliceUnitsAtStations(List<Integer> stationLocations) {
        System.out.println("Initializing police units at " + stationLocations.size() + " station locations: " + stationLocations);
        
        int totalNodes = cityMap.getNodeIds().size();
        int unitId = 1;
        
        // Calculate total units to create based on map size
        int totalUnitsNeeded = Math.max(10, totalNodes / 5); // At least 10 units, scale with map size
        
        // Distribute units across different types
        unitId = createUnitsAtStations("PATROL", stationLocations, Math.max(3, totalUnitsNeeded / 3), unitId);
        unitId = createUnitsAtStations("EMERGENCY", stationLocations, Math.max(2, totalUnitsNeeded / 5), unitId);
        unitId = createUnitsAtStations("SWAT", stationLocations, Math.max(2, totalUnitsNeeded / 6), unitId);
        unitId = createUnitsAtStations("DETECTIVE", stationLocations, Math.max(1, totalUnitsNeeded / 8), unitId);
        unitId = createUnitsAtStations("BOMB_SQUAD", stationLocations, Math.max(1, totalUnitsNeeded / 10), unitId);
        unitId = createUnitsAtStations("TRAFFIC", stationLocations, Math.max(1, totalUnitsNeeded / 8), unitId);
        unitId = createUnitsAtStations("K9", stationLocations, Math.max(1, totalUnitsNeeded / 10), unitId);
        unitId = createUnitsAtStations("RIOT_CONTROL", stationLocations, Math.max(1, totalUnitsNeeded / 12), unitId);
        unitId = createUnitsAtStations("HOSTAGE_NEGOTIATION", stationLocations, Math.max(1, totalUnitsNeeded / 15), unitId);
        unitId = createUnitsAtStations("CYBER_CRIME", stationLocations, Math.max(1, totalUnitsNeeded / 15), unitId);
        
        System.out.println("Created " + (unitId - 1) + " total units distributed across " + stationLocations.size() + " stations");
    }
    
    /**
     * Create units of a specific type distributed across police stations
     */
    private int createUnitsAtStations(String unitType, List<Integer> stationLocations, int count, int startingId) {
        int currentId = startingId;
        for (int i = 0; i < count; i++) {
            int stationLocation = stationLocations.get(i % stationLocations.size());
            Unit unit = new Unit(currentId++, stationLocation, "AVAILABLE", unitType);
            allUnits.put(unit.getUnitId(), unit);
            System.out.println("  Created Unit " + unit.getUnitId() + " (" + unitType + ") at station location " + stationLocation);
        }
        return currentId;
    }
    
    /**
     * Initialize police units at optimal locations using Floyd-Warshall analysis
     */
    private void initializePoliceUnitsOptimally() {
        List<Integer> nodes = new ArrayList<>(cityMap.getNodeIds());
        int totalNodes = nodes.size();
        
        // Use Floyd-Warshall to find optimal strategic locations
        List<Integer> strategicLocations = findStrategicLocations(nodes);
        
        int unitId = 1;
        
        // Initialize different unit types with strategic distribution
        unitId = createUnitsOfType("PATROL", strategicLocations, Math.max(2, totalNodes / 2), unitId);
        unitId = createUnitsOfType("SWAT", strategicLocations, Math.max(1, totalNodes / 4), unitId);
        unitId = createUnitsOfType("BOMB_SQUAD", strategicLocations, Math.max(1, totalNodes / 6), unitId);
        unitId = createUnitsOfType("EMERGENCY", strategicLocations, Math.max(1, totalNodes / 3), unitId);
        unitId = createUnitsOfType("DETECTIVE", strategicLocations, Math.max(1, totalNodes / 5), unitId);
        unitId = createUnitsOfType("K9", strategicLocations, Math.max(1, totalNodes / 8), unitId);
        unitId = createUnitsOfType("TRAFFIC", strategicLocations, Math.max(1, totalNodes / 6), unitId);
        unitId = createUnitsOfType("RIOT_CONTROL", strategicLocations, Math.max(1, totalNodes / 10), unitId);
        unitId = createUnitsOfType("HOSTAGE_NEGOTIATION", strategicLocations, Math.max(1, totalNodes / 12), unitId);
        unitId = createUnitsOfType("CYBER_CRIME", strategicLocations, Math.max(1, totalNodes / 15), unitId);
    }
    
    /**
     * Create units of a specific type at strategic locations
     */
    private int createUnitsOfType(String unitType, List<Integer> locations, int count, int startingId) {
        int currentId = startingId;
        for (int i = 0; i < count; i++) {
            int locationId = locations.get(i % locations.size());
            Unit unit = new Unit(currentId++, locationId, "AVAILABLE", unitType);
            allUnits.put(unit.getUnitId(), unit);
        }
        return currentId;
    }
    
    /**
     * Find strategic locations using Floyd-Warshall centrality analysis
     */
    private List<Integer> findStrategicLocations(List<Integer> allNodes) {
        List<Integer> strategic = new ArrayList<>();
        Map<Integer, Double> centralityScores = new HashMap<>();
        
        // Calculate centrality scores using Floyd-Warshall results
        for (Integer node : allNodes) {
            double totalDistance = 0;
            int reachableNodes = 0;
            
            for (Integer other : allNodes) {
                if (!node.equals(other)) {
                    double distance = floydWarshall.getShortestDistance(node, other);
                    if (distance < Double.MAX_VALUE) {
                        totalDistance += distance;
                        reachableNodes++;
                    }
                }
            }
            
            // Lower average distance = higher centrality = better strategic location
            double centrality = reachableNodes > 0 ? totalDistance / reachableNodes : Double.MAX_VALUE;
            centralityScores.put(node, centrality);
        }
        
        // Sort nodes by centrality (lower scores = more central)
        strategic.addAll(allNodes);
        strategic.sort((a, b) -> Double.compare(centralityScores.get(a), centralityScores.get(b)));
        
        return strategic;
    }
    
    /**
     * Print distribution of unit types for debugging
     */
    private void printUnitDistribution() {
        Map<String, Integer> distribution = new HashMap<>();
        for (Unit unit : allUnits.values()) {
            distribution.merge(unit.getCapabilities(), 1, Integer::sum);
        }
        
        System.out.println("=== UNIT DISTRIBUTION ===");
        for (Map.Entry<String, Integer> entry : distribution.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " units");
        }
        System.out.println("========================");
    }
    
    /**
     * Handle new crime by finding appropriate units (assignment logic moved to SimulatorCore)
     */
    public void handleNewCrime(Crime crime) {
        System.out.println("PoliceManager.handleNewCrime: Received crime " + crime.getCrimeId() + 
                          " (" + crime.getType() + ") at location " + crime.getLocationId());
        
        // Store crime location for later reference
        crimeLocations.put(crime.getCrimeId(), crime.getLocationId());
        
        // Check if crime is already being handled
        if (activeAssignments.containsKey(crime.getCrimeId())) {
            System.out.println("Crime " + crime.getCrimeId() + " already has active assignment");
            return;
        }
        
        // Add to pending crimes - assignment logic now handled by SimulatorCore
        pendingCrimes.add(crime);
        System.out.println("Crime " + crime.getCrimeId() + " added to pending queue for SimulatorCore assignment");
    }
    
    /**
     * Find suitable units for a crime based on specialization
     */
    /**
     * Find suitable units for a crime (public for SimulatorCore access)
     */
    public List<Unit> findSuitableUnits(Crime crime) {
        List<Unit> suitable = new ArrayList<>();
        String[] requiredCapabilities = getRequiredCapabilities(crime.getType());
        
        System.out.println("Finding suitable units for crime " + crime.getCrimeId() + " (" + crime.getType() + ")");
        System.out.println("Required capabilities: " + java.util.Arrays.toString(requiredCapabilities));
        
        for (Unit unit : allUnits.values()) {
            if (!unit.getStatus().equals("AVAILABLE")) continue;
            
            // Check if unit has required capabilities
            for (String capability : requiredCapabilities) {
                if (unit.getCapabilities().equals(capability)) {
                    suitable.add(unit);
                    System.out.println("  Found suitable unit: " + unit.getUnitId() + " (" + unit.getCapabilities() + ")");
                    break; // Found matching capability
                }
            }
        }
        
        System.out.println("Total suitable units found: " + suitable.size());
        return suitable;
    }
    
    /**
     * Get required unit capabilities for a crime type
     */
    private String[] getRequiredCapabilities(String crimeType) {
        // Check for specific crime type mappings
        for (Map.Entry<String, String[]> entry : CRIME_UNIT_MAPPING.entrySet()) {
            if (crimeType.toUpperCase().contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        // Default to patrol units
        return CRIME_UNIT_MAPPING.get("DEFAULT");
    }
    
    // Hungarian algorithm logic moved to SimulatorCore
    
    /**
     * Get pending crimes for SimulatorCore assignment logic
     */
    public List<Crime> getPendingCrimes() {
        return new ArrayList<>(pendingCrimes);
    }
    
    /**
     * Remove crime from pending list (called by SimulatorCore after assignment)
     */
    public void removePendingCrime(Crime crime) {
        pendingCrimes.remove(crime);
    }
    
    /**
     * Get units at a specific location (for station-based assignment)
     */
    public List<Unit> getUnitsAtLocation(int locationId) {
        List<Unit> unitsAtLocation = new ArrayList<>();
        
        for (Unit unit : allUnits.values()) {
            if (unit.getCurrentLocationId() == locationId && unit.getStatus().equals("Available")) {
                unitsAtLocation.add(unit);
            }
        }
        
        return unitsAtLocation;
    }
    
    /**
     * Dispatch a specific unit to a crime (public for SimulatorCore access)
     */
    public void dispatchUnitToCrime(Unit unit, Crime crime, int currentTick) {
        // Create assignment record with tick information
        Assignment assignment = new Assignment(
            crime.getCrimeId(), 
            unit.getUnitId(), 
            crime.getCrimeId(), 
            new java.sql.Timestamp(System.currentTimeMillis()), 
            0.0,
            currentTick
        );
        
        // Record the assignment
        activeAssignments.put(crime.getCrimeId(), assignment);
        totalAssignmentsMade++; // Increment assignment counter
        
        // Initialize assigned units set for this crime
        assignedUnits.computeIfAbsent(crime.getCrimeId(), k -> new HashSet<>()).add(unit.getUnitId());
        
        // Update unit status and add assignment
        unit.setStatus("DISPATCHED");
        unit.addAssignment(crime.getCrimeId()); // Track assignment and update fatigue
        
        // Calculate police navigation route using A* with traffic awareness
        var policeRoute = pathfindingService.calculatePoliceNavigationPath(
            unit.getCurrentLocationId(), crime.getLocationId());
        
        System.out.println("Dispatched Unit " + unit.getUnitId() + " (" + unit.getCapabilities() + 
                         ", " + unit.getExperienceLevel() + ", fatigue: " + 
                         String.format("%.2f", unit.getFatigueLevel()) + ") to crime " + 
                         crime.getCrimeId() + " at location " + crime.getLocationId());
        
        if (policeRoute.isValidPath()) {
            System.out.println("  -> Route calculated using " + policeRoute.getAlgorithm() + 
                             ", distance: " + String.format("%.2f", policeRoute.getDistance()) + 
                             ", computation time: " + policeRoute.getComputationTime() / 1000000.0 + "ms");
        }
        
        // Call the legacy dispatch method for compatibility
        dispatchUnit(unit.getUnitId(), crime.getCrimeId());
    }
    
    /**
     * Get total number of assignments made for statistics
     */
    public int getTotalAssignmentsMade() {
        return totalAssignmentsMade;
    }
    
    /**
     * Get average response time from completed assignments
     */
    public double getAverageResponseTime() {
        if (completedAssignments.isEmpty()) {
            return 0.0;
        }
        
        return completedAssignments.stream()
            .mapToDouble(Assignment::getResponseTime)
            .average().orElse(0.0);
    }
    
    /**
     * Enhanced crime type checking with comprehensive specializations
     */
    /**
     * Check if a unit can handle a specific crime type (public for SimulatorCore access)
     */
    public boolean canHandleCrime(Unit unit, Crime crime) {
        String crimeType = crime.getType().toUpperCase();
        String capability = unit.getCapabilities();
        
        // Bomb squad required for explosive-related crimes
        if (crimeType.contains("BOMB") || crimeType.contains("EXPLOSIVE")) {
            return capability.equals("BOMB_SQUAD");
        }
        
        // SWAT for high-risk situations
        if (crimeType.contains("ARMED") || crimeType.contains("HOSTAGE") || 
            crimeType.contains("TERRORISM") || crimeType.contains("ACTIVE_SHOOTER")) {
            return capability.equals("SWAT") || capability.equals("EMERGENCY");
        }
        
        // K9 units for drug-related crimes
        if (crimeType.contains("DRUG") || crimeType.contains("NARCOTIC")) {
            return capability.equals("K9") || capability.equals("PATROL");
        }
        
        // Detective for investigation crimes
        if (crimeType.contains("FRAUD") || crimeType.contains("MURDER") || 
            crimeType.contains("INVESTIGATION")) {
            return capability.equals("DETECTIVE") || capability.equals("PATROL");
        }
        
        // Traffic units for traffic-related crimes
        if (crimeType.contains("TRAFFIC") || crimeType.contains("DUI")) {
            return capability.equals("TRAFFIC") || capability.equals("PATROL");
        }
        
        // Cyber crime units
        if (crimeType.contains("CYBER") || crimeType.contains("HACKING")) {
            return capability.equals("CYBER_CRIME") || capability.equals("DETECTIVE");
        }
        
        // Riot control for large disturbances
        if (crimeType.contains("RIOT") || crimeType.contains("CIVIL_UNREST")) {
            return capability.equals("RIOT_CONTROL") || capability.equals("SWAT");
        }
        
        // Emergency units for critical crimes
        if (crime.getSeverity().equals("CRITICAL") || crimeType.equals("EMERGENCY")) {
            return capability.equals("EMERGENCY") || capability.equals("PATROL") || capability.equals("SWAT");
        }
        
        // Regular patrol can handle most standard crimes
        return capability.equals("PATROL") || capability.equals("EMERGENCY");
    }
    
    /**
     * Process pending crimes when units become available
     * Enhanced with Hungarian algorithm for batch assignment
     */
    private void processPendingCrimes(int currentTick) {
        if (pendingCrimes.isEmpty()) return;
        
        // Get all available units
        List<Unit> availableUnits = getAvailableUnits();
        if (availableUnits.isEmpty()) return;
        
        // If we have multiple crimes and units, use Hungarian algorithm for optimal batch assignment
        if (pendingCrimes.size() > 1 && availableUnits.size() > 1) {
            processPendingCrimesWithHungarian(availableUnits, currentTick);
        } else {
            // Process individual crimes for small batches - simplified assignment
            List<Crime> toRemove = new ArrayList<>();
            for (Crime pendingCrime : pendingCrimes) {
                List<Unit> suitableUnits = findSuitableUnits(pendingCrime);
                if (!suitableUnits.isEmpty()) {
                    Unit bestUnit = suitableUnits.get(0); // Simple: take first suitable unit
                    if (bestUnit != null) {
                        dispatchUnitToCrime(bestUnit, pendingCrime, currentTick);
                        toRemove.add(pendingCrime);
                        System.out.println("Assigned pending crime " + pendingCrime.getCrimeId() + " to Unit " + bestUnit.getUnitId());
                    }
                }
            }
            
            // Remove assigned crimes from pending queue
            pendingCrimes.removeAll(toRemove);
        }
    }
    
    /**
     * Process multiple pending crimes using Hungarian algorithm for optimal assignment
     */
    private void processPendingCrimesWithHungarian(List<Unit> availableUnits, int currentTick) {
        System.out.println("Processing " + pendingCrimes.size() + " pending crimes with " + 
                          availableUnits.size() + " available units using Hungarian algorithm");
        
        // Limit batch size for performance
        int batchSize = Math.min(10, Math.min(pendingCrimes.size(), availableUnits.size()));
        List<Crime> batchCrimes = pendingCrimes.subList(0, batchSize);
        
        // Find suitable units for each crime in batch
        Map<Crime, List<Unit>> suitableUnitsMap = new HashMap<>();
        List<Unit> allSuitableUnits = new ArrayList<>();
        
        for (Crime crime : batchCrimes) {
            List<Unit> suitable = new ArrayList<>();
            for (Unit unit : availableUnits) {
                if (findSuitableUnits(crime).contains(unit)) {
                    suitable.add(unit);
                    if (!allSuitableUnits.contains(unit)) {
                        allSuitableUnits.add(unit);
                    }
                }
            }
            suitableUnitsMap.put(crime, suitable);
        }
        
        if (allSuitableUnits.isEmpty()) return;
        
        // Create cost matrix for Hungarian algorithm
        int numUnits = Math.min(allSuitableUnits.size(), batchCrimes.size());
        int numCrimes = batchCrimes.size();
        
        double[][] costMatrix = new double[numUnits][numCrimes];
        int[] unitIds = new int[numUnits];
        int[] crimeIds = new int[numCrimes];
        
        // Fill cost matrix
        for (int i = 0; i < numUnits; i++) {
            Unit unit = allSuitableUnits.get(i);
            unitIds[i] = unit.getUnitId();
            
            for (int j = 0; j < numCrimes; j++) {
                Crime crime = batchCrimes.get(j);
                crimeIds[j] = crime.getCrimeId();
                
                // Check if unit can handle this crime
                List<Unit> suitableForCrime = suitableUnitsMap.get(crime);
                if (suitableForCrime != null && suitableForCrime.contains(unit)) {
                    // Calculate actual cost (simplified - no specialization bonus)
                    var pathResult = pathfindingService.calculateShortestPath(
                        unit.getCurrentLocationId(), crime.getLocationId());
                    
                    double baseCost = pathResult.isValidPath() ? pathResult.getDistance() : 1000.0;
                    costMatrix[i][j] = Math.max(0.1, baseCost);
                } else {
                    // Very high cost for unsuitable unit-crime pairs
                    costMatrix[i][j] = 10000.0;
                }
            }
        }
        
        // Solve using Hungarian algorithm
        List<HungarianAlgorithm.Assignment> assignments = 
            HungarianAlgorithm.solve(costMatrix, unitIds, crimeIds);
        
        // Execute assignments
        List<Crime> assignedCrimes = new ArrayList<>();
        double totalCost = 0.0;
        
        for (HungarianAlgorithm.Assignment assignment : assignments) {
            if (assignment.getCost() < 1000.0) { // Valid assignment (not dummy/unsuitable)
                Unit unit = allUnits.get(assignment.getUnitId());
                Crime crime = findCrimeById(assignment.getCrimeId(), batchCrimes);
                
                if (unit != null && crime != null && unit.getStatus().equals("AVAILABLE")) {
                    dispatchUnitToCrime(unit, crime, currentTick);
                    assignedCrimes.add(crime);
                    totalCost += assignment.getCost();
                    
                    System.out.println("Hungarian batch assignment: Unit " + unit.getUnitId() + 
                                     " (" + unit.getCapabilities() + ") -> Crime " + crime.getCrimeId() + 
                                     " (cost: " + String.format("%.2f", assignment.getCost()) + ")");
                }
            }
        }
        
        // Remove assigned crimes from pending queue
        pendingCrimes.removeAll(assignedCrimes);
        
        System.out.println("Hungarian batch complete: " + assignedCrimes.size() + 
                          " crimes assigned with total cost " + String.format("%.2f", totalCost));
    }
    
    /**
     * Helper method to find crime by ID in a list
     */
    private Crime findCrimeById(int crimeId, List<Crime> crimes) {
        return crimes.stream()
                    .filter(crime -> crime.getCrimeId() == crimeId)
                    .findFirst()
                    .orElse(null);
    }

    public List<Unit> getAvailableUnits() {
        List<Unit> available = new ArrayList<>();
        for (Unit unit : allUnits.values()) {
            if (unit.getStatus().equals("AVAILABLE")) {
                available.add(unit);
            }
        }
        return available;
    }
    
    public List<Unit> getDispatchedUnits() {
        List<Unit> dispatched = new ArrayList<>();
        for (Unit unit : allUnits.values()) {
            if (unit.getStatus().equals("DISPATCHED") || unit.getStatus().equals("EN_ROUTE")) {
                dispatched.add(unit);
            }
        }
        return dispatched;
    }

    public void dispatchUnit(int unitId, int crimeId) {
        Unit unit = allUnits.get(unitId);
        if (unit != null) {
            unit.setStatus("DISPATCHED");
            System.out.println("PoliceManager.dispatchUnit: Unit " + unitId + " dispatched to crime " + crimeId);
        }
    }

    public void updateAllUnits(int currentTick) {
        // Update unit positions and check for arrivals
        List<Integer> completedCrimes = new ArrayList<>();
        
        for (Assignment assignment : activeAssignments.values()) {
            Unit unit = allUnits.get(assignment.getUnitId());
            if (unit != null) {
                // Simulate unit movement with realistic progression
                if (unit.getStatus().equals("DISPATCHED")) {
                    unit.setStatus("EN_ROUTE");
                    System.out.println("Unit " + unit.getUnitId() + " (" + unit.getCapabilities() + ") en route to crime " + assignment.getCrimeId());
                } else if (unit.getStatus().equals("EN_ROUTE")) {
                    // Simulate arrival based on distance and unit type
                    double arrivalChance = calculateArrivalChance(unit);
                    if (Math.random() < arrivalChance) {
                        unit.setStatus("ON_SCENE");
                        Integer crimeLocation = crimeLocations.get(assignment.getCrimeId());
                        if (crimeLocation != null) {
                            unit.setCurrentLocationId(crimeLocation);
                        }
                        System.out.println("Unit " + unit.getUnitId() + " (" + unit.getCapabilities() + ") arrived at crime " + assignment.getCrimeId());
                    }
                } else if (unit.getStatus().equals("ON_SCENE")) {
                    // Simulate crime resolution based on unit specialization
                    double resolutionChance = calculateResolutionChance(unit);
                    if (Math.random() < resolutionChance) {
                        completedCrimes.add(assignment.getCrimeId());
                        unit.setStatus("AVAILABLE");
                        System.out.println("Unit " + unit.getUnitId() + " (" + unit.getCapabilities() + ") completed crime " + assignment.getCrimeId());
                    }
                }
            }
        }
        
        // Process completed assignments
        for (Integer crimeId : completedCrimes) {
            Assignment completed = activeAssignments.remove(crimeId);
            if (completed != null) {
                // Set completion tick and calculate response time based on simulation time
                completed.setCompletionTick(currentTick);
                completedAssignments.add(completed);
                
                // Clean up assigned units tracking
                assignedUnits.remove(crimeId);
                crimeLocations.remove(crimeId);
            }
        }
        
        // Process pending crimes if units become available
        processPendingCrimes(currentTick);
        
        // Print status summary
        int available = getAvailableUnits().size();
        int dispatched = getDispatchedUnits().size();
        int onScene = getArrivedUnits().size();
        
        System.out.println("PoliceManager.updateAllUnits: " + available + " available, " + 
                          dispatched + " dispatched, " + onScene + " on scene");
    }
    
    /**
     * Calculate arrival chance based on unit type and capabilities
     */
    private double calculateArrivalChance(Unit unit) {
        String capability = unit.getCapabilities();
        
        // Different unit types have different response speeds
        switch (capability) {
            case "EMERGENCY": return 0.5; // Fast response
            case "SWAT": return 0.4; // Quick but cautious
            case "PATROL": return 0.35; // Standard response
            case "TRAFFIC": return 0.45; // Good mobility
            case "K9": return 0.3; // Slower due to equipment
            case "BOMB_SQUAD": return 0.25; // Very cautious approach
            case "RIOT_CONTROL": return 0.3; // Heavy equipment
            case "DETECTIVE": return 0.2; // Not rushing
            case "HOSTAGE_NEGOTIATION": return 0.2; // Careful approach
            case "CYBER_CRIME": return 0.15; // Office-based, slower response
            default: return 0.3;
        }
    }
    
    /**
     * Calculate resolution chance based on unit specialization
     */
    private double calculateResolutionChance(Unit unit) {
        String capability = unit.getCapabilities();
        
        // Specialized units are more effective at their tasks
        switch (capability) {
            case "SWAT": return 0.6; // Highly effective
            case "BOMB_SQUAD": return 0.7; // Very specialized
            case "EMERGENCY": return 0.5; // Good general response
            case "DETECTIVE": return 0.4; // Takes time to investigate
            case "K9": return 0.55; // Very effective for drugs
            case "TRAFFIC": return 0.45; // Good for traffic crimes
            case "RIOT_CONTROL": return 0.5; // Effective for crowds
            case "HOSTAGE_NEGOTIATION": return 0.3; // Takes time but crucial
            case "CYBER_CRIME": return 0.35; // Complex investigations
            case "PATROL": return 0.4; // General effectiveness
            default: return 0.4;
        }
    }

    public List<Unit> getArrivedUnits() {
        List<Unit> arrived = new ArrayList<>();
        for (Unit unit : allUnits.values()) {
            if (unit.getStatus().equals("ON_SCENE")) {
                arrived.add(unit);
            }
        }
        return arrived;
    }

    public List<Assignment> getCompletedAssignments() {
        return new ArrayList<>(completedAssignments);
    }

    /**
     * Clear completed assignments list (called after processing to prevent duplicates)
     */
    public void clearCompletedAssignments() {
        completedAssignments.clear();
    }

    public void returnUnitToService(int unitId) {
        Unit unit = allUnits.get(unitId);
        if (unit != null) {
            unit.setStatus("AVAILABLE");
            System.out.println("Unit " + unitId + " returned to service");
        }
    }
}