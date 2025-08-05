// SimulatorCore.java
// Module 4: Dispatch & Simulation Core
// Orchestrates simulation loop and dispatch logic

// Expected Output Example:
// - Runs simulation loop and processes events
// - Dispatches police units to crimes
// - Example usage:
//   SimulatorCore sc = new SimulatorCore(cityMap, policeManager, crimeGenerator, pathfindingService);
//   sc.run();

import services.CrimeAssignmentService;
import models.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimulatorCore {
    
    // Core modules
    private CityMap cityMap;
    private PoliceManager policeManager;
    private CrimeGenerator crimeGenerator;
    private PathfindingService pathfindingService;
    private CrimeAssignmentService assignmentService;
    
    // Simulation state
    private int currentTick;
    private boolean isRunning;
    private int maxTicks;
    private ScheduledExecutorService scheduler;
    
    // Statistics
    private int totalCrimesGenerated;
    private int totalAssignments;
    private double totalResponseTime;
    private List<String> eventLog;
    
    // Simulation parameters
    private static final int TICK_INTERVAL_MS = 1000; // 1 second per tick
    private static final int DEFAULT_MAX_TICKS = 1000; // 1000 ticks = ~16 minutes
    
    public SimulatorCore(CityMap cityMap, PoliceManager policeManager, 
                        CrimeGenerator crimeGenerator, PathfindingService pathfindingService) {
        this.cityMap = cityMap;
        this.policeManager = policeManager;
        this.crimeGenerator = crimeGenerator;
        this.pathfindingService = pathfindingService;
        this.assignmentService = new CrimeAssignmentService(); // Temporarily no PathfindingService
        
        // Initialize simulation state
        this.currentTick = 0;
        this.isRunning = false;
        this.maxTicks = DEFAULT_MAX_TICKS;
        this.scheduler = null; // Will be created when simulation starts
        
        // Initialize statistics
        this.totalCrimesGenerated = 0;
        this.totalAssignments = 0;
        this.totalResponseTime = 0.0;
        this.eventLog = new ArrayList<>();
        
        logEvent("Simulator initialized successfully");
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
     * Process crime assignments and dispatch units
     */
    private void processDispatches() {
        // Process dispatch operations with police manager
        if (policeManager == null) {
            logEvent("Police manager not available - skipping dispatch");
            return;
        }
        
        // Get available units and active crimes
        List<Unit> availableUnits = policeManager.getAvailableUnits();
        List<Crime> activeCrimes = crimeGenerator.getActiveCrimes();
        
        if (!availableUnits.isEmpty() && !activeCrimes.isEmpty()) {
            // Use assignment service to optimally assign units to crimes
            List<Assignment> assignments = assignmentService.assignUnitsToCrimes(availableUnits, activeCrimes);
            
            for (Assignment assignment : assignments) {
                // Update unit status and crime status
                policeManager.dispatchUnit(assignment.getUnitId(), assignment.getCrimeId());
                crimeGenerator.updateCrimeStatus(assignment.getCrimeId(), "IN_PROGRESS");
                
                totalAssignments++;
                totalResponseTime += assignment.getResponseTime();
                
                logEvent("Dispatched Unit " + assignment.getUnitId() + 
                        " to Crime " + assignment.getCrimeId() + 
                        " (ETA: " + String.format("%.1f", assignment.getResponseTime()) + " minutes)");
            }
        }
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
        policeManager.updateAllUnits();
        
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
        double avgResponseTime = totalAssignments > 0 ? totalResponseTime / totalAssignments : 0.0;
        
        System.out.println("\n=== SIMULATION COMPLETE ===");
        System.out.println("Total ticks: " + currentTick);
        System.out.println("Total crimes generated: " + totalCrimesGenerated);
        System.out.println("Total assignments made: " + totalAssignments);
        System.out.printf("Average response time: %.2f minutes\n", avgResponseTime);
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
        stats.put("totalAssignments", totalAssignments);
        stats.put("averageResponseTime", totalAssignments > 0 ? totalResponseTime / totalAssignments : 0.0);
        stats.put("activeCrimes", crimeGenerator.getActiveCrimes().size());
        return stats;
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
