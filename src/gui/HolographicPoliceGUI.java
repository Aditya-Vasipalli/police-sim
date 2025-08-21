package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;
import java.lang.reflect.Method;

// Add missing imports for core classes
// These classes are in the default package (src root)
// Import not needed for CityMap, SimulatorCore as they're in default package

/**
 * Futuristic Holographic Police Simulator GUI
 * Features anime-style holographic visualization with glowing effects
 */
public class HolographicPoliceGUI extends JFrame {
    private HolographicMapPanel mapPanel;
    private JPanel controlPanel;
    private JTextArea logArea;
    private Object cityMap;  // CityMap from default package
    private javax.swing.Timer animationTimer;  // Specify javax.swing.Timer
    
    // Animation and simulation state
    private List<AnimatedCrime> activeCrimes = new ArrayList<>();
    private List<AnimatedUnit> animatedUnits = new ArrayList<>();
    private Map<Integer, List<Integer>> activeAStarPaths = new HashMap<>();
    
    // Holographic colors
    private static final Color HOLO_BLUE = new Color(0, 150, 255, 180);
    private static final Color HOLO_CYAN = new Color(0, 255, 255, 200);
    private static final Color HOLO_GREEN = new Color(0, 255, 100, 180);
    private static final Color HOLO_RED = new Color(255, 50, 50, 200);
    private static final Color HOLO_YELLOW = new Color(255, 255, 0, 180);
    private static final Color BACKGROUND_DARK = new Color(10, 10, 20);
    private static final Color GRID_COLOR = new Color(0, 100, 150, 80);
    private static final Color CRIME_RED = new Color(255, 0, 0, 220);
    private static final Color STATION_BLUE = new Color(0, 100, 255, 220);
    private static final Color NODE_GREY = new Color(128, 128, 128, 150);

    // Inner classes for animation
    private static class AnimatedCrime {
        int nodeId;
        String crimeType;
        String severity;
        
        AnimatedCrime(int nodeId, String crimeType, String severity) {
            this.nodeId = nodeId;
            this.crimeType = crimeType;
            this.severity = severity;
        }
    }
    
    private static class AnimatedUnit {
        int unitId;
        List<Integer> currentPath;
        int currentPathIndex;
        int targetCrimeNodeId;
        float pathProgress;
        
        AnimatedUnit(int unitId, String unitType, List<Integer> path, int targetCrimeNodeId) {
            this.unitId = unitId;
            this.currentPath = new ArrayList<>(path);
            this.currentPathIndex = 0;
            this.targetCrimeNodeId = targetCrimeNodeId;
            this.pathProgress = 0;
        }
    }

    public HolographicPoliceGUI(Object cityMap) {
        this.cityMap = cityMap;
        
        initializeHolographicGUI();
        setupAnimationTimer();
    }
    
    private void initializeHolographicGUI() {
        setTitle("Police Response Simulator - Holographic Interface");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_DARK);
        
        // Create holographic map panel
        mapPanel = new HolographicMapPanel();
        add(mapPanel, BorderLayout.CENTER);
        
        // Create futuristic control panel
        createHolographicControlPanel();
        add(controlPanel, BorderLayout.NORTH);
        
        // Create holographic log area
        createHolographicLogArea();
        add(new JScrollPane(logArea), BorderLayout.SOUTH);
        
        // Set window properties
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Make window look futuristic
        setUndecorated(false);
        getRootPane().setBorder(BorderFactory.createLineBorder(HOLO_CYAN, 2));
    }
    
    private void createHolographicControlPanel() {
        controlPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Holographic background
                GradientPaint gradient = new GradientPaint(0, 0, new Color(20, 20, 40, 200), 
                                                         0, getHeight(), new Color(10, 10, 20, 150));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Glowing border
                g2d.setColor(HOLO_CYAN);
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRect(1, 1, getWidth()-2, getHeight()-2);
                
                g2d.dispose();
            }
        };
        controlPanel.setLayout(new FlowLayout());
        controlPanel.setPreferredSize(new Dimension(0, 60));
        controlPanel.setOpaque(false);
        
        // Create holographic buttons
        JButton startBtn = createHolographicButton("START SIMULATION", HOLO_GREEN);
        JButton pauseBtn = createHolographicButton("PAUSE", HOLO_YELLOW);
        JButton crimeBtn = createHolographicButton("GENERATE CRIME", HOLO_RED);
        JButton resetBtn = createHolographicButton("RESET", HOLO_BLUE);
        
        // Add button actions
        startBtn.addActionListener(e -> startSimulation());
        pauseBtn.addActionListener(e -> pauseSimulation());
        crimeBtn.addActionListener(e -> generateCrime());
        resetBtn.addActionListener(e -> resetSimulation());
        
        controlPanel.add(startBtn);
        controlPanel.add(pauseBtn);
        controlPanel.add(crimeBtn);
        controlPanel.add(resetBtn);
    }
    
    private JButton createHolographicButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Holographic button background
                if (getModel().isPressed()) {
                    g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 255));
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 220));
                } else {
                    g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 150));
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Glowing border
                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                
                // Text
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 10));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2;
                g2d.drawString(getText(), x, y);
                
                g2d.dispose();
            }
        };
        
        button.setPreferredSize(new Dimension(120, 35));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        
        return button;
    }
    
    private void createHolographicLogArea() {
        logArea = new JTextArea(6, 60) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Holographic background
                g2d.setColor(new Color(5, 5, 15, 200));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                super.paintComponent(g);
                g2d.dispose();
            }
        };
        
        logArea.setEditable(false);
        logArea.setBackground(new Color(5, 5, 15, 200));
        logArea.setForeground(HOLO_CYAN);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        logArea.setBorder(BorderFactory.createLineBorder(HOLO_CYAN, 1));
        
        logArea.append("=== HOLOGRAPHIC POLICE INTERFACE ONLINE ===\n");
        logArea.append("City map loaded successfully\n");
        logArea.append("All systems operational. Ready for deployment.\n");
    }
    
    private void setupAnimationTimer() {
        animationTimer = new javax.swing.Timer(50, e -> {
            mapPanel.updateAnimation();
            mapPanel.repaint();
        });
        animationTimer.start();
    }
    
    // Button action methods
    private void startSimulation() {
        logArea.append(">> SIMULATION STARTED\n");
        
        // Generate a test crime to demonstrate
        generateTestCrime();
        
        // TODO: Call simulatorCore.run(100) via reflection
        scrollLogToBottom();
    }
    
    private void pauseSimulation() {
        logArea.append(">> SIMULATION PAUSED\n");
        // TODO: Call simulatorCore.pause() via reflection
        scrollLogToBottom();
    }
    
    private void generateCrime() {
        logArea.append(">> GENERATING EMERGENCY CRIME EVENT\n");
        generateTestCrime();
        scrollLogToBottom();
    }
    
    private void resetSimulation() {
        logArea.append(">> SYSTEM RESET\n");
        activeCrimes.clear();
        animatedUnits.clear();
        activeAStarPaths.clear();
        scrollLogToBottom();
    }
    
    private void generateTestCrime() {
        // Generate a random crime at a random node for demonstration
        Random rand = new Random();
        int randomNode = rand.nextInt(133); // Based on city map size
        String[] crimeTypes = {"ROBBERY", "ASSAULT", "BURGLARY", "VANDALISM", "FRAUD"};
        String[] severities = {"LOW", "MEDIUM", "HIGH", "CRITICAL"};
        
        String crimeType = crimeTypes[rand.nextInt(crimeTypes.length)];
        String severity = severities[rand.nextInt(severities.length)];
        
        AnimatedCrime crime = new AnimatedCrime(randomNode, crimeType, severity);
        activeCrimes.add(crime);
        
        logArea.append("CRIME GENERATED: " + crimeType + " (" + severity + ") at node " + randomNode + "\n");
        
        // Simulate unit dispatch with A* pathfinding
        simulateUnitDispatch(crime);
    }
    
    private void simulateUnitDispatch(AnimatedCrime crime) {
        // Find nearest police station (demo: use stations 0, 3, or random)
        Random rand = new Random();
        int[] stations = {0, 3, 45, 99}; // Mix of demo and real stations
        int stationNode = stations[rand.nextInt(stations.length)];
        
        // Create A* path from station to crime
        List<Integer> path = generateDemoPath(stationNode, crime.nodeId);
        
        // Create animated unit
        AnimatedUnit unit = new AnimatedUnit(rand.nextInt(31) + 1, "PATROL", path, crime.nodeId);
        animatedUnits.add(unit);
        
        // Store path for visualization
        activeAStarPaths.put(unit.unitId, path);
        
        logArea.append("UNIT " + unit.unitId + " DISPATCHED: " + stationNode + " -> " + crime.nodeId + "\n");
        logArea.append("A* PATH: " + path + "\n");
    }
    
    private List<Integer> generateDemoPath(int start, int end) {
        // Generate a demo path for visualization
        // In real implementation, this would use A* algorithm
        List<Integer> path = new ArrayList<>();
        path.add(start);
        
        // Add some intermediate nodes for demonstration
        Random rand = new Random();
        int steps = 3 + rand.nextInt(4); // 3-6 steps
        int current = start;
        
        for (int i = 0; i < steps; i++) {
            int next = Math.abs(current + rand.nextInt(20) - 10) % 133;
            path.add(next);
            current = next;
        }
        
        path.add(end);
        return path;
    }
    
    private void scrollLogToBottom() {
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
    
    /**
     * Inner class for holographic map visualization
     */
    private class HolographicMapPanel extends JPanel {
        private Map<Integer, Point2D.Double> nodePositions;
        private float animationPhase = 0;
        
        public HolographicMapPanel() {
            setBackground(BACKGROUND_DARK);
            setOpaque(true);
            
            // Defer node position calculation until panel is sized
            addComponentListener(new java.awt.event.ComponentAdapter() {
                @Override
                public void componentResized(java.awt.event.ComponentEvent e) {
                    if (nodePositions == null) {
                        calculateNodePositions();
                    }
                }
            });
            
            // Add mouse listener for interaction
            addMouseMotionListener(new java.awt.event.MouseMotionListener() {
                @Override
                public void mouseDragged(java.awt.event.MouseEvent e) {}
                
                @Override
                public void mouseMoved(java.awt.event.MouseEvent e) {
                    // Check if hovering over a crime node for tooltip
                    repaint(); // Simple repaint for now
                }
            });
            
            // Add click listener for node selection
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    // Find clicked node and show details
                    Point2D.Double clickPoint = new Point2D.Double(e.getX(), e.getY());
                    for (Map.Entry<Integer, Point2D.Double> entry : nodePositions.entrySet()) {
                        Point2D.Double nodePos = entry.getValue();
                        double distance = Math.sqrt(Math.pow(clickPoint.x - nodePos.x, 2) + 
                                                   Math.pow(clickPoint.y - nodePos.y, 2));
                        if (distance < 15) { // Within 15 pixels
                            nodeClicked(entry.getKey());
                            break;
                        }
                    }
                }
            });
        }
        
        private void nodeClicked(Integer nodeId) {
            if (isPoliceStation(nodeId)) {
                logArea.append("POLICE STATION SELECTED: Node " + nodeId + "\n");
                // TODO: Show units at this station
            } else if (isCrimeLocation(nodeId)) {
                AnimatedCrime crime = findCrimeAtNode(nodeId);
                if (crime != null) {
                    logArea.append("CRIME SELECTED: " + crime.crimeType + " (" + crime.severity + ") at Node " + nodeId + "\n");
                }
            } else {
                logArea.append("NODE SELECTED: " + nodeId + "\n");
            }
            scrollLogToBottom();
        }
        
        private AnimatedCrime findCrimeAtNode(int nodeId) {
            for (AnimatedCrime crime : activeCrimes) {
                if (crime.nodeId == nodeId) return crime;
            }
            return null;
        }
        
        private void calculateNodePositions() {
            nodePositions = new HashMap<>();
            
            try {
                // Use reflection to get nodes from cityMap
                Method getAllNodesMethod = cityMap.getClass().getMethod("getAllNodes");
                Object nodesMap = getAllNodesMethod.invoke(cityMap);
                
                if (nodesMap instanceof Map) {
                    Map<?, ?> nodes = (Map<?, ?>) nodesMap;
                    
                    // Find bounds of the actual coordinates
                    double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
                    double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;
                    boolean hasValidCoords = false;
                    
                    for (Object nodeObj : nodes.values()) {
                        try {
                            Method getXMethod = nodeObj.getClass().getMethod("getX");
                            Method getYMethod = nodeObj.getClass().getMethod("getY");
                            double x = (Double) getXMethod.invoke(nodeObj);
                            double y = (Double) getYMethod.invoke(nodeObj);
                            
                            minX = Math.min(minX, x);
                            maxX = Math.max(maxX, x);
                            minY = Math.min(minY, y);
                            maxY = Math.max(maxY, y);
                            hasValidCoords = true;
                        } catch (Exception e) {
                            // Skip this node if reflection fails
                        }
                    }
                    
                    if (hasValidCoords && maxX > minX && maxY > minY) {
                        // Scale to fit panel with margin
                        double margin = 50;
                        int panelWidth = Math.max(getWidth(), 1000);  // Fallback minimum size
                        int panelHeight = Math.max(getHeight(), 600);
                        double availableWidth = panelWidth - 2 * margin;
                        double availableHeight = panelHeight - 2 * margin;
                        
                        // Ensure minimum size
                        if (availableWidth < 200) availableWidth = 800;
                        if (availableHeight < 200) availableHeight = 500;
                        
                        double scaleX = availableWidth / (maxX - minX);
                        double scaleY = availableHeight / (maxY - minY);
                        double scale = Math.min(scaleX, scaleY);
                        
                        logArea.append("Scaling city map: " + nodes.size() + " nodes, scale=" + String.format("%.2f", scale) + "\n");
                        
                        for (Map.Entry<?, ?> entry : nodes.entrySet()) {
                            try {
                                Integer nodeId = (Integer) entry.getKey();
                                Object nodeObj = entry.getValue();
                                
                                Method getXMethod = nodeObj.getClass().getMethod("getX");
                                Method getYMethod = nodeObj.getClass().getMethod("getY");
                                double x = (Double) getXMethod.invoke(nodeObj);
                                double y = (Double) getYMethod.invoke(nodeObj);
                                
                                double scaledX = margin + (x - minX) * scale;
                                double scaledY = margin + (y - minY) * scale;
                                nodePositions.put(nodeId, new Point2D.Double(scaledX, scaledY));
                            } catch (Exception e) {
                                // Skip this node if reflection fails
                            }
                        }
                        scrollLogToBottom();
                    } else {
                        throw new Exception("Invalid coordinate bounds");
                    }
                }
            } catch (Exception e) {
                // Fallback to demo layout if reflection fails
                logArea.append("Using demo node layout (reflection failed: " + e.getMessage() + ")\n");
                createDemoNodeLayout();
                scrollLogToBottom();
            }
            
            if (nodePositions.isEmpty()) {
                createDemoNodeLayout();
            }
        }
        
        private void createDemoNodeLayout() {
            // Create a more extensive demo layout for testing that fills the screen
            Random rand = new Random(42); // Fixed seed for consistent layout
            
            // Use panel dimensions or fallback values
            int panelWidth = Math.max(getWidth(), 1000);
            int panelHeight = Math.max(getHeight(), 600);
            
            int margin = 50;
            int usableWidth = panelWidth - 2 * margin;
            int usableHeight = panelHeight - 2 * margin;
            
            logArea.append("Creating demo layout: " + usableWidth + "x" + usableHeight + " area\n");
            
            // Create nodes in a more natural distribution
            for (int i = 0; i < 133; i++) { // Match city map size
                // Create a more organic layout instead of strict grid
                double angle = (i * 2.4) % (2 * Math.PI); // Spiral pattern
                double radius = 50 + (i * 3) % Math.min(usableWidth, usableHeight) / 3;
                
                double baseX = margin + usableWidth / 2 + Math.cos(angle) * radius;
                double baseY = margin + usableHeight / 2 + Math.sin(angle) * radius;
                
                // Add some randomness for natural look
                double x = baseX + (rand.nextDouble() - 0.5) * 60;
                double y = baseY + (rand.nextDouble() - 0.5) * 60;
                
                // Ensure nodes stay within bounds
                x = Math.max(margin, Math.min(panelWidth - margin, x));
                y = Math.max(margin, Math.min(panelHeight - margin, y));
                
                nodePositions.put(i, new Point2D.Double(x, y));
            }
            
            scrollLogToBottom();
        }
        
        public void updateAnimation() {
            animationPhase += 0.1f;
            if (animationPhase > 2 * Math.PI) {
                animationPhase = 0;
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Recalculate positions if panel size changed
            if (nodePositions == null || nodePositions.isEmpty()) {
                calculateNodePositions();
            }
            
            // Draw holographic grid
            drawHolographicGrid(g2d);
            
            // Draw edges with glow effect
            drawHolographicEdges(g2d);
            
            // Draw A* pathfinding routes
            drawAStarPaths(g2d);
            
            // Draw nodes with color coding (grey=normal, blue=stations, red=crimes)
            drawHolographicNodes(g2d);
            
            // Draw police stations with pulsing effects
            drawPoliceStations(g2d);
            
            // Draw animated units moving along paths
            drawAnimatedUnits(g2d);
            
            // Draw crime details tooltip if hovering
            drawCrimeTooltips(g2d);
            
            g2d.dispose();
        }
        
        private void drawHolographicGrid(Graphics2D g2d) {
            g2d.setColor(GRID_COLOR);
            g2d.setStroke(new BasicStroke(1f));
            
            int gridSize = 30;
            for (int x = 0; x < getWidth(); x += gridSize) {
                g2d.drawLine(x, 0, x, getHeight());
            }
            for (int y = 0; y < getHeight(); y += gridSize) {
                g2d.drawLine(0, y, getWidth(), y);
            }
        }
        
        private void drawHolographicEdges(Graphics2D g2d) {
            try {
                // Use reflection to get nodes and edges from cityMap
                Method getAllNodesMethod = cityMap.getClass().getMethod("getAllNodes");
                Object nodesMap = getAllNodesMethod.invoke(cityMap);
                
                if (nodesMap instanceof Map) {
                    Map<?, ?> nodes = (Map<?, ?>) nodesMap;
                    
                    for (Object nodeObj : nodes.values()) {
                        try {
                            Method getNodeIdMethod = nodeObj.getClass().getMethod("getNodeId");
                            Integer nodeId = (Integer) getNodeIdMethod.invoke(nodeObj);
                            Point2D.Double p1 = nodePositions.get(nodeId);
                            if (p1 == null) continue;
                            
                            Method getAdjacentEdgesMethod = nodeObj.getClass().getMethod("getAdjacentEdges");
                            Object edges = getAdjacentEdgesMethod.invoke(nodeObj);
                            
                            if (edges instanceof List) {
                                List<?> edgeList = (List<?>) edges;
                                for (Object edge : edgeList) {
                                    Method getDestinationMethod = edge.getClass().getMethod("getDestinationNode");
                                    Integer destNodeId = (Integer) getDestinationMethod.invoke(edge);
                                    Point2D.Double p2 = nodePositions.get(destNodeId);
                                    
                                    if (p2 != null) {
                                        drawGlowingLine(g2d, p1, p2, HOLO_BLUE, 1.5f);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            // Skip this node if reflection fails
                        }
                    }
                }
            } catch (Exception e) {
                // Fallback to demo edges if reflection fails
                drawDemoEdges(g2d);
            }
        }
        
        private void drawDemoEdges(Graphics2D g2d) {
            // Draw demo edges in a grid pattern
            for (Map.Entry<Integer, Point2D.Double> entry1 : nodePositions.entrySet()) {
                Integer nodeId1 = entry1.getKey();
                Point2D.Double p1 = entry1.getValue();
                
                // Connect to nearby nodes (simple grid-like connections)
                for (Map.Entry<Integer, Point2D.Double> entry2 : nodePositions.entrySet()) {
                    Integer nodeId2 = entry2.getKey();
                    Point2D.Double p2 = entry2.getValue();
                    
                    if (nodeId1.equals(nodeId2)) continue;
                    
                    double distance = Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
                    
                    // Only connect nearby nodes (within ~80 pixels)
                    if (distance < 80 && Math.random() > 0.7) { // Sparse connections
                        drawGlowingLine(g2d, p1, p2, HOLO_BLUE, 1f);
                    }
                }
            }
        }
        
        private void drawCrimeTooltips(Graphics2D g2d) {
            // TODO: Add mouse hover detection for crime tooltips
            // For now, just draw crime labels
            for (AnimatedCrime crime : activeCrimes) {
                Point2D.Double pos = nodePositions.get(crime.nodeId);
                if (pos == null) continue;
                
                // Draw crime info tooltip
                g2d.setColor(HOLO_RED);
                g2d.setFont(new Font("Arial", Font.BOLD, 9));
                String crimeInfo = crime.crimeType + " (" + crime.severity + ")";
                g2d.drawString(crimeInfo, (float)(pos.x + 15), (float)(pos.y - 15));
            }
        }
        
        private void drawHolographicNodes(Graphics2D g2d) {
            for (Map.Entry<Integer, Point2D.Double> entry : nodePositions.entrySet()) {
                Integer nodeId = entry.getKey();
                Point2D.Double pos = entry.getValue();
                
                // Determine node color based on type
                Color nodeColor = NODE_GREY;
                Color glowColor = HOLO_CYAN;
                int radius = 6;
                
                // Check if this is a police station
                if (isPoliceStation(nodeId)) {
                    nodeColor = STATION_BLUE;
                    glowColor = HOLO_BLUE;
                    radius = 10;
                }
                
                // Check if this is a crime location
                if (isCrimeLocation(nodeId)) {
                    nodeColor = CRIME_RED;
                    glowColor = HOLO_RED;
                    radius = 12;
                    
                    // Add pulsing effect for crimes
                    float pulse = (float)(0.8 + 0.4 * Math.sin(animationPhase * 3));
                    radius = (int)(radius * pulse);
                }
                
                // Draw node with glow effect
                drawGlowingSphere(g2d, pos, radius, nodeColor, glowColor);
                
                // Draw node ID for debugging (smaller font)
                g2d.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 150));
                g2d.setFont(new Font("Arial", Font.BOLD, 7));
                g2d.drawString(String.valueOf(nodeId), 
                             (float)(pos.x + radius + 2), (float)(pos.y - 2));
            }
        }
        
        private boolean isPoliceStation(int nodeId) {
            // Check demo stations and try to get real stations
            int[] demoStations = {0, 3, 45, 99, 113, 125, 135, 145};
            for (int station : demoStations) {
                if (station == nodeId) return true;
            }
            return false;
        }
        
        private boolean isCrimeLocation(int nodeId) {
            for (AnimatedCrime crime : activeCrimes) {
                if (crime.nodeId == nodeId) return true;
            }
            return false;
        }
        
        private void drawAStarPaths(Graphics2D g2d) {
            // Draw all active A* paths
            for (Map.Entry<Integer, List<Integer>> entry : activeAStarPaths.entrySet()) {
                List<Integer> path = entry.getValue();
                if (path.size() < 2) continue;
                
                // Draw path with animated glow
                for (int i = 0; i < path.size() - 1; i++) {
                    Point2D.Double p1 = nodePositions.get(path.get(i));
                    Point2D.Double p2 = nodePositions.get(path.get(i + 1));
                    
                    if (p1 != null && p2 != null) {
                        // Animated path color
                        float alpha = (float)(0.6 + 0.4 * Math.sin(animationPhase * 2 + i * 0.3));
                        Color pathColor = new Color(HOLO_YELLOW.getRed(), HOLO_YELLOW.getGreen(), 
                                                  HOLO_YELLOW.getBlue(), (int)(255 * alpha));
                        drawGlowingLine(g2d, p1, p2, pathColor, 3f);
                        
                        // Draw directional arrows
                        drawArrow(g2d, p1, p2, pathColor);
                    }
                }
            }
        }
        
        private void drawAnimatedUnits(Graphics2D g2d) {
            for (AnimatedUnit unit : animatedUnits) {
                if (unit.currentPath.isEmpty()) continue;
                
                // Calculate current position along path
                Point2D.Double currentPos = calculateUnitPosition(unit);
                if (currentPos == null) continue;
                
                // Draw unit as moving holographic marker
                float pulse = (float)(0.7 + 0.3 * Math.sin(animationPhase * 4));
                Color unitColor = new Color(HOLO_GREEN.getRed(), HOLO_GREEN.getGreen(), 
                                          HOLO_GREEN.getBlue(), (int)(200 * pulse));
                
                drawGlowingSphere(g2d, currentPos, 8, unitColor, HOLO_GREEN);
                
                // Draw unit ID
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 8));
                g2d.drawString("U" + unit.unitId, (float)(currentPos.x - 8), (float)(currentPos.y - 12));
                
                // Update unit position for next frame
                updateUnitPosition(unit);
            }
        }
        
        private Point2D.Double calculateUnitPosition(AnimatedUnit unit) {
            if (unit.currentPathIndex >= unit.currentPath.size() - 1) {
                // Unit has reached destination
                return nodePositions.get(unit.currentPath.get(unit.currentPath.size() - 1));
            }
            
            Point2D.Double startPos = nodePositions.get(unit.currentPath.get(unit.currentPathIndex));
            Point2D.Double endPos = nodePositions.get(unit.currentPath.get(unit.currentPathIndex + 1));
            
            if (startPos == null || endPos == null) return startPos;
            
            // Interpolate between start and end positions
            double t = unit.pathProgress;
            double x = startPos.x + (endPos.x - startPos.x) * t;
            double y = startPos.y + (endPos.y - startPos.y) * t;
            
            return new Point2D.Double(x, y);
        }
        
        private void updateUnitPosition(AnimatedUnit unit) {
            unit.pathProgress += 0.02f; // Adjust speed as needed
            
            if (unit.pathProgress >= 1.0f) {
                unit.pathProgress = 0;
                unit.currentPathIndex++;
                
                // Check if unit reached destination
                if (unit.currentPathIndex >= unit.currentPath.size() - 1) {
                    // Unit has arrived at crime scene
                    removeCrimeAtNode(unit.targetCrimeNodeId);
                    logArea.append("UNIT " + unit.unitId + " ARRIVED at crime node " + unit.targetCrimeNodeId + "\n");
                    scrollLogToBottom();
                }
            }
        }
        
        private void removeCrimeAtNode(int nodeId) {
            activeCrimes.removeIf(crime -> crime.nodeId == nodeId);
        }
        
        private void drawArrow(Graphics2D g2d, Point2D.Double start, Point2D.Double end, Color color) {
            // Draw small directional arrow along the path
            double dx = end.x - start.x;
            double dy = end.y - start.y;
            double length = Math.sqrt(dx * dx + dy * dy);
            
            if (length < 10) return; // Too short for arrow
            
            // Normalize and create arrow points
            dx /= length;
            dy /= length;
            
            double arrowX = start.x + dx * length * 0.7; // 70% along the line
            double arrowY = start.y + dy * length * 0.7;
            
            // Arrow head points
            double arrowSize = 4;
            double[] arrowXPoints = {
                arrowX + dx * arrowSize,
                arrowX - dx * arrowSize + dy * arrowSize,
                arrowX - dx * arrowSize - dy * arrowSize
            };
            double[] arrowYPoints = {
                arrowY + dy * arrowSize,
                arrowY - dy * arrowSize - dx * arrowSize,
                arrowY - dy * arrowSize + dx * arrowSize
            };
            
            g2d.setColor(color);
            g2d.fillPolygon(
                new int[]{(int)arrowXPoints[0], (int)arrowXPoints[1], (int)arrowXPoints[2]},
                new int[]{(int)arrowYPoints[0], (int)arrowYPoints[1], (int)arrowYPoints[2]},
                3
            );
        }
        
        private void drawPoliceStations(Graphics2D g2d) {
            // TODO: Get police station locations from simulatorCore
            // For now, demo with nodes 0 and 3 as police stations
            List<Integer> stations = Arrays.asList(0, 3);
            
            for (Integer stationId : stations) {
                Point2D.Double pos = nodePositions.get(stationId);
                if (pos == null) continue;
                
                // Draw pulsing police station
                float pulse = (float)(0.8 + 0.4 * Math.sin(animationPhase * 2));
                drawGlowingSphere(g2d, pos, (int)(15 * pulse), HOLO_BLUE, Color.WHITE);
                
                // Draw station symbol
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 10));
                g2d.drawString("◎", (float)(pos.x - 5), (float)(pos.y + 3));
            }
        }
        
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(1000, 600);
        }
        
        private void drawGlowingLine(Graphics2D g2d, Point2D.Double p1, Point2D.Double p2, Color color, float width) {
            if (p1 == null || p2 == null) return;
            
            // Draw glow effect
            for (int i = 5; i > 0; i--) {
                g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30 * i));
                g2d.setStroke(new BasicStroke(width + i * 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
            }
            
            // Draw main line
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
        }
        
        private void drawGlowingSphere(Graphics2D g2d, Point2D.Double pos, int radius, Color fillColor, Color glowColor) {
            if (pos == null) return;
            
            // Draw glow effect
            for (int i = 10; i > 0; i--) {
                g2d.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 20 * i));
                g2d.fillOval((int)(pos.x - radius - i), (int)(pos.y - radius - i), 
                           2 * (radius + i), 2 * (radius + i));
            }
            
            // Draw main sphere
            g2d.setColor(fillColor);
            g2d.fillOval((int)(pos.x - radius), (int)(pos.y - radius), 2 * radius, 2 * radius);
            
            // Draw bright outline
            g2d.setColor(glowColor);
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawOval((int)(pos.x - radius), (int)(pos.y - radius), 2 * radius, 2 * radius);
        }
    }
}
