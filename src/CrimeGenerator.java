// CrimeGenerator.java
// Module 3: Crime & Event Generation
// Generates new crime events and manages active crimes

// Expected Output Example:
// - Generates random or scheduled crimes
// - Maintains active crime list
// - Example usage:
//   CrimeGenerator cg = new CrimeGenerator(cityMap);
//   cg.generateCrime();
//   List<Crime> crimes = cg.getActiveCrimes();

import java.util.*;
import java.sql.Timestamp;
import java.util.concurrent.ThreadLocalRandom;
import models.Crime;
import database.CrimeDAO;

/**
 * CrimeGenerator handles the generation and management of crime events
 * in the police response simulation system.
 */
public class CrimeGenerator {
    private CityMap cityMap;
    private List<Crime> activeCrimes;
    private CrimeDAO crimeDAO;
    private Random random;
    private int nextCrimeId;
    
    // Crime type probabilities (can be adjusted for different scenarios)
    private static final String[] CRIME_TYPES = {
        "THEFT", "ASSAULT", "BURGLARY", "VANDALISM", "DRUG_OFFENSE", 
        "ROBBERY", "DOMESTIC_VIOLENCE", "FRAUD", "TRAFFIC_VIOLATION", "EMERGENCY"
    };
    
    // Crime severity levels with their probabilities
    private static final String[] SEVERITY_LEVELS = {"LOW", "MEDIUM", "HIGH", "CRITICAL"};
    private static final double[] SEVERITY_PROBABILITIES = {0.4, 0.35, 0.2, 0.05};
    
    // Crime generation parameters
    private double baseGenerationRate = 0.1; // Base probability of generating a crime per cycle
    private int maxActiveCrimes = 50;
    
    /**
     * Constructor for CrimeGenerator
     * @param cityMap The city map containing location information
     */
    public CrimeGenerator(CityMap cityMap) {
        this.cityMap = cityMap;
        this.activeCrimes = new ArrayList<>();
        this.crimeDAO = new CrimeDAO();
        this.random = new Random();
        this.nextCrimeId = 1;
    }
    
    /**
     * Generates a new random crime event
     * @return The generated Crime object, or null if no crime was generated
     */
    public Crime generateCrime() {
        // Check if we should generate a crime based on probability
        if (random.nextDouble() > baseGenerationRate || activeCrimes.size() >= maxActiveCrimes) {
            return null;
        }
        
        // Generate random crime properties
        int locationId = generateRandomLocationId();
        String crimeType = CRIME_TYPES[random.nextInt(CRIME_TYPES.length)];
        String severity = generateSeverity();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        
        // Create new crime
        Crime crime = new Crime();
        crime.setCrimeId(nextCrimeId++);
        crime.setLocationId(locationId);
        crime.setTimestamp(timestamp);
        crime.setSeverity(severity);
        crime.setType(crimeType);
        crime.setStatus("ACTIVE");
        
        // Add to active crimes list
        activeCrimes.add(crime);
        
        // Persist to database
        crimeDAO.insertCrime(crime);
        
        System.out.println("Generated new crime: " + crimeType + " (" + severity + ") at location " + locationId);
        
        return crime;
    }
    
    /**
     * Generates a crime with specific parameters
     * @param locationId The location where the crime occurs
     * @param crimeType The type of crime
     * @param severity The severity level
     * @return The generated Crime object
     */
    public Crime generateSpecificCrime(int locationId, String crimeType, String severity) {
        Crime crime = new Crime();
        crime.setCrimeId(nextCrimeId++);
        crime.setLocationId(locationId);
        crime.setTimestamp(new Timestamp(System.currentTimeMillis()));
        crime.setSeverity(severity);
        crime.setType(crimeType);
        crime.setStatus("ACTIVE");
        
        activeCrimes.add(crime);
        crimeDAO.insertCrime(crime);
        
        System.out.println("Generated specific crime: " + crimeType + " (" + severity + ") at location " + locationId);
        
        return crime;
    }
    
    /**
     * Generates multiple crimes for testing or scenario setup
     * @param count Number of crimes to generate
     * @return List of generated crimes
     */
    public List<Crime> generateMultipleCrimes(int count) {
        List<Crime> generatedCrimes = new ArrayList<>();
        
        for (int i = 0; i < count && activeCrimes.size() < maxActiveCrimes; i++) {
            Crime crime = generateCrime();
            if (crime != null) {
                generatedCrimes.add(crime);
            }
        }
        
        return generatedCrimes;
    }
    
    /**
     * Gets all currently active crimes
     * @return List of active Crime objects
     */
    public List<Crime> getActiveCrimes() {
        return new ArrayList<>(activeCrimes);
    }
    
    /**
     * Gets crimes by severity level
     * @param severity The severity level to filter by
     * @return List of crimes matching the severity
     */
    public List<Crime> getCrimesBySeverity(String severity) {
        return activeCrimes.stream()
                .filter(crime -> crime.getSeverity().equals(severity))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Gets crimes by type
     * @param crimeType The crime type to filter by
     * @return List of crimes matching the type
     */
    public List<Crime> getCrimesByType(String crimeType) {
        return activeCrimes.stream()
                .filter(crime -> crime.getType().equals(crimeType))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Resolves a crime (marks it as resolved and removes from active list)
     * @param crimeId The ID of the crime to resolve
     * @return true if crime was found and resolved, false otherwise
     */
    public boolean resolveCrime(int crimeId) {
        Crime crime = findCrimeById(crimeId);
        if (crime != null) {
            crime.setStatus("RESOLVED");
            activeCrimes.remove(crime);
            crimeDAO.updateCrimeStatus(crimeId, "RESOLVED");
            System.out.println("Crime " + crimeId + " has been resolved.");
            return true;
        }
        return false;
    }
    
    /**
     * Updates the status of a crime
     * @param crimeId The ID of the crime
     * @param status The new status
     * @return true if update was successful, false otherwise
     */
    public boolean updateCrimeStatus(int crimeId, String status) {
        Crime crime = findCrimeById(crimeId);
        if (crime != null) {
            String oldStatus = crime.getStatus();
            crime.setStatus(status);
            crimeDAO.updateCrimeStatus(crimeId, status);
            
            // Remove from active list if status is no longer active
            if (!status.equals("ACTIVE") && !status.equals("IN_PROGRESS")) {
                activeCrimes.remove(crime);
            }
            
            System.out.println("Crime " + crimeId + " status updated from " + oldStatus + " to " + status);
            return true;
        }
        return false;
    }
    
    /**
     * Gets crime statistics
     * @return Map containing various crime statistics
     */
    public Map<String, Object> getCrimeStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalActiveCrimes", activeCrimes.size());
        stats.put("crimeGenerationRate", baseGenerationRate);
        stats.put("maxActiveCrimes", maxActiveCrimes);
        
        // Count crimes by severity
        Map<String, Long> severityCount = new HashMap<>();
        for (String severity : SEVERITY_LEVELS) {
            long count = activeCrimes.stream()
                    .filter(crime -> crime.getSeverity().equals(severity))
                    .count();
            severityCount.put(severity, count);
        }
        stats.put("crimesBySeverity", severityCount);
        
        // Count crimes by type
        Map<String, Long> typeCount = new HashMap<>();
        for (String type : CRIME_TYPES) {
            long count = activeCrimes.stream()
                    .filter(crime -> crime.getType().equals(type))
                    .count();
            typeCount.put(type, count);
        }
        stats.put("crimesByType", typeCount);
        
        return stats;
    }
    
    /**
     * Sets the crime generation rate
     * @param rate The new generation rate (0.0 to 1.0)
     */
    public void setGenerationRate(double rate) {
        this.baseGenerationRate = Math.max(0.0, Math.min(1.0, rate));
    }
    
    /**
     * Sets the maximum number of active crimes
     * @param max The maximum number of active crimes
     */
    public void setMaxActiveCrimes(int max) {
        this.maxActiveCrimes = Math.max(1, max);
    }
    
    /**
     * Clears all active crimes (for testing or reset purposes)
     */
    public void clearActiveCrimes() {
        activeCrimes.clear();
        System.out.println("All active crimes have been cleared.");
    }
    
    /**
     * Simulates time passage and potentially ages/escalates crimes
     */
    public void updateCrimes() {
        long currentTime = System.currentTimeMillis();
        List<Crime> crimesToUpdate = new ArrayList<>();
        
        for (Crime crime : activeCrimes) {
            long crimeAge = currentTime - crime.getTimestamp().getTime();
            
            // Escalate crimes that have been active for too long
            if (crimeAge > 300000 && !crime.getSeverity().equals("CRITICAL")) { // 5 minutes
                if (crime.getSeverity().equals("LOW")) {
                    crime.setSeverity("MEDIUM");
                    crimesToUpdate.add(crime);
                } else if (crime.getSeverity().equals("MEDIUM")) {
                    crime.setSeverity("HIGH");
                    crimesToUpdate.add(crime);
                } else if (crime.getSeverity().equals("HIGH")) {
                    crime.setSeverity("CRITICAL");
                    crimesToUpdate.add(crime);
                }
            }
        }
        
        // Update database for escalated crimes
        for (Crime crime : crimesToUpdate) {
            crimeDAO.updateCrimeStatus(crime.getCrimeId(), crime.getStatus());
            System.out.println("Crime " + crime.getCrimeId() + " escalated to " + crime.getSeverity());
        }
    }
    
    // Private helper methods
    
    private int generateRandomLocationId() {
        // Generate random location ID from available city map nodes
        if (cityMap != null) {
            // Get valid location IDs from the city map
            Set<Integer> validLocations = cityMap.getAllNodeIds();
            if (!validLocations.isEmpty()) {
                List<Integer> locationList = new ArrayList<>(validLocations);
                return locationList.get(ThreadLocalRandom.current().nextInt(locationList.size()));
            }
        }
        // Fallback: generate random location ID between 1 and 100
        return ThreadLocalRandom.current().nextInt(1, 101);
    }
    
    private String generateSeverity() {
        double rand = random.nextDouble();
        double cumulative = 0.0;
        
        for (int i = 0; i < SEVERITY_PROBABILITIES.length; i++) {
            cumulative += SEVERITY_PROBABILITIES[i];
            if (rand <= cumulative) {
                return SEVERITY_LEVELS[i];
            }
        }
        
        return SEVERITY_LEVELS[0]; // Default to LOW
    }
    
    private Crime findCrimeById(int crimeId) {
        return activeCrimes.stream()
                .filter(crime -> crime.getCrimeId() == crimeId)
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Gets the current crime generation rate
     * @return The current generation rate
     */
    public double getGenerationRate() {
        return baseGenerationRate;
    }
    
    /**
     * Gets the maximum number of active crimes allowed
     * @return The maximum number of active crimes
     */
    public int getMaxActiveCrimes() {
        return maxActiveCrimes;
    }
}
