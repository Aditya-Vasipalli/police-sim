package models;
import java.time.LocalDateTime;
import java.util.Objects;

public class Stat {
    private LocalDateTime timestamp;
    private int totalCrimes;
    private double avgResponseTime;
    private int solvedCount;
    private int activeUnits;
    private String crimeType;
    private int unitId;
    private double responseTime;
    private boolean resolved;
    private String location;
    private double efficiency; // Response time / distance ratio
    
    // Default constructor
    public Stat() {
        this.timestamp = LocalDateTime.now();
    }
    
    // Constructor for basic statistics
    public Stat(LocalDateTime timestamp, int totalCrimes, double avgResponseTime, int solvedCount) {
        this.timestamp = timestamp;
        this.totalCrimes = totalCrimes;
        this.avgResponseTime = avgResponseTime;
        this.solvedCount = solvedCount;
    }
    
    // Constructor for detailed crime response statistics
    public Stat(String crimeType, int unitId, double responseTime, boolean resolved, String location) {
        this.timestamp = LocalDateTime.now();
        this.crimeType = crimeType;
        this.unitId = unitId;
        this.responseTime = responseTime;
        this.resolved = resolved;
        this.location = location;
    }
    
    // Getters
    public LocalDateTime getTimestamp() { return timestamp; }
    public int getTotalCrimes() { return totalCrimes; }
    public double getAvgResponseTime() { return avgResponseTime; }
    public int getSolvedCount() { return solvedCount; }
    public int getActiveUnits() { return activeUnits; }
    public String getCrimeType() { return crimeType; }
    public int getUnitId() { return unitId; }
    public double getResponseTime() { return responseTime; }
    public boolean isResolved() { return resolved; }
    public String getLocation() { return location; }
    public double getEfficiency() { return efficiency; }
    
    // Setters
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setTotalCrimes(int totalCrimes) { this.totalCrimes = totalCrimes; }
    public void setAvgResponseTime(double avgResponseTime) { this.avgResponseTime = avgResponseTime; }
    public void setSolvedCount(int solvedCount) { this.solvedCount = solvedCount; }
    public void setActiveUnits(int activeUnits) { this.activeUnits = activeUnits; }
    public void setCrimeType(String crimeType) { this.crimeType = crimeType; }
    public void setUnitId(int unitId) { this.unitId = unitId; }
    public void setResponseTime(double responseTime) { this.responseTime = responseTime; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }
    public void setLocation(String location) { this.location = location; }
    public void setEfficiency(double efficiency) { this.efficiency = efficiency; }
    
    // Utility methods
    public double getResolutionRate() {
        return totalCrimes > 0 ? (double) solvedCount / totalCrimes : 0.0;
    }
    
    public boolean isWithinTargetTime(double targetMinutes) {
        return responseTime <= targetMinutes;
    }
    
    public String getPerformanceGrade() {
        if (responseTime <= 5.0) return "A";
        else if (responseTime <= 10.0) return "B";
        else if (responseTime <= 15.0) return "C";
        else if (responseTime <= 20.0) return "D";
        else return "F";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Stat stat = (Stat) obj;
        return Objects.equals(timestamp, stat.timestamp) &&
               Objects.equals(crimeType, stat.crimeType) &&
               unitId == stat.unitId;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(timestamp, crimeType, unitId);
    }
    
    @Override
    public String toString() {
        if (crimeType != null) {
            return String.format("Stat{timestamp=%s, crimeType='%s', unitId=%d, responseTime=%.2f, resolved=%s}", 
                timestamp, crimeType, unitId, responseTime, resolved);
        } else {
            return String.format("Stat{timestamp=%s, totalCrimes=%d, avgResponseTime=%.2f, solvedCount=%d}", 
                timestamp, totalCrimes, avgResponseTime, solvedCount);
        }
    }
}
