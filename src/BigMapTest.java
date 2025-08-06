public class BigMapTest {
    public static void main(String[] args) {
        System.out.println("=== Big City Map Police Simulation Test ===");
        try {
            // Initialize with big city map
            CityMap cityMap = new CityMap("big_city_map.csv");
            System.out.println("Loaded city map with " + cityMap.getTotalNodes() + " nodes");
            
            PathfindingService pathfindingService = new PathfindingService(cityMap);
            CrimeGenerator crimeGenerator = new CrimeGenerator(cityMap);
            
            // Create simulation with station-based police
            SimulatorCore simulator = SimulatorCore.createWithStationBasedPolice(
                cityMap, crimeGenerator, pathfindingService);
            
            System.out.println("Starting 50-tick simulation...");
            simulator.run(50);
            
            // Wait for simulation to complete
            while (simulator.isRunning()) {
                Thread.sleep(100);
            }
            
            System.out.println("Test completed!");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
