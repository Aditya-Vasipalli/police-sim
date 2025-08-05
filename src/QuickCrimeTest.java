// QuickCrimeTest.java
// Quick test to show crime generation

public class QuickCrimeTest {
    public static void main(String[] args) {
        System.out.println("=== CRIME GENERATION TEST ===");
        
        try {
            // Initialize city map
            CityMap cityMap = new CityMap();
            cityMap.loadFromCSV("city_map.csv");
            System.out.println("City map loaded: " + cityMap.getNodeCount() + " nodes");
            
            // Initialize crime generator
            CrimeGenerator crimeGenerator = new CrimeGenerator(cityMap);
            System.out.println("Crime generator initialized");
            
            // Generate 5 crimes
            System.out.println("\nGenerating 5 crimes:");
            System.out.println("=".repeat(50));
            
            for (int i = 1; i <= 5; i++) {
                Crime crime = crimeGenerator.generateCrime();
                if (crime != null) {
                    System.out.printf("Crime %d: %s (%s) at Node %d - Priority: %s%n",
                        i, crime.getType(), crime.getSeverity(), 
                        crime.getLocationId(), crime.getPriority());
                } else {
                    System.out.println("Crime " + i + ": Failed to generate");
                }
            }
            
            System.out.println("\n=== TEST COMPLETE ===");
            System.out.println("These crimes would appear in:");
            System.out.println("1. Crime Feed Panel (live scrolling list)");
            System.out.println("2. Police Assignment Panel (when units assigned)");
            System.out.println("3. Console logs during simulation");
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
