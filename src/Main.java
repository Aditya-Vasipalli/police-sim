// Main.java
// Entry point for the Police Response Time Simulator
// Main.java
// Entry point for the Police Response Time Simulator

// Expected Output Example:
// - Initializes all modules and starts the simulation
// - Example usage:
//   java Main
//   (Console output: Simulation started, events processed, summary printed)

import java.util.Scanner;
import java.util.Arrays;
import models.Graph;

public class Main {
    public static void main(String[] args) {
            // If "gui" argument is provided, launch GUI
            boolean useGUI = Arrays.asList(args).contains("gui");
            System.out.println("Args received: " + Arrays.toString(args));
            System.out.println("Use GUI: " + useGUI);
            if (useGUI) {
                System.out.println("Launching GUI mode...");
                try {
                    CityMap cityMap = new CityMap("big_city_map.csv");
                    // GraphVisualizer expects a Graph, so pass cityMap.getGraph() or similar
                    Graph cityGraph = cityMap.getGraph();
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        System.out.println("Creating GUI window...");
                        new gui.PoliceSimulatorGUI(cityGraph).setVisible(true);
                        System.out.println("GUI window should be visible now.");
                    });
                    System.out.println("GUI launch completed.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("=== Police Response Time Simulator ===");
                System.out.println("Initializing simulation modules...");
                try {
                    // ...existing code...
                    System.out.println("Loading city map...");
                    CityMap cityMap = new CityMap("big_city_map.csv");
                    System.out.println("Loaded city map with " + cityMap.getTotalNodes() + " nodes and " + cityMap.getTotalEdges() + " edges");
                    System.out.println("Initializing pathfinding service...");
                    PathfindingService pathfindingService = new PathfindingService(cityMap);
                    System.out.println("Initializing crime generator...");
                    CrimeGenerator crimeGenerator = new CrimeGenerator(cityMap);
                    System.out.println("Initializing simulation core with station-based police...");
                    SimulatorCore simulatorCore = SimulatorCore.createWithStationBasedPolice(cityMap, crimeGenerator, pathfindingService);
                    System.out.println("All modules initialized successfully!");
                    System.out.println("=====================================");
                    runInteractiveMode(simulatorCore);
                } catch (Exception e) {
                    System.err.println("Error initializing simulation: " + e.getMessage());
                    e.printStackTrace();
                }
            }
    }
    
    /**
     * Run interactive mode for user control
     */
    private static void runInteractiveMode(SimulatorCore simulator) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        
        while (!exit) {
            printMenu();
            System.out.print("Enter choice: ");
            
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                
                switch (choice) {
                    case 1:
                        System.out.print("Enter number of simulation ticks (default 100): ");
                        String input = scanner.nextLine().trim();
                        int ticks = input.isEmpty() ? 100 : Integer.parseInt(input);
                        simulator.run(ticks);
                        break;
                        
                    case 2:
                        simulator.stop();
                        break;
                        
                    case 3:
                        simulator.pause();
                        break;
                        
                    case 4:
                        simulator.resume();
                        break;
                        
                    case 5:
                        printStatistics(simulator);
                        break;
                        
                    case 6:
                        generateTestCrimes(simulator);
                        break;
                        
                    case 7:
                        showEventLog(simulator);
                        break;
                        
                    case 8:
                        simulator.emergencyStop();
                        break;
                        
                    case 9:
                        testPathfinding(simulator);
                        break;
                        
                    case 0:
                        exit = true;
                        if (simulator.isRunning()) {
                            simulator.stop();
                        }
                        System.out.println("Exiting simulator. Goodbye!");
                        break;
                        
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
                
            } catch (Exception e) {
                System.out.println("Error processing input: " + e.getMessage());
                scanner.nextLine(); // Clear invalid input
            }
        }
        
        scanner.close();
    }
    
    /**
     * Print the interactive menu
     */
    private static void printMenu() {
        System.out.println("\n=== SIMULATION CONTROL MENU ===");
        System.out.println("1. Start Simulation");
        System.out.println("2. Stop Simulation");
        System.out.println("3. Pause Simulation");
        System.out.println("4. Resume Simulation");
        System.out.println("5. Show Statistics");
        System.out.println("6. Generate Test Crimes");
        System.out.println("7. Show Event Log");
        System.out.println("8. Emergency Stop");
        System.out.println("9. Test Pathfinding");
        System.out.println("0. Exit");
        System.out.println("==============================");
    }
    
    /**
     * Print current simulation statistics
     */
    private static void printStatistics(SimulatorCore simulator) {
        System.out.println("\n=== CURRENT STATISTICS ===");
        var stats = simulator.getStatistics();
        for (var entry : stats.entrySet()) {
            System.out.printf("%s: %s\n", entry.getKey(), entry.getValue());
        }
        System.out.println("=========================");
    }
    
    /**
     * Generate test crimes for demonstration
     */
    private static void generateTestCrimes(SimulatorCore simulator) {
        System.out.println("Generating test crimes...");
        CrimeGenerator generator = simulator.getCrimeGenerator();
        
        // Generate some test crimes
        generator.generateSpecificCrime(1, "ROBBERY", "HIGH");
        generator.generateSpecificCrime(3, "ASSAULT", "MEDIUM");
        generator.generateSpecificCrime(5, "BURGLARY", "LOW");
        generator.generateSpecificCrime(7, "EMERGENCY", "CRITICAL");
        
        System.out.println("Generated 4 test crimes.");
        System.out.println("Active crimes: " + generator.getActiveCrimes().size());
    }
    
    /**
     * Show recent event log entries
     */
    private static void showEventLog(SimulatorCore simulator) {
        System.out.println("\n=== RECENT EVENTS ===");
        var eventLog = simulator.getEventLog();
        int startIndex = Math.max(0, eventLog.size() - 10); // Show last 10 events
        
        for (int i = startIndex; i < eventLog.size(); i++) {
            System.out.println(eventLog.get(i));
        }
        System.out.println("===================");
    }
    
    /**
     * Test pathfinding functionality
     */
    private static void testPathfinding(SimulatorCore simulator) {
        System.out.println("\n=== PATHFINDING TEST ===");
        PathfindingService pathfinding = simulator.getPathfindingService();
        
        // Test pathfinding between nodes 0 and 8 (opposite corners in default 3x3 grid)
        var result = pathfinding.calculateShortestPath(0, 8);
        
        if (result.isValidPath()) {
            System.out.println("Path from node 0 to node 8:");
            System.out.println("Path: " + result.getPath());
            System.out.printf("Distance: %.2f\n", result.getDistance());
            System.out.printf("Algorithm: %s\n", result.getAlgorithm());
            System.out.printf("Computation time: %d ms\n", result.getComputationTime());
        } else {
            System.out.println("No path found between nodes 0 and 8");
        }
        System.out.println("======================");
    }
}
