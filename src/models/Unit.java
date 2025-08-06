package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Unit {
    private int unitId;
    private int currentLocationId;
    private String status;
    private String capabilities;
    private List<Integer> assignedCrimes;
    
    // Random stats for Hungarian algorithm optimization
    private double responseTimeMultiplier; // 0.8-1.2 (affects how fast unit responds)
    private int equipmentQuality; // 1-10 (affects crime resolution effectiveness)
    private String experienceLevel; // "ROOKIE", "VETERAN", "ELITE"
    private double fatigueLevel; // 0.0-1.0 (affects performance, increases with workload)
    private int totalAssignments; // Track workload for fatigue calculation
    
    // Default constructor
    public Unit() {
        this.assignedCrimes = new ArrayList<>();
        initializeRandomStats();
    }
    
    // Constructor with all fields
    public Unit(int unitId, int currentLocationId, String status, String capabilities) {
        this.unitId = unitId;
        this.currentLocationId = currentLocationId;
        this.status = status;
        this.capabilities = capabilities;
        this.assignedCrimes = new ArrayList<>();
        initializeRandomStats();
    }
    
    /**
     * Initialize random stats for Hungarian algorithm optimization
     */
    private void initializeRandomStats() {
        Random random = new Random();
        
        // Response time multiplier: 0.8 to 1.2 (faster to slower)
        this.responseTimeMultiplier = 0.8 + (random.nextDouble() * 0.4);
        
        // Equipment quality: 1-10
        this.equipmentQuality = 1 + random.nextInt(10);
        
        // Experience level based on random distribution
        double experienceRand = random.nextDouble();
        if (experienceRand < 0.3) {
            this.experienceLevel = "ROOKIE";
        } else if (experienceRand < 0.8) {
            this.experienceLevel = "VETERAN";
        } else {
            this.experienceLevel = "ELITE";
        }
        
        // Start with low fatigue
        this.fatigueLevel = random.nextDouble() * 0.2; // 0-20% initial fatigue
        this.totalAssignments = 0;
    }
    
    // Getters
    public int getUnitId() {
        return unitId;
    }
    
    public int getCurrentLocationId() {
        return currentLocationId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getCapabilities() {
        return capabilities;
    }
    
    public List<Integer> getAssignedCrimes() {
        return assignedCrimes;
    }
    
    public double getResponseTimeMultiplier() {
        return responseTimeMultiplier;
    }
    
    public int getEquipmentQuality() {
        return equipmentQuality;
    }
    
    public String getExperienceLevel() {
        return experienceLevel;
    }
    
    public double getFatigueLevel() {
        return fatigueLevel;
    }
    
    public int getTotalAssignments() {
        return totalAssignments;
    }
    
    /**
     * Calculate overall performance score for Hungarian algorithm
     */
    public double getPerformanceScore() {
        double experienceBonus;
        switch (experienceLevel) {
            case "ELITE":
                experienceBonus = 1.2;
                break;
            case "VETERAN":
                experienceBonus = 1.0;
                break;
            case "ROOKIE":
                experienceBonus = 0.8;
                break;
            default:
                experienceBonus = 1.0;
                break;
        }
        
        double equipmentBonus = equipmentQuality / 10.0; // Normalize to 0.1-1.0
        double fatigueBonus = 1.0 - fatigueLevel; // Less fatigue = better performance
        
        return (experienceBonus + equipmentBonus + fatigueBonus) / 3.0;
    }
    
    // Setters
    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }
    
    public void setCurrentLocationId(int currentLocationId) {
        this.currentLocationId = currentLocationId;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }
    
    public void setAssignedCrimes(List<Integer> assignedCrimes) {
        this.assignedCrimes = assignedCrimes;
    }
    
    /**
     * Add assignment and update fatigue level
     */
    public void addAssignment(int crimeId) {
        this.assignedCrimes.add(crimeId);
        this.totalAssignments++;
        
        // Increase fatigue with each assignment (max 0.9)
        this.fatigueLevel = Math.min(0.9, this.fatigueLevel + 0.1);
    }
    
    /**
     * Remove assignment and reduce fatigue slightly
     */
    public void removeAssignment(int crimeId) {
        this.assignedCrimes.remove(Integer.valueOf(crimeId));
        
        // Reduce fatigue when assignment completes (min 0.0)
        this.fatigueLevel = Math.max(0.0, this.fatigueLevel - 0.05);
    }
    
    /**
     * Reset fatigue (for shift changes, rest periods)
     */
    public void resetFatigue() {
        this.fatigueLevel = 0.0;
    }
    
    @Override
    public String toString() {
        return "Unit{" +
                "unitId=" + unitId +
                ", location=" + currentLocationId +
                ", status='" + status + '\'' +
                ", capabilities='" + capabilities + '\'' +
                ", experience='" + experienceLevel + '\'' +
                ", equipment=" + equipmentQuality +
                ", fatigue=" + String.format("%.2f", fatigueLevel) +
                ", performance=" + String.format("%.2f", getPerformanceScore()) +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Unit unit = (Unit) obj;
        return unitId == unit.unitId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(unitId);
    }
}
