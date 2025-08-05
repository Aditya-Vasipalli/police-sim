package models;

public class Assignment {
    private int assignId;
    private int unitId;
    private int crimeId;
    private java.sql.Timestamp timeAssigned;
    private double responseTime;
    
    // Default constructor
    public Assignment() {
    }
    
    // Constructor with all fields
    public Assignment(int assignId, int unitId, int crimeId, java.sql.Timestamp timeAssigned, double responseTime) {
        this.assignId = assignId;
        this.unitId = unitId;
        this.crimeId = crimeId;
        this.timeAssigned = timeAssigned;
        this.responseTime = responseTime;
    }
    
    // Getters
    public int getAssignId() {
        return assignId;
    }
    
    public int getUnitId() {
        return unitId;
    }
    
    public int getCrimeId() {
        return crimeId;
    }
    
    public java.sql.Timestamp getTimeAssigned() {
        return timeAssigned;
    }
    
    public double getResponseTime() {
        return responseTime;
    }
    
    // Setters
    public void setAssignId(int assignId) {
        this.assignId = assignId;
    }
    
    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }
    
    public void setCrimeId(int crimeId) {
        this.crimeId = crimeId;
    }
    
    public void setTimeAssigned(java.sql.Timestamp timeAssigned) {
        this.timeAssigned = timeAssigned;
    }
    
    public void setResponseTime(double responseTime) {
        this.responseTime = responseTime;
    }
    
    @Override
    public String toString() {
        return "Assignment{" +
                "assignId=" + assignId +
                ", unitId=" + unitId +
                ", crimeId=" + crimeId +
                ", timeAssigned=" + timeAssigned +
                ", responseTime=" + responseTime +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Assignment assignment = (Assignment) obj;
        return assignId == assignment.assignId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(assignId);
    }
}
