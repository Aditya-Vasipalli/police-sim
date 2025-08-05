package models;

public class Unit {
    private int unitId;
    private int currentLocationId;
    private String status;
    private String capabilities;
    
    // Default constructor
    public Unit() {
    }
    
    // Constructor with all fields
    public Unit(int unitId, int currentLocationId, String status, String capabilities) {
        this.unitId = unitId;
        this.currentLocationId = currentLocationId;
        this.status = status;
        this.capabilities = capabilities;
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
    
    @Override
    public String toString() {
        return "Unit{" +
                "unitId=" + unitId +
                ", currentLocationId=" + currentLocationId +
                ", status='" + status + '\'' +
                ", capabilities='" + capabilities + '\'' +
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
