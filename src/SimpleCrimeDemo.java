// SimpleCrimeDemo.java
// Demonstrates crime generation without dependencies

import java.util.Random;

public class SimpleCrimeDemo {
    public static void main(String[] args) {
        System.out.println("=== SIMPLE CRIME DEMONSTRATION ===\n");
        
        // Simulate crime generation
        String[] crimeTypes = {"Theft", "Vandalism", "Burglary", "Assault", "Traffic"};
        String[] severities = {"Low", "Medium", "High", "Critical"};
        String[] priorities = {"Low", "Medium", "High", "Emergency"};
        
        Random random = new Random();
        
        System.out.println("Generating 5 sample crimes that would appear in the system:\n");
        
        for (int i = 1; i <= 5; i++) {
            int crimeId = 1000 + i;
            String type = crimeTypes[random.nextInt(crimeTypes.length)];
            String severity = severities[random.nextInt(severities.length)];
            String priority = priorities[random.nextInt(priorities.length)];
            int location = random.nextInt(20) + 1; // Random node 1-20
            
            System.out.printf("CRIME #%d:\n", i);
            System.out.printf("  ID: %d\n", crimeId);
            System.out.printf("  Type: %s\n", type);
            System.out.printf("  Severity: %s\n", severity);
            System.out.printf("  Location: Node %d\n", location);
            System.out.printf("  Priority: %s\n", priority);
            System.out.printf("  Status: Reported\n");
            System.out.println("  " + "-".repeat(30));
        }
        
        System.out.println("\nThese crimes would appear in:");
        System.out.println("✓ Crime Feed Panel - immediately when generated");
        System.out.println("✓ Police Assignment Panel - when assigned to units");
        System.out.println("✓ Console logs during simulation");
        System.out.println("✓ Pathfinding visualization when units respond");
        
        System.out.println("\nTo see crimes in the Assignment Panel:");
        System.out.println("1. Ensure the simulation is running");
        System.out.println("2. Check that police units are available");
        System.out.println("3. Wait for automatic assignment or trigger manually");
    }
}
