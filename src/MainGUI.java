// MainGUI.java
// Dedicated GUI launcher for Police Response Time Simulator
// Separates GUI from console application

import javax.swing.*;
import java.awt.*;

public class MainGUI {
    public static void main(String[] args) {
        System.out.println("=== Police Response Time Simulator - GUI Mode ===");
        System.out.println("Initializing holographic interface...");
        
        try {
            // Set look and feel for a more modern appearance
            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            
            // Initialize core modules
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
            System.out.println("Launching holographic interface...");
            
            // Launch GUI in Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                gui.HolographicPoliceGUI gui = new gui.HolographicPoliceGUI(cityMap, simulatorCore);
                gui.setVisible(true);
                System.out.println("Holographic interface online!");
            });
            
        } catch (Exception e) {
            System.err.println("Error initializing GUI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
