package gui;

import models.Graph;
import java.awt.*;
import javax.swing.*;

/**
 * Simple test class to verify PoliceSimulatorGUI works
 */
public class GUITest {
    public static void main(String[] args) {
        // Create a simple test graph
        Graph testGraph = new Graph();
        testGraph.addEdge(0, 1, 1.0);
        testGraph.addEdge(1, 2, 1.0);
        testGraph.addEdge(2, 3, 1.0);
        
        System.out.println("Creating GUI window...");
        SwingUtilities.invokeLater(() -> {
            PoliceSimulatorGUI gui = new PoliceSimulatorGUI(testGraph);
            gui.setVisible(true);
            System.out.println("GUI window created and should be visible.");
        });
        
        // Keep the main thread alive so GUI stays open
        System.out.println("GUI test started. Close the window to exit.");
    }
}
