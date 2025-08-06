// SimulatorCore.java
// Module 4: Dispatch & Simulation Core
// Orchestrates simulation loop and dispatch logic

// Expected Output Example:
// - Runs simulation loop and processes events
// - Dispatches police units to crimes
// - Example usage:
//   SimulatorCore sc = new SimulatorCore(cityMap, policeManager, crimeGenerator, pathfindingService);
//   sc.run();

// CrimeAssignmentService import removed - no longer used
import models.*;
import algorithms.HungarianAlgorithm;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimulatorCore {
    
    // Helper classes for new assignment logic
    private static class StationWithUnits {
        int stationLocation;
        double distance;
        List<Unit> availableUnits;
        
        StationWithUnits(int stationLocation, double distance, List<Unit> availableUnits) {
            this.stationLocation = stationLocation;
            this.distance = distance;
            this.availableUnits = availableUnits;
        }
    }
    
    private static class CrimeStationAssignment {
        Crime crime;
        List<StationWithUnits> suitableStations;
        
        CrimeStationAssignment(Crime crime, List<StationWithUnits> suitableStations) {
            this.crime = crime;
            this.suitableStations = suitableStations;
        }
    }
    
    // Core modules
    private CityMap cityMap;
    private PoliceManager policeManager;
    private CrimeGenerator crimeGenerator;
    private PathfindingService pathfindingService;
    // Removed CrimeAssignmentService - assignments now handled by PoliceManager
    
    // Police station locations (calculated once at start using Floyd-Warshall)
    private List<Integer> policeStationLocations;
    
    // Simulation state
    private int currentTick;
    private boolean isRunning;
    private int maxTicks;
    private ScheduledExecutorService scheduler;
    
    // Statistics
    private int totalCrimesGenerated;
    private List<String> eventLog;
    private List<Assignment> allCompletedAssignments; // Global storage for all completed assignments
    
    // Simulation parameters
    private static final int TICK_INTERVAL_MS = 1000; // 1 second per tick
    private static final int DEFAULT_MAX_TICKS = 1000; // 1000 ticks = ~16 minutes
    
    public SimulatorCore(CityMap cityMap, PoliceManager policeManager, 
                        CrimeGenerator crimeGenerator, PathfindingService pathfindingService) {
        this.cityMap = cityMap;
        this.policeManager = policeManager;
        this.crimeGenerator = crimeGenerator;
        this.pathfindingService = pathfindingService;
        // CrimeAssignmentService removed - PoliceManager handles all assignments
        
        // Initialize simulation state
        this.currentTick = 0;
        this.isRunning = false;
        this.maxTicks = DEFAULT_MAX_TICKS;
        this.scheduler = null; // Will be created when simulation starts
        
        // Initialize statistics
        this.totalCrimesGenerated = 0;
        this.eventLog = new ArrayList<>();
        this.allCompletedAssignments = new ArrayList<>(); // Initialize global completed assignments list
        
        // Initialize police station locations using Floyd-Warshall (once at start)
        initializePoliceStations();
        
        logEvent("Simulator initialized successfully");
    }
    
    /**
     * Alternative constructor that creates PoliceManager with proper police station distribution
     */
    public static SimulatorCore createWithStationBasedPolice(CityMap cityMap, CrimeGenerator crimeGenerator, 
                                                           PathfindingService pathfindingService) {
        // First, calculate optimal police station locations
        int totalNodes = cityMap.getTotalNodes();
        int numStations = Math.max(5, totalNodes / 20); // More stations for bigger map
        List<Integer> policeStationLocations = cityMap.findOptimalPoliceStationLocations(numStations);
        
        // Create PoliceManager with these station locations
        PoliceManager policeManager = new PoliceManager(cityMap, policeStationLocations);
        
        // Create SimulatorCore
        SimulatorCore simulator = new SimulatorCore(cityMap, policeManager, crimeGenerator, pathfindingService);
        
        // Override the police station locations with the pre-calculated ones
        simulator.policeStationLocations = policeStationLocations;
        
        return simulator;
    }
    
    /**
     * Start the simulation
     */
    public void run() {
        run(DEFAULT_MAX_TICKS);
    }
    
    /**
     * Start the simulation with specified max ticks
     */
    public void run(int maxTicks) {
        if (isRunning) {
            logEvent("Simulation already running!");
            return;
        }
        
        this.maxTicks = maxTicks;
        this.isRunning = true;
        this.currentTick = 0;
        
        // Create a new scheduler for this simulation run
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
        scheduler = Executors.newSingleThreadScheduledExecutor();
        
        logEvent("Starting simulation for " + maxTicks + " ticks...");
        
        // Schedule the simulation loop
        scheduler.scheduleAtFixedRate(this::simulationTick, 0, TICK_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Stop the simulation
     */
    public void stop() {
        if (isRunning) {
            isRunning = false;
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdown();
                try {
                    if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                        scheduler.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    scheduler.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
            logEvent("Simulation stopped at tick " + currentTick);
            printFinalStatistics();
        }
    }
    
    /**
     * Execute one simulation tick
     */
    private void simulationTick() {
        try {
            currentTick++;
            
            // Check if simulation should end
            if (currentTick >= maxTicks) {
                stop();
                return;
            }
            
            logEvent("=== Tick " + currentTick + " ===");
            
            // Step 1: Generate new crimes
            generateCrimes();
            
            // Step 2: Process assignments and dispatch units
            processDispatches();
            
            // Step 3: Update unit movements and statuses
            updateUnits();
            
            // Step 4: Handle crime resolutions
            resolveCrimes();
            
            // Step 5: Print tick summary
            if (currentTick % 10 == 0) { // Print every 10 ticks
                printTickSummary();
            }
            
        } catch (Exception e) {
            logEvent("Error in simulation tick " + currentTick + ": " + e.getMessage());
            e.printStackTrace();
            stop();
        }
    }
    
    /**
     * Generate new crimes for this tick
     */
    private void generateCrimes() {
        // Generate new crimes and integrate with police manager
        Crime newCrime = crimeGenerator.generateCrime();
        if (newCrime != null) {
            totalCrimesGenerated++;
            logEvent("New crime generated: " + newCrime.getType() + 
                    " (" + newCrime.getSeverity() + ") at location " + newCrime.getLocationId());
            
            // Notify police manager if available
            if (policeManager != null) {
                policeManager.handleNewCrime(newCrime);
            }
        }
    }
    
    /**
     * Process crime assignments using correct flow: Dijkstra → Station Finding → Hungarian
     */
    private void processDispatches() {
        // Process dispatch operations with correct algorithm flow
        if (policeManager == null) {
            logEvent("Police manager not available - skipping dispatch");
            return;
        }
        
        // Get pending crimes (units are organized by stations)
        List<Crime> pendingCrimes = policeManager.getPendingCrimes();
        
        if (pendingCrimes.isEmpty()) {
            return;
        }
        
        logEvent("Processing assignments: " + pendingCrimes.size() + " pending crimes");
        
        // NEW LOGIC: For each crime, find nearest stations with appropriate units
        List<CrimeStationAssignment> crimeStationMappings = new ArrayList<>();
        
        for (Crime crime : pendingCrimes) {
            List<StationWithUnits> suitableStations = findNearestStationsWithAppropriateUnits(crime);
            if (!suitableStations.isEmpty()) {
                crimeStationMappings.add(new CrimeStationAssignment(crime, suitableStations));
            }
        }
        
        if (crimeStationMappings.isEmpty()) {
            logEvent("No stations have appropriate units for pending crimes");
            
            // FALLBACK: Direct unit assignment when station-based assignment fails
            List<Unit> availableUnits = policeManager.getAvailableUnits();
            if (!availableUnits.isEmpty() && !pendingCrimes.isEmpty()) {
                logEvent("Falling back to direct unit assignment");
                
                // Process each pending crime individually
                List<Crime> assignedCrimes = new ArrayList<>();
                for (Crime crime : pendingCrimes) {
                    List<Unit> suitableUnits = policeManager.findSuitableUnits(crime);
                    suitableUnits.retainAll(availableUnits); // Only consider available units
                    
                    if (!suitableUnits.isEmpty()) {
                        // Find the closest suitable unit
                        Unit bestUnit = findClosestUnit(suitableUnits, crime);
                        
                        if (bestUnit != null) {
                            policeManager.dispatchUnitToCrime(bestUnit, crime, currentTick);
                            assignedCrimes.add(crime);
                            availableUnits.remove(bestUnit); // Remove from available list
                            
                            logEvent("Fallback assignment: Unit " + bestUnit.getUnitId() + 
                                    " (" + bestUnit.getCapabilities() + ") assigned to crime " + crime.getCrimeId());
                        }
                    }
                }
                
                // Remove assigned crimes from pending
                for (Crime crime : assignedCrimes) {
                    policeManager.removePendingCrime(crime);
                }
            }
            return;
        }
        
        // Use Hungarian algorithm for optimal assignment across multiple crimes/stations
        if (crimeStationMappings.size() > 1) {
            processWithHungarianAlgorithmNew(crimeStationMappings);
        } else {
            // Single crime: simple assignment to nearest suitable station
            processSimpleStationAssignment(crimeStationMappings.get(0));
        }
    }
    
    /**
     * Find nearest police stations that have appropriate units for a crime
     * Uses Dijkstra to find distances to all stations, then checks unit availability
     */
    private List<StationWithUnits> findNearestStationsWithAppropriateUnits(Crime crime) {
        // Get all police station locations from cityMap
        List<Integer> stationLocations = getPoliceStationLocations();
        
        List<StationWithUnits> suitableStations = new ArrayList<>();
        
        // Calculate distances to all stations using Dijkstra
        for (Integer stationLocation : stationLocations) {
            var pathResult = pathfindingService.calculateShortestPath(crime.getLocationId(), stationLocation);
            
            if (pathResult.isValidPath()) {
                // Check if this station has appropriate units for the crime
                List<Unit> unitsAtStation = policeManager.getUnitsAtLocation(stationLocation);
                List<Unit> suitableUnits = filterSuitableUnits(unitsAtStation, crime);
                
                if (!suitableUnits.isEmpty()) {
                    suitableStations.add(new StationWithUnits(
                        stationLocation, 
                        pathResult.getDistance(), 
                        suitableUnits
                    ));
                }
            }
        }
        
        // Sort stations by distance (nearest first)
        suitableStations.sort(Comparator.comparingDouble(s -> s.distance));
        
        logEvent("Crime " + crime.getCrimeId() + " (" + crime.getType() + "): Found " + 
                suitableStations.size() + " stations with appropriate units");
        
        return suitableStations;
    }
    
    /**
     * Initialize police station locations using Floyd-Warshall algorithm
     * This happens once at simulation start for optimal placement
     */
    private void initializePoliceStations() {
        int totalNodes = cityMap.getTotalNodes();
        int numStations = Math.max(5, totalNodes / 20); // More stations for bigger map
        
        this.policeStationLocations = cityMap.findOptimalPoliceStationLocations(numStations);
        
        logEvent("Police stations initialized at " + numStations + " optimal locations using Floyd-Warshall: " + 
                policeStationLocations);
    }
    
    /**
     * Get police station locations from the city map
     */
    private List<Integer> getPoliceStationLocations() {
        // Return pre-calculated station locations (calculated once at start)
        return policeStationLocations;
    }
    
    /**
     * Filter units that can handle the specific crime type
     */
    private List<Unit> filterSuitableUnits(List<Unit> units, Crime crime) {
        List<Unit> suitable = new ArrayList<>();
        String[] requiredCapabilities = getRequiredCapabilities(crime.getType());
        
        for (Unit unit : units) {
            if (unit.getStatus().equals("Available")) {
                for (String capability : requiredCapabilities) {
                    if (unit.getCapabilities().equals(capability)) {
                        suitable.add(unit);
                        break; // Unit matches, no need to check other capabilities
                    }
                }
            }
        }
        
        return suitable;
    }
    
    /**
     * Process single crime assignment to nearest suitable station
     */
    private void processSimpleStationAssignment(CrimeStationAssignment assignment) {
        Crime crime = assignment.crime;
        StationWithUnits nearestStation = assignment.suitableStations.get(0);
        
        // Select best unit from the nearest station using unit stats
        Unit bestUnit = selectBestUnitFromStation(nearestStation.availableUnits, crime);
        
        if (bestUnit != null) {
            policeManager.dispatchUnitToCrime(bestUnit, crime, currentTick);
            policeManager.removePendingCrime(crime);
            
            logEvent("Station-based assignment: Unit " + bestUnit.getUnitId() + 
                    " (" + bestUnit.getCapabilities() + ") from station " + nearestStation.stationLocation +
                    " assigned to crime " + crime.getCrimeId() + " (distance: " + 
                    String.format("%.2f", nearestStation.distance) + ")");
        }
    }
    
    /**
     * Process multiple crimes using Hungarian algorithm with station-based logic
     */
    private void processWithHungarianAlgorithmNew(List<CrimeStationAssignment> assignments) {
        logEvent("Using Hungarian algorithm for station-based optimal assignment");
        
        // Build list of all available crime-unit pairs
        List<CrimeUnitPair> allPairs = new ArrayList<>();
        
        for (CrimeStationAssignment assignment : assignments) {
            Crime crime = assignment.crime;
            
            // Consider units from multiple stations (nearest first)
            int stationsToConsider = Math.min(3, assignment.suitableStations.size());
            
            for (int i = 0; i < stationsToConsider; i++) {
                StationWithUnits station = assignment.suitableStations.get(i);
                
                for (Unit unit : station.availableUnits) {
                    allPairs.add(new CrimeUnitPair(crime, unit, station.distance));
                }
            }
        }
        
        if (allPairs.isEmpty()) {
            logEvent("No valid crime-unit pairs for Hungarian algorithm");
            return;
        }
        
        // Group by crimes and units for Hungarian matrix
        Set<Crime> uniqueCrimes = new HashSet<>();
        Set<Unit> uniqueUnits = new HashSet<>();
        
        for (CrimeUnitPair pair : allPairs) {
            uniqueCrimes.add(pair.crime);
            uniqueUnits.add(pair.unit);
        }
        
        List<Crime> crimes = new ArrayList<>(uniqueCrimes);
        List<Unit> units = new ArrayList<>(uniqueUnits);
        
        // Build cost matrix
        double[][] costMatrix = new double[units.size()][crimes.size()];
        int[] unitIds = new int[units.size()];
        int[] crimeIds = new int[crimes.size()];
        
        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            unitIds[i] = unit.getUnitId();
            
            for (int j = 0; j < crimes.size(); j++) {
                Crime crime = crimes.get(j);
                crimeIds[j] = crime.getCrimeId();
                
                // Find the crime-unit pair
                CrimeUnitPair pair = allPairs.stream()
                    .filter(p -> p.crime.equals(crime) && p.unit.equals(unit))
                    .findFirst().orElse(null);
                
                if (pair != null) {
                    // Calculate cost using distance, specialization, and unit stats
                    double baseCost = pair.distance;
                    double specializationBonus = getSpecializationBonus(unit, crime);
                    double performanceBonus = unit.getPerformanceScore() * 20.0; // Scale performance
                    double fatigueBonus = (1.0 - unit.getFatigueLevel()) * 10.0; // Less fatigue = lower cost
                    
                    costMatrix[i][j] = Math.max(0.1, baseCost - specializationBonus - performanceBonus - fatigueBonus);
                } else {
                    // High cost for invalid pairs
                    costMatrix[i][j] = 10000.0;
                }
            }
        }
        
        // Solve using Hungarian algorithm
        List<HungarianAlgorithm.Assignment> assignments_result = 
            HungarianAlgorithm.solve(costMatrix, unitIds, crimeIds);
        
        // Execute assignments
        int assignmentCount = 0;
        for (HungarianAlgorithm.Assignment assignment : assignments_result) {
            Unit unit = units.stream()
                .filter(u -> u.getUnitId() == assignment.getUnitId())
                .findFirst().orElse(null);
            Crime crime = crimes.stream()
                .filter(c -> c.getCrimeId() == assignment.getCrimeId())
                .findFirst().orElse(null);
            
            if (unit != null && crime != null && assignment.getCost() < 5000.0) {
                policeManager.dispatchUnitToCrime(unit, crime, currentTick);
                policeManager.removePendingCrime(crime);
                assignmentCount++;
                
                logEvent("Hungarian station-based: Unit " + unit.getUnitId() + 
                        " (" + unit.getCapabilities() + ", " + unit.getExperienceLevel() + 
                        ") assigned to crime " + crime.getCrimeId() + 
                        " with cost " + String.format("%.2f", assignment.getCost()));
            }
        }
        
        logEvent("Hungarian station-based algorithm completed: " + assignmentCount + " assignments made");
    }
    
    /**
     * Select best unit from station based on unit stats and crime requirements
     */
    private Unit selectBestUnitFromStation(List<Unit> units, Crime crime) {
        if (units.isEmpty()) return null;
        
        // Sort units by performance score (considering experience, equipment, fatigue)
        units.sort((a, b) -> Double.compare(b.getPerformanceScore(), a.getPerformanceScore()));
        
        return units.get(0); // Return best performing unit
    }
    
    // Helper class for Hungarian algorithm
    private static class CrimeUnitPair {
        Crime crime;
        Unit unit;
        double distance;
        
        CrimeUnitPair(Crime crime, Unit unit, double distance) {
            this.crime = crime;
            this.unit = unit;
            this.distance = distance;
        }
    }
    

    
    /**
     * Calculate specialization bonus for unit-crime pairing
     */
    private double getSpecializationBonus(Unit unit, Crime crime) {
        String[] requiredCapabilities = getRequiredCapabilities(crime.getType());
        String unitCapability = unit.getCapabilities();
        
        // Higher bonus for better matches
        for (int i = 0; i < requiredCapabilities.length; i++) {
            if (unitCapability.equals(requiredCapabilities[i])) {
                return 50.0 - (i * 10.0); // Primary match gets 50, secondary gets 40, etc.
            }
        }
        return 0.0; // No specialization bonus
    }
    
    /**
     * Get required capabilities for crime type
     */
    private String[] getRequiredCapabilities(String crimeType) {
        // Define crime-capability mappings
        switch (crimeType.toUpperCase()) {
            case "DRUG_OFFENSE": return new String[]{"K9", "PATROL", "DETECTIVE"};
            case "BOMB_THREAT": return new String[]{"BOMB_SQUAD", "SWAT", "EMERGENCY"};
            case "HOSTAGE_SITUATION": return new String[]{"HOSTAGE_NEGOTIATION", "SWAT", "EMERGENCY"};
            case "CYBERCRIME": return new String[]{"CYBER_CRIME", "DETECTIVE", "PATROL"};
            case "RIOT": return new String[]{"RIOT_CONTROL", "SWAT", "PATROL"};
            case "TRAFFIC_VIOLATION": return new String[]{"TRAFFIC", "PATROL"};
            case "ASSAULT": return new String[]{"PATROL", "EMERGENCY", "SWAT"};
            case "BURGLARY": return new String[]{"PATROL", "DETECTIVE", "EMERGENCY"};
            case "ROBBERY": return new String[]{"PATROL", "SWAT", "EMERGENCY"};
            default: return new String[]{"PATROL", "EMERGENCY"};
        }
    }
    
    /**
     * Find closest unit to crime location
     */
    private Unit findClosestUnit(List<Unit> units, Crime crime) {
        Unit closestUnit = null;
        double shortestDistance = Double.MAX_VALUE;
        
        for (Unit unit : units) {
            var pathResult = pathfindingService.calculateShortestPath(
                unit.getCurrentLocationId(), crime.getLocationId());
                
            if (pathResult.isValidPath() && pathResult.getDistance() < shortestDistance) {
                shortestDistance = pathResult.getDistance();
                closestUnit = unit;
            }
        }
        
        return closestUnit;
    }
    
    /**
     * Update unit movements and statuses
     */
    private void updateUnits() {
        // Update unit movements and positions
        if (policeManager == null) {
            return;
        }
        
        // Update all unit positions and check arrivals
        policeManager.updateAllUnits(currentTick);
        
        // Check for units that have arrived at their destinations
        List<Unit> arrivedUnits = policeManager.getArrivedUnits();
        for (Unit unit : arrivedUnits) {
            logEvent("Unit " + unit.getUnitId() + " arrived at destination");
        }
    }
    
    /**
     * Handle crime resolutions
     */
    private void resolveCrimes() {
        // Process crime resolution through police manager
        if (policeManager == null) {
            // Fallback: Randomly resolve some crimes when no police manager
            List<Crime> activeCrimes = crimeGenerator.getActiveCrimes();
            if (!activeCrimes.isEmpty() && Math.random() < 0.1) { // 10% chance to resolve
                Crime crime = activeCrimes.get(0);
                crimeGenerator.resolveCrime(crime.getCrimeId());
                logEvent("Crime " + crime.getCrimeId() + " resolved (fallback)");
            }
            return;
        }
        
        // Check for completed assignments
        List<Assignment> completedAssignments = policeManager.getCompletedAssignments();
        for (Assignment assignment : completedAssignments) {
            // Resolve the crime
            crimeGenerator.resolveCrime(assignment.getCrimeId());
            
            // Return unit to available status
            policeManager.returnUnitToService(assignment.getUnitId());
            
            logEvent("Crime " + assignment.getCrimeId() + " resolved by Unit " + assignment.getUnitId());
        }
        
        // COLLECT COMPLETED ASSIGNMENTS: Transfer newly completed assignments to global storage
        List<Assignment> newlyCompleted = policeManager.getCompletedAssignments();
        if (!newlyCompleted.isEmpty()) {
            allCompletedAssignments.addAll(newlyCompleted);
            // Now clear the police manager's completed assignments to prevent reprocessing
            policeManager.clearCompletedAssignments();
        }
    }
    
    /**
     * Print summary for current tick
     */
    private void printTickSummary() {
        List<Crime> activeCrimes = crimeGenerator.getActiveCrimes();
        
        // Get unit statistics from police manager
        int availableUnits = (policeManager != null) ? policeManager.getAvailableUnits().size() : 0;
        int dispatchedUnits = (policeManager != null) ? policeManager.getDispatchedUnits().size() : 0;
        
        System.out.printf("Tick %d Summary: %d active crimes, %d available units, %d dispatched units\n",
            currentTick, activeCrimes.size(), availableUnits, dispatchedUnits);
    }
    
    /**
     * Print final simulation statistics
     */
    private void printFinalStatistics() {
        System.out.println("\n=== SIMULATION COMPLETE ===");
        System.out.println("Total ticks: " + currentTick);
        System.out.println("Total crimes generated: " + totalCrimesGenerated);
        System.out.println("Total assignments made: " + policeManager.getTotalAssignmentsMade());
        System.out.printf("Average response time: %.2f minutes\n", getAverageResponseTime());
        System.out.println("Active crimes remaining: " + crimeGenerator.getActiveCrimes().size());
        System.out.println("===========================\n");
    }
    
    /**
     * Emergency stop for critical situations
     */
    public void emergencyStop() {
        logEvent("EMERGENCY STOP initiated!");
        stop();
    }
    
    /**
     * Pause the simulation
     */
    public void pause() {
        if (isRunning) {
            isRunning = false;
            scheduler.shutdown();
            logEvent("Simulation paused at tick " + currentTick);
        }
    }
    
    /**
     * Resume the simulation
     */
    public void resume() {
        if (!isRunning && currentTick < maxTicks) {
            isRunning = true;
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(this::simulationTick, 0, TICK_INTERVAL_MS, TimeUnit.MILLISECONDS);
            logEvent("Simulation resumed at tick " + currentTick);
        }
    }
    
    /**
     * Get current simulation statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("currentTick", currentTick);
        stats.put("isRunning", isRunning);
        stats.put("totalCrimesGenerated", totalCrimesGenerated);
        stats.put("totalAssignments", policeManager.getTotalAssignmentsMade());
        stats.put("averageResponseTime", getAverageResponseTime()); // Use SimulatorCore's method
        stats.put("activeCrimes", crimeGenerator.getActiveCrimes().size());
        return stats;
    }
    
    /**
     * Calculate average response time from global completed assignments
     */
    private double getAverageResponseTime() {
        if (allCompletedAssignments.isEmpty()) {
            return 0.0;
        }
        
        double total = 0.0;
        for (Assignment assignment : allCompletedAssignments) {
            total += assignment.getResponseTime();
        }
        
        return total / allCompletedAssignments.size();
    }
    
    /**
     * Log an event with timestamp
     */
    private void logEvent(String message) {
        String logEntry = "[Tick " + currentTick + "] " + message;
        eventLog.add(logEntry);
        System.out.println(logEntry);
    }
    
    /**
     * Get the event log
     */
    public List<String> getEventLog() {
        return new ArrayList<>(eventLog);
    }
    
    // Getters for external access
    public int getCurrentTick() { return currentTick; }
    public boolean isRunning() { return isRunning; }
    public CityMap getCityMap() { return cityMap; }
    public CrimeGenerator getCrimeGenerator() { return crimeGenerator; }
    public PathfindingService getPathfindingService() { return pathfindingService; }
}
