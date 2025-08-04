package models;
import java.sql.Timestamp;

public class Crime {
    private int crimeId;
    private int locationId;
    private Timestamp timestamp;
    private String severity;
    private String type;
    private String status;
    
    // Default constructor
    public Crime() {
    }
    
    // Constructor with all fields
    public Crime(int crimeId, int locationId, Timestamp timestamp, String severity, String type, String status) {
        this.crimeId = crimeId;
        this.locationId = locationId;
        this.timestamp = timestamp;
        this.severity = severity;
        this.type = type;
        this.status = status;
    }
    
    // Getters
    public int getCrimeId() {
        return crimeId;
    }
    
    public int getLocationId() {
        return locationId;
    }
    
    public Timestamp getTimestamp() {
        return timestamp;
    }
    
    public String getSeverity() {
        return severity;
    }
    
    public String getType() {
        return type;
    }
    
    public String getStatus() {
        return status;
    }
    
    // Setters
    public void setCrimeId(int crimeId) {
        this.crimeId = crimeId;
    }
    
    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }
    
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    
    public void setSeverity(String severity) {
        this.severity = severity;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "Crime{" +
                "crimeId=" + crimeId +
                ", locationId=" + locationId +
                ", timestamp=" + timestamp +
                ", severity='" + severity + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Crime crime = (Crime) obj;
        return crimeId == crime.crimeId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(crimeId);
    }
}
