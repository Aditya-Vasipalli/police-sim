// GUIDemo.java
// Simple demo to test individual GUI components

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class GUIDemo {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("=== GUI Components Demo ===");
            
            // Test 1: Police Assignment Panel
            testAssignmentPanel();
            
            // Test 2: Crime Feed Panel  
            testCrimeFeedPanel();
            
            // Test 3: Pathfinding Panel
            testPathfindingPanel();
            
            System.out.println("GUI component demos launched!");
        });
    }
    
    private static void testAssignmentPanel() {
        JFrame frame = new JFrame("Police Assignment Panel Test");
        PoliceAssignmentPanel panel = new PoliceAssignmentPanel();
        
        // Add sample assignments
        Map<Integer, PoliceAssignmentPanel.AssignmentInfo> assignments = new HashMap<>();
        
        assignments.put(1, new PoliceAssignmentPanel.AssignmentInfo(
            1, 101, "Theft", 5, "High", "Dispatched", 3.5, 2.1));
        assignments.put(2, new PoliceAssignmentPanel.AssignmentInfo(
            2, 102, "Assault", 8, "Medium", "En Route", 5.2, 3.8));
        assignments.put(3, new PoliceAssignmentPanel.AssignmentInfo(
            3, 103, "Burglary", 12, "Low", "Resolved", 8.1, 4.5));
            
        panel.updateAssignments(assignments);
        
        frame.add(panel);
        frame.setSize(900, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private static void testCrimeFeedPanel() {
        JFrame frame = new JFrame("Crime Feed Panel Test");
        CrimeFeedPanel panel = new CrimeFeedPanel();
        
        // Add sample crimes
        panel.addCrimeToFeed(new CrimeFeedPanel.CrimeEvent(
            1, "Theft", 5, "High", "Reported", "Vehicle theft in parking lot"));
        panel.addCrimeToFeed(new CrimeFeedPanel.CrimeEvent(
            2, "Assault", 8, "Medium", "In Progress", "Domestic dispute"));
        panel.addCrimeToFeed(new CrimeFeedPanel.CrimeEvent(
            3, "Burglary", 12, "Low", "Resolved", "Break-in at residence"));
        panel.addCrimeToFeed(new CrimeFeedPanel.CrimeEvent(
            4, "Traffic", 3, "Medium", "Reported", "Traffic accident"));
            
        frame.add(panel);
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private static void testPathfindingPanel() {
        JFrame frame = new JFrame("Pathfinding Panel Test");
        PathfindingPanel panel = new PathfindingPanel();
        
        // Load city map
        CityMap cityMap = new CityMap("city_map.csv");
        panel.setCityMap(cityMap.getAllNodes());
        
        // Add sample paths
        java.util.List<Integer> path1 = Arrays.asList(0, 3, 6, 7);
        java.util.List<Integer> path2 = Arrays.asList(1, 4, 5, 8);
        
        panel.drawPath(path1, Color.RED, "Unit 1 -> Crime A");
        panel.drawPath(path2, Color.BLUE, "Unit 2 -> Crime B");
        
        frame.add(panel);
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
