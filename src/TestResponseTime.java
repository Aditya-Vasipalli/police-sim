// TestResponseTime.java - Test the response time calculation fix

public class TestResponseTime {
    public static void main(String[] args) {
        System.out.println("=== Response Time Test ===");
        
        try {
            // Initialize modules
            CityMap cityMap = new CityMap("big_city_map.csv");
            PathfindingService pathfindingService = new PathfindingService(cityMap);
            CrimeGenerator crimeGenerator = new CrimeGenerator(cityMap);
            SimulatorCore simulatorCore = SimulatorCore.createWithStationBasedPolice(cityMap, crimeGenerator, pathfindingService);
            
            System.out.println("Running 30-tick simulation...");
            simulatorCore.run(30);
            
            // Print final statistics
            var stats = simulatorCore.getStatistics();
            System.out.println("\n=== FINAL STATISTICS ===");
            for (var entry : stats.entrySet()) {
                System.out.printf("%s: %s\n", entry.getKey(), entry.getValue());
            }
            System.out.println("========================");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
