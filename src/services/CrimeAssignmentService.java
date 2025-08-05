package services;

import algorithms.HungarianAlgorithm;
import models.Crime;
import models.Unit;
import models.Assignment;
import java.util.*;
import java.sql.Timestamp;

/**
 * Advanced crime assignment service using Hungarian Algorithm for optimal assignment
 * and greedy fallback strategies for real-time scenarios
 */
public class CrimeAssignmentService {
    
    // Note: PathfindingService integration disabled temporarily for compilation
    // private PathfindingService pathfindingService;
    private database.AssignmentDAO assignmentDAO;
    private int nextAssignmentId = 1;
    
    public CrimeAssignmentService() {
        // this.pathfindingService = pathfindingService;
        this.assignmentDAO = new database.AssignmentDAO();
    }
    
    /**
     * Assigns multiple units to multiple crimes using Hungarian Algorithm for optimal assignment
     * @param availableUnits List of available police units
     * @param activeCrimes List of active crimes
     * @return List of optimal assignments
     */
    public List<Assignment> assignUnitsToCrimes(List<Unit> availableUnits, List<Crime> activeCrimes) {
        if (availableUnits.isEmpty() || activeCrimes.isEmpty()) {
            return new ArrayList<>();
        }
        
        // For single crime or unit, use greedy assignment
        if (availableUnits.size() == 1 || activeCrimes.size() == 1) {
            return greedyAssignment(availableUnits, activeCrimes);
        }
        
        // Use Hungarian algorithm for multiple units and crimes
        return hungarianAssignment(availableUnits, activeCrimes);
    }
    
    /**
     * Optimal assignment using Hungarian Algorithm
     */
    private List<Assignment> hungarianAssignment(List<Unit> units, List<Crime> crimes) {
        int numUnits = units.size();
        int numCrimes = crimes.size();
        
        // Build cost matrix (response time + priority weighting)
        double[][] costMatrix = new double[numUnits][numCrimes];
        int[] unitIds = new int[numUnits];
        int[] crimeIds = new int[numCrimes];
        
        for (int i = 0; i < numUnits; i++) {
            unitIds[i] = units.get(i).getUnitId();
            for (int j = 0; j < numCrimes; j++) {
                crimeIds[j] = crimes.get(j).getCrimeId();
                costMatrix[i][j] = calculateAssignmentCost(units.get(i), crimes.get(j));
            }
        }
        
        // Solve using Hungarian algorithm
        List<HungarianAlgorithm.Assignment> hungarianResult = 
            HungarianAlgorithm.solve(costMatrix, unitIds, crimeIds);
        
        // Convert to Assignment objects and persist
        List<Assignment> assignments = new ArrayList<>();
        for (HungarianAlgorithm.Assignment ha : hungarianResult) {
            Assignment assignment = createAssignment(ha.getUnitId(), ha.getCrimeId(), ha.getCost());
            assignments.add(assignment);
            assignmentDAO.insertAssignment(assignment);
        }
        
        System.out.printf("Hungarian assignment: %d units assigned to %d crimes (total cost: %.2f)\n",
            assignments.size(), assignments.size(), 
            HungarianAlgorithm.calculateTotalCost(hungarianResult));
        
        return assignments;
    }
    
    /**
     * Greedy assignment strategy (fallback for simple cases)
     */
    private List<Assignment> greedyAssignment(List<Unit> units, List<Crime> crimes) {
        List<Assignment> assignments = new ArrayList<>();
        
        // Sort crimes by priority (high priority first)
        List<Crime> sortedCrimes = new ArrayList<>(crimes);
        sortedCrimes.sort((c1, c2) -> {
            int p1 = getPriorityValue(c1.getSeverity());
            int p2 = getPriorityValue(c2.getSeverity());
            return Integer.compare(p2, p1); // Descending order
        });
        
        Set<Integer> assignedUnits = new HashSet<>();
        
        for (Crime crime : sortedCrimes) {
            if (assignedUnits.size() >= units.size()) break;
            
            Unit bestUnit = null;
            double minCost = Double.MAX_VALUE;
            
            // Find closest available unit
            for (Unit unit : units) {
                if (!assignedUnits.contains(unit.getUnitId())) {
                    double cost = calculateAssignmentCost(unit, crime);
                    if (cost < minCost) {
                        minCost = cost;
                        bestUnit = unit;
                    }
                }
            }
            
            if (bestUnit != null) {
                Assignment assignment = createAssignment(bestUnit.getUnitId(), crime.getCrimeId(), minCost);
                assignments.add(assignment);
                assignedUnits.add(bestUnit.getUnitId());
                assignmentDAO.insertAssignment(assignment);
            }
        }
        
        System.out.printf("Greedy assignment: %d assignments made\n", assignments.size());
        return assignments;
    }
    
    /**
     * Calculate assignment cost (response time + priority weighting)
     */
    private double calculateAssignmentCost(Unit unit, Crime crime) {
        // Simplified distance calculation (can be replaced with PathfindingService later)
        double distance = calculateSimpleDistance(unit.getCurrentLocationId(), crime.getLocationId());
        double responseTime = distance / 30.0; // Assume 30 km/h average speed
        
        // Apply priority weighting (higher priority crimes get lower cost)
        double priorityWeight = getPriorityWeight(crime.getSeverity());
        
        return responseTime / priorityWeight;
    }
    
    /**
     * Get priority weight for cost calculation
     */
    private double getPriorityWeight(String severity) {
        switch (severity.toUpperCase()) {
            case "CRITICAL": return 4.0;
            case "HIGH": return 3.0;
            case "MEDIUM": return 2.0;
            case "LOW": return 1.0;
            default: return 1.0;
        }
    }
    
    /**
     * Get numeric priority value for sorting
     */
    private int getPriorityValue(String severity) {
        switch (severity.toUpperCase()) {
            case "CRITICAL": return 4;
            case "HIGH": return 3;
            case "MEDIUM": return 2;
            case "LOW": return 1;
            default: return 1;
        }
    }
    
    /**
     * Create Assignment object
     */
    private Assignment createAssignment(int unitId, int crimeId, double responseTime) {
        Assignment assignment = new Assignment();
        assignment.setAssignId(nextAssignmentId++);
        assignment.setUnitId(unitId);
        assignment.setCrimeId(crimeId);
        assignment.setTimeAssigned(new Timestamp(System.currentTimeMillis()));
        assignment.setResponseTime(responseTime);
        return assignment;
    }
    
    /**
     * Emergency assignment for high-priority crimes
     */
    public Assignment emergencyAssignment(Unit unit, Crime crime) {
        double cost = calculateAssignmentCost(unit, crime);
        Assignment assignment = createAssignment(unit.getUnitId(), crime.getCrimeId(), cost);
        assignmentDAO.insertAssignment(assignment);
        
        System.out.printf("Emergency assignment: Unit %d -> Crime %d\n", 
            unit.getUnitId(), crime.getCrimeId());
        
        return assignment;
    }
    
    /**
     * Simple distance calculation - can be replaced with PathfindingService integration
     */
    private double calculateSimpleDistance(int locationId1, int locationId2) {
        // For now, return a simple distance based on location ID difference
        // This is a placeholder until PathfindingService integration is completed
        return Math.abs(locationId1 - locationId2) * 0.5; // Arbitrary distance calculation
    }
}
