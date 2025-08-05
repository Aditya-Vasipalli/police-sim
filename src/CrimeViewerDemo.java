// CrimeViewerDemo.java
// Simple demo to show crimes being generated and assignments

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class CrimeViewerDemo extends JFrame {
    private CrimeGenerator crimeGenerator;
    private CityMap cityMap;
    private JTextArea crimeLogArea;
    private JLabel statsLabel;
    private Timer crimeTimer;
    private int crimeCount = 0;
    
    public CrimeViewerDemo() {
        initializeComponents();
        setupCrimeGeneration();
    }
    
    private void initializeComponents() {
        setTitle("Police Simulation - Crime Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create crime log area
        crimeLogArea = new JTextArea(20, 50);
        crimeLogArea.setEditable(false);
        crimeLogArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(crimeLogArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        // Create stats label
        statsLabel = new JLabel("Crimes generated: 0");
        statsLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create control panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton generateButton = new JButton("Generate Crime Now");
        JButton startAutoButton = new JButton("Start Auto Generation");
        JButton stopAutoButton = new JButton("Stop Auto Generation");
        JButton clearButton = new JButton("Clear Log");
        
        generateButton.addActionListener(e -> generateSingleCrime());
        startAutoButton.addActionListener(e -> startAutoGeneration());
        stopAutoButton.addActionListener(e -> stopAutoGeneration());
        clearButton.addActionListener(e -> clearLog());
        
        controlPanel.add(generateButton);
        controlPanel.add(startAutoButton);
        controlPanel.add(stopAutoButton);
        controlPanel.add(clearButton);
        
        // Layout
        add(controlPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(statsLabel, BorderLayout.SOUTH);
        
        setSize(700, 500);
        setLocationRelativeTo(null);
    }
    
    private void setupCrimeGeneration() {
        try {
            // Initialize city map
            cityMap = new CityMap();
            cityMap.loadFromCSV("city_map.csv");
            
            // Initialize crime generator
            crimeGenerator = new CrimeGenerator(cityMap);
            
            appendToLog("Crime generation system initialized successfully!");
            appendToLog("City map loaded with " + cityMap.getNodeCount() + " locations");
            appendToLog("Click 'Generate Crime Now' to create crimes manually");
            appendToLog("Or click 'Start Auto Generation' for continuous crime generation");
            appendToLog("=" * 60);
            
        } catch (Exception e) {
            appendToLog("Error initializing crime generation: " + e.getMessage());
            appendToLog("Creating fallback crime generator...");
            crimeGenerator = new CrimeGenerator(null); // Fallback mode
        }
    }
    
    private void generateSingleCrime() {
        try {
            Crime crime = crimeGenerator.generateCrime();
            if (crime != null) {
                crimeCount++;
                displayCrime(crime);
                updateStats();
            } else {
                appendToLog("Failed to generate crime");
            }
        } catch (Exception e) {
            appendToLog("Error generating crime: " + e.getMessage());
        }
    }
    
    private void displayCrime(Crime crime) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("CRIME #%d GENERATED:\n", crimeCount));
        sb.append(String.format("  ID: %d\n", crime.getCrimeId()));
        sb.append(String.format("  Type: %s\n", crime.getType()));
        sb.append(String.format("  Severity: %s\n", crime.getSeverity()));
        sb.append(String.format("  Location: Node %d\n", crime.getLocationId()));
        sb.append(String.format("  Priority: %s\n", crime.getPriority()));
        sb.append(String.format("  Time: %s\n", new java.util.Date(crime.getTimestamp())));
        sb.append(String.format("  Description: %s\n", crime.getDescription()));
        sb.append("-".repeat(50) + "\n");
        
        appendToLog(sb.toString());
    }
    
    private void startAutoGeneration() {
        if (crimeTimer != null && crimeTimer.isRunning()) {
            return; // Already running
        }
        
        // Generate crime every 2 seconds
        crimeTimer = new Timer(2000, e -> generateSingleCrime());
        crimeTimer.start();
        
        appendToLog("*** AUTO CRIME GENERATION STARTED ***");
        appendToLog("Generating crimes every 2 seconds...\n");
    }
    
    private void stopAutoGeneration() {
        if (crimeTimer != null) {
            crimeTimer.stop();
            appendToLog("*** AUTO CRIME GENERATION STOPPED ***\n");
        }
    }
    
    private void clearLog() {
        crimeLogArea.setText("");
        crimeCount = 0;
        updateStats();
    }
    
    private void appendToLog(String text) {
        SwingUtilities.invokeLater(() -> {
            crimeLogArea.append(text + "\n");
            crimeLogArea.setCaretPosition(crimeLogArea.getDocument().getLength());
        });
    }
    
    private void updateStats() {
        statsLabel.setText("Crimes generated: " + crimeCount);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                // Use default look and feel
            }
            
            new CrimeViewerDemo().setVisible(true);
        });
    }
}
