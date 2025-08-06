// PoliceSimulatorGUI.java
// Main GUI application for the Police Response Time Simulator

import javax.swing.*;
import java.awt.*;
import java.util.*;
import javax.swing.border.EmptyBorder;

public class PoliceSimulatorGUI extends JFrame {
    private CityMap cityMap;
    private PathfindingService pathfindingService;
    
    // GUI Components
    private PathfindingPanel pathPanel;
    private PoliceAssignmentPanel assignmentPanel;
    private CrimeFeedPanel crimePanel;
    private JTextArea logArea;
    private JLabel statusLabel;
    
    // Control Components
    private JButton startSimButton;
    private JButton stopSimButton;
    private JButton clearPathsButton;
    private JButton generateCrimeButton;
    private JComboBox<String> algorithmSelector;
    
    // Simulation state
    private javax.swing.Timer simulationTimer;
    private boolean isSimulationRunning = false;
    private Random random = new Random();
    private int crimeCounter = 0;
    private int unitCounter = 0;
    
    public PoliceSimulatorGUI() {
        initializeComponents();
        initializeSimulation();
        setupGUI();
        
        // Load default city map
        loadCityMap();
    }
    
    private void initializeComponents() {
        // Core simulation components
        cityMap = new CityMap("city_map.csv");
        pathfindingService = new PathfindingService(cityMap);
        
        // GUI panels
        pathPanel = new PathfindingPanel();
        assignmentPanel = new PoliceAssignmentPanel();
        crimePanel = new CrimeFeedPanel();
        
        // Controls
        startSimButton = new JButton("Start Simulation");
        stopSimButton = new JButton("Stop Simulation");
        clearPathsButton = new JButton("Clear Paths");
        generateCrimeButton = new JButton("Generate Crime");
        
        algorithmSelector = new JComboBox<>(new String[]{"A*", "Dijkstra", "Auto-Select"});
        
        // Log area
        logArea = new JTextArea(8, 50);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        // Status label
        statusLabel = new JLabel("Ready");
    }
    
    private void setupGUI() {
        setTitle("Police Response Time Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Left panel - Map and controls
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(pathPanel, BorderLayout.CENTER);
        leftPanel.add(createControlPanel(), BorderLayout.SOUTH);
        
        // Right panel - Assignment table and crime feed
        JPanel rightPanel = new JPanel(new BorderLayout());
        
        // Top right - Assignment table
        rightPanel.add(assignmentPanel, BorderLayout.CENTER);
        
        // Bottom right - Crime feed
        crimePanel.setPreferredSize(new Dimension(300, 200));
        rightPanel.add(crimePanel, BorderLayout.SOUTH);
        
        // Split pane for main content
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        mainSplitPane.setDividerLocation(600);
        mainSplitPane.setResizeWeight(0.6);
        
        // Bottom panel - Log area
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("System Log"));
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setPreferredSize(new Dimension(0, 150));
        bottomPanel.add(logScrollPane, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(statusLabel);
        statusPanel.setBorder(new EmptyBorder(2, 10, 2, 10));
        
        // Assemble main panel
        mainPanel.add(mainSplitPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
        
        // Setup event handlers
        setupEventHandlers();
        
        // Final setup
        pack();
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(BorderFactory.createTitledBorder("Simulation Controls"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Row 1 - Algorithm selection
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("Algorithm:"), gbc);
        gbc.gridx = 1;
        controlPanel.add(algorithmSelector, gbc);
        
        // Row 2 - Main controls
        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(startSimButton, gbc);
        gbc.gridx = 1;
        controlPanel.add(stopSimButton, gbc);
        
        // Row 3 - Additional controls
        gbc.gridx = 0; gbc.gridy = 2;
        controlPanel.add(generateCrimeButton, gbc);
        gbc.gridx = 1;
        controlPanel.add(clearPathsButton, gbc);
        
        return controlPanel;
    }
    
    private void setupEventHandlers() {
        startSimButton.addActionListener(e -> startSimulation());
        stopSimButton.addActionListener(e -> stopSimulation());
        clearPathsButton.addActionListener(e -> clearPaths());
        generateCrimeButton.addActionListener(e -> generateRandomCrime());
        
        // Initially disable stop button
        stopSimButton.setEnabled(false);
    }
    
    private void initializeSimulation() {
        // Create simulation timer (runs every 2 seconds)
        simulationTimer = new javax.swing.Timer(2000, e -> simulationStep());
    }
    
    private void loadCityMap() {
        try {
            pathPanel.setCityMap(cityMap.getAllNodes());
            logMessage("City map loaded: " + cityMap.getTotalNodes() + " nodes, " + 
                      cityMap.getTotalEdges() + " edges");
            updateStatus("City map loaded successfully");
        } catch (Exception e) {
            logMessage("Error loading city map: " + e.getMessage());
            updateStatus("Error loading city map");
        }
    }
    
    private void startSimulation() {
        isSimulationRunning = true;
        simulationTimer.start();
        
        startSimButton.setEnabled(false);
        stopSimButton.setEnabled(true);
        
        logMessage("Simulation started");
        updateStatus("Simulation running...");
    }
    
    private void stopSimulation() {
        isSimulationRunning = false;
        simulationTimer.stop();
        
        startSimButton.setEnabled(true);
        stopSimButton.setEnabled(false);
        
        logMessage("Simulation stopped");
        updateStatus("Simulation stopped");
    }
    
    private void simulationStep() {
        if (!isSimulationRunning) return;
        
        // Randomly generate crimes
        if (random.nextDouble() < 0.3) { // 30% chance per step
            generateRandomCrime();
        }
        
        // Simulate some responses being resolved
        if (random.nextDouble() < 0.2) { // 20% chance per step
            simulateResponseCompletion();
        }
    }
    
    private void generateRandomCrime() {
        // Crime types and priorities
        String[] crimeTypes = {"Theft", "Assault", "Burglary", "Vandalism", "Traffic", "Domestic"};
        String[] priorities = {"High", "Medium", "Low"};
        
        // Generate random crime
        int crimeId = ++crimeCounter;
        String crimeType = crimeTypes[random.nextInt(crimeTypes.length)];
        int location = random.nextInt(cityMap.getTotalNodes());
        String priority = priorities[random.nextInt(priorities.length)];
        
        // Create crime event
        CrimeFeedPanel.CrimeEvent crime = new CrimeFeedPanel.CrimeEvent(
            crimeId, crimeType, location, priority, "Reported", 
            "Auto-generated crime event"
        );
        
        // Add to crime feed
        crimePanel.addCrimeToFeed(crime);
        
        // Dispatch police unit
        dispatchPoliceUnit(crime);
        
        logMessage(String.format("Crime generated: %s at Node %d (Priority: %s)", 
                                crimeType, location, priority));
    }
    
    private void dispatchPoliceUnit(CrimeFeedPanel.CrimeEvent crime) {
        // Find nearby police unit (simulate)
        int unitId = ++unitCounter;
        int unitLocation = random.nextInt(cityMap.getTotalNodes());
        
        // Calculate path from unit to crime location
        PathfindingService.PathResult pathResult = pathfindingService.calculateShortestPath(
            unitLocation, crime.location);
        
        if (pathResult.isValidPath()) {
            // Draw path on map
            Color pathColor = getRandomColor();
            String pathLabel = String.format("Unit %d -> Crime %d", unitId, crime.crimeId);
            pathPanel.drawPath(pathResult.getPath(), pathColor, pathLabel);
            
            // Create assignment
            PoliceAssignmentPanel.AssignmentInfo assignment = 
                new PoliceAssignmentPanel.AssignmentInfo(
                    unitId, crime.crimeId, crime.crimeType, crime.location,
                    crime.priority, "Dispatched", pathResult.getDistance() * 0.5, // Estimate time
                    pathResult.getDistance()
                );
            
            // Add to assignment panel
            Map<Integer, PoliceAssignmentPanel.AssignmentInfo> assignments = new HashMap<>();
            assignments.put(unitId, assignment);
            assignmentPanel.addAssignment(assignment);
            
            logMessage(String.format("Unit %d dispatched to Crime %d (Distance: %.2f km)", 
                                    unitId, crime.crimeId, pathResult.getDistance()));
        }
    }
    
    private void simulateResponseCompletion() {
        // This would update existing assignments to "Resolved" status
        // For demo purposes, we'll just log it
        if (random.nextBoolean()) {
            logMessage("Response completed successfully");
        }
    }
    
    private void clearPaths() {
        pathPanel.clearPaths();
        logMessage("All paths cleared");
    }
    
    private Color getRandomColor() {
        Color[] colors = {
            Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, 
            Color.MAGENTA, Color.CYAN, new Color(128, 0, 128), // Purple
            new Color(255, 165, 0), // Orange
            new Color(0, 128, 0)    // Dark Green
        };
        return colors[random.nextInt(colors.length)];
    }
    
    private void logMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = java.time.LocalTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
            logArea.append(String.format("[%s] %s\n", timestamp, message));
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    private void updateStatus(String status) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(status));
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Use default look and feel
            new PoliceSimulatorGUI().setVisible(true);
        });
    }
}
