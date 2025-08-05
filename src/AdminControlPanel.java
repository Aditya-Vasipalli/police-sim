// AdminControlPanel.java
// Admin Control Panel for managing the police simulation
// Provides buttons to start/pause sim, generate crimes, and view logs

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Admin Control Panel for managing the police simulation
 * Provides buttons to start/pause sim, generate crimes, and view logs
 */
public class AdminControlPanel extends JPanel {
    
    private SimulatorCore simulatorCore;
    private JButton startButton, stopButton, pauseButton, resumeButton;
    private JButton generateCrimesButton, emergencyStopButton;
    private JTextArea statisticsArea, logArea;
    private JTextField ticksField;
    private Timer updateTimer;
    
    public AdminControlPanel() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        // Update statistics every 2 seconds
        updateTimer = new Timer(2000, e -> updateStatistics());
    }
    
    /**
     * Set the simulator core reference
     */
    public void setSimulatorCore(SimulatorCore simulatorCore) {
        this.simulatorCore = simulatorCore;
        updateStatistics();
    }
    
    /**
     * Initialize GUI components
     */
    private void initializeComponents() {
        // Control buttons
        startButton = new JButton("Start Simulation");
        stopButton = new JButton("Stop Simulation");
        pauseButton = new JButton("Pause");
        resumeButton = new JButton("Resume");
        generateCrimesButton = new JButton("Generate Test Crimes");
        emergencyStopButton = new JButton("EMERGENCY STOP");
        
        // Input field for ticks
        ticksField = new JTextField("100", 10);
        
        // Text areas
        statisticsArea = new JTextArea(8, 30);
        statisticsArea.setEditable(false);
        statisticsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        logArea = new JTextArea(10, 30);
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        
        // Style emergency stop button
        emergencyStopButton.setBackground(Color.RED);
        emergencyStopButton.setForeground(Color.WHITE);
        emergencyStopButton.setFont(emergencyStopButton.getFont().deriveFont(Font.BOLD));
        
        // Initial button states
        stopButton.setEnabled(false);
        pauseButton.setEnabled(false);
        resumeButton.setEnabled(false);
    }
    
    /**
     * Setup the layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Simulation Control"));
        
        // Control panel
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Row 1: Start controls
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("Ticks:"), gbc);
        gbc.gridx = 1;
        controlPanel.add(ticksField, gbc);
        gbc.gridx = 2;
        controlPanel.add(startButton, gbc);
        
        // Row 2: Basic controls
        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(stopButton, gbc);
        gbc.gridx = 1;
        controlPanel.add(pauseButton, gbc);
        gbc.gridx = 2;
        controlPanel.add(resumeButton, gbc);
        
        // Row 3: Additional controls
        gbc.gridx = 0; gbc.gridy = 2;
        controlPanel.add(generateCrimesButton, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        controlPanel.add(emergencyStopButton, gbc);
        
        // Statistics panel
        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));
        statsPanel.add(new JScrollPane(statisticsArea), BorderLayout.CENTER);
        
        // Log panel
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Event Log"));
        logPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);
        
        // Add to main panel
        add(controlPanel, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
        add(logPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        startButton.addActionListener(e -> startSimulation());
        stopButton.addActionListener(e -> stopSimulation());
        pauseButton.addActionListener(e -> pauseSimulation());
        resumeButton.addActionListener(e -> resumeSimulation());
        generateCrimesButton.addActionListener(e -> generateTestCrimes());
        emergencyStopButton.addActionListener(e -> emergencyStop());
    }
    
    /**
     * Start the simulation
     */
    private void startSimulation() {
        if (simulatorCore == null) {
            JOptionPane.showMessageDialog(this, "Simulator not initialized!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int ticks = Integer.parseInt(ticksField.getText().trim());
            if (ticks <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a positive number of ticks", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            simulatorCore.run(ticks);
            
            // Update button states
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            pauseButton.setEnabled(true);
            resumeButton.setEnabled(false);
            
            // Start updating statistics
            updateTimer.start();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number of ticks", "Invalid Input", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * Stop the simulation
     */
    private void stopSimulation() {
        if (simulatorCore != null) {
            simulatorCore.stop();
            
            // Update button states
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            pauseButton.setEnabled(false);
            resumeButton.setEnabled(false);
            
            // Stop updating statistics
            updateTimer.stop();
            updateStatistics(); // Final update
        }
    }
    
    /**
     * Pause the simulation
     */
    private void pauseSimulation() {
        if (simulatorCore != null) {
            simulatorCore.pause();
            
            // Update button states
            pauseButton.setEnabled(false);
            resumeButton.setEnabled(true);
        }
    }
    
    /**
     * Resume the simulation
     */
    private void resumeSimulation() {
        if (simulatorCore != null) {
            simulatorCore.resume();
            
            // Update button states
            pauseButton.setEnabled(true);
            resumeButton.setEnabled(false);
        }
    }
    
    /**
     * Generate test crimes
     */
    private void generateTestCrimes() {
        if (simulatorCore == null) {
            JOptionPane.showMessageDialog(this, "Simulator not initialized!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Generate test crimes
        CrimeGenerator generator = simulatorCore.getCrimeGenerator();
        generator.generateSpecificCrime(1, "ROBBERY", "HIGH");
        generator.generateSpecificCrime(3, "ASSAULT", "MEDIUM");
        generator.generateSpecificCrime(5, "BURGLARY", "LOW");
        generator.generateSpecificCrime(7, "EMERGENCY", "CRITICAL");
        
        JOptionPane.showMessageDialog(this, "Generated 4 test crimes", "Test Crimes Generated", JOptionPane.INFORMATION_MESSAGE);
        updateStatistics();
    }
    
    /**
     * Emergency stop
     */
    private void emergencyStop() {
        if (simulatorCore != null) {
            int result = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to perform an emergency stop?", 
                "Emergency Stop", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                simulatorCore.emergencyStop();
                
                // Update button states
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                pauseButton.setEnabled(false);
                resumeButton.setEnabled(false);
                
                // Stop updating statistics
                updateTimer.stop();
            }
        }
    }
    
    /**
     * Update statistics display
     */
    private void updateStatistics() {
        if (simulatorCore == null) {
            statisticsArea.setText("Simulator not initialized");
            return;
        }
        
        Map<String, Object> stats = simulatorCore.getStatistics();
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== SIMULATION STATISTICS ===\n");
        sb.append(String.format("Current Tick: %s\n", stats.get("currentTick")));
        sb.append(String.format("Running: %s\n", stats.get("isRunning")));
        sb.append(String.format("Total Crimes: %s\n", stats.get("totalCrimesGenerated")));
        sb.append(String.format("Total Assignments: %s\n", stats.get("totalAssignments")));
        sb.append(String.format("Avg Response Time: %.2f min\n", (Double) stats.get("averageResponseTime")));
        sb.append(String.format("Active Crimes: %s\n", stats.get("activeCrimes")));
        sb.append("============================");
        
        statisticsArea.setText(sb.toString());
        
        // Update log area
        updateEventLog();
    }
    
    /**
     * Update event log display
     */
    private void updateEventLog() {
        if (simulatorCore == null) return;
        
        java.util.List<String> eventLog = simulatorCore.getEventLog();
        StringBuilder sb = new StringBuilder();
        
        // Show last 15 events
        int startIndex = Math.max(0, eventLog.size() - 15);
        for (int i = startIndex; i < eventLog.size(); i++) {
            sb.append(eventLog.get(i)).append("\n");
        }
        
        logArea.setText(sb.toString());
        
        // Auto-scroll to bottom
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}
