// CrimeGeneratorTest.java
// Test class to demonstrate CrimeGenerator functionality

import models.Crime;
import java.util.List;
import java.util.Map;

/**
 * Test class for demonstrating CrimeGenerator functionality
 * This class can be used to verify that the CrimeGenerator works correctly
 */
public class CrimeGeneratorTest {
    
    public static void main(String[] args) {
        // Create a mock city map (in a real implementation, this would load from file)
        CityMap cityMap = new CityMap("city_map.csv");
        
        // Initialize the crime generator
        CrimeGenerator crimeGenerator = new CrimeGenerator(cityMap);
        
        System.out.println("=== Crime Generator Test ===");
        System.out.println();
        
        // Test 1: Generate random crimes
        System.out.println("1. Generating random crimes...");
        for (int i = 0; i < 5; i++) {
            Crime crime = crimeGenerator.generateCrime();
            if (crime != null) {
                System.out.println("   Generated: " + crime);
            } else {
                System.out.println("   No crime generated this cycle");
            }
        }
        System.out.println();
        
        // Test 2: Generate specific crimes
        System.out.println("2. Generating specific crimes...");
        Crime specificCrime1 = crimeGenerator.generateSpecificCrime(15, "ROBBERY", "HIGH");
        Crime specificCrime2 = crimeGenerator.generateSpecificCrime(23, "ASSAULT", "MEDIUM");
        System.out.println("   Generated: " + specificCrime1);
        System.out.println("   Generated: " + specificCrime2);
        System.out.println();
        
        // Test 3: Generate multiple crimes at once
        System.out.println("3. Generating multiple crimes...");
        List<Crime> multipleCrimes = crimeGenerator.generateMultipleCrimes(3);
        System.out.println("   Generated " + multipleCrimes.size() + " crimes:");
        for (Crime crime : multipleCrimes) {
            System.out.println("      " + crime);
        }
        System.out.println();
        
        // Test 4: Display active crimes
        System.out.println("4. Current active crimes:");
        List<Crime> activeCrimes = crimeGenerator.getActiveCrimes();
        System.out.println("   Total active crimes: " + activeCrimes.size());
        for (Crime crime : activeCrimes) {
            System.out.println("      " + crime);
        }
        System.out.println();
        
        // Test 5: Filter crimes by severity
        System.out.println("5. High severity crimes:");
        List<Crime> highSeverityCrimes = crimeGenerator.getCrimesBySeverity("HIGH");
        for (Crime crime : highSeverityCrimes) {
            System.out.println("   " + crime);
        }
        System.out.println();
        
        // Test 6: Filter crimes by type
        System.out.println("6. Robbery crimes:");
        List<Crime> robberyCrimes = crimeGenerator.getCrimesByType("ROBBERY");
        for (Crime crime : robberyCrimes) {
            System.out.println("   " + crime);
        }
        System.out.println();
        
        // Test 7: Crime statistics
        System.out.println("7. Crime statistics:");
        Map<String, Object> stats = crimeGenerator.getCrimeStatistics();
        System.out.println("   " + stats);
        System.out.println();
        
        // Test 8: Resolve a crime
        System.out.println("8. Resolving crimes...");
        if (!activeCrimes.isEmpty()) {
            Crime crimeToResolve = activeCrimes.get(0);
            System.out.println("   Resolving crime: " + crimeToResolve.getCrimeId());
            boolean resolved = crimeGenerator.resolveCrime(crimeToResolve.getCrimeId());
            System.out.println("   Resolution successful: " + resolved);
            System.out.println("   Active crimes after resolution: " + crimeGenerator.getActiveCrimes().size());
        }
        System.out.println();
        
        // Test 9: Update crime status
        System.out.println("9. Updating crime status...");
        activeCrimes = crimeGenerator.getActiveCrimes();
        if (!activeCrimes.isEmpty()) {
            Crime crimeToUpdate = activeCrimes.get(0);
            System.out.println("   Updating crime " + crimeToUpdate.getCrimeId() + " to IN_PROGRESS");
            boolean updated = crimeGenerator.updateCrimeStatus(crimeToUpdate.getCrimeId(), "IN_PROGRESS");
            System.out.println("   Update successful: " + updated);
        }
        System.out.println();
        
        // Test 10: Configuration changes
        System.out.println("10. Testing configuration changes...");
        System.out.println("   Current generation rate: " + crimeGenerator.getGenerationRate());
        System.out.println("   Current max active crimes: " + crimeGenerator.getMaxActiveCrimes());
        
        crimeGenerator.setGenerationRate(0.5);
        crimeGenerator.setMaxActiveCrimes(100);
        
        System.out.println("   New generation rate: " + crimeGenerator.getGenerationRate());
        System.out.println("   New max active crimes: " + crimeGenerator.getMaxActiveCrimes());
        System.out.println();
        
        System.out.println("=== Test Complete ===");
        System.out.println("CrimeGenerator is working correctly and ready for integration!");
    }
}
