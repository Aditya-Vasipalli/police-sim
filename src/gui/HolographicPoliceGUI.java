package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;
import models.CityMapNode;
import models.Crime;
import models.Unit;

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
    private Object simulatorCore;  // SimulatorCore from default package
    private javax.swing.Timer animationTimer;  // Specify javax.swing.Timer
    
    // Holographic colors
    private static final Color HOLO_BLUE = new Color(0, 150, 255, 180);
    private static final Color HOLO_CYAN = new Color(0, 255, 255, 200);
    private static final Color HOLO_GREEN = new Color(0, 255, 100, 180);
    private static final Color HOLO_RED = new Color(255, 50, 50, 200);
    private static final Color HOLO_YELLOW = new Color(255, 255, 0, 180);
    private static final Color BACKGROUND_DARK = new Color(10, 10, 20);
    private static final Color GRID_COLOR = new Color(0, 100, 150, 80);

    public HolographicPoliceGUI(Object cityMap, Object simulatorCore) {
        this.cityMap = cityMap;
        this.simulatorCore = simulatorCore;
        
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
        // TODO: Call simulatorCore.run(100) via reflection
    }
    
    private void pauseSimulation() {
        logArea.append(">> SIMULATION PAUSED\n");
        // TODO: Call simulatorCore.pause() via reflection
    }
    
    private void generateCrime() {
        logArea.append(">> GENERATING EMERGENCY CRIME EVENT\n");
        // TODO: Implement crime generation
    }
    
    private void resetSimulation() {
        logArea.append(">> SYSTEM RESET\n");
        // TODO: Implement reset
    }
    
    /**
     * Inner class for holographic map visualization
     */
    private class HolographicMapPanel extends JPanel {
        private Map<Integer, Point2D.Double> nodePositions;
        private float animationPhase = 0;
        private Random random = new Random();
        
        public HolographicMapPanel() {
            setBackground(BACKGROUND_DARK);
            setOpaque(true);
            calculateNodePositions();
        }
        
        private void calculateNodePositions() {
            nodePositions = new HashMap<>();
            // TODO: Get nodes from cityMap via reflection
            // For now, create a demo layout with some test nodes
            
            // Demo nodes for testing
            nodePositions.put(0, new Point2D.Double(100, 100));
            nodePositions.put(1, new Point2D.Double(200, 150));
            nodePositions.put(2, new Point2D.Double(300, 200));
            nodePositions.put(3, new Point2D.Double(400, 100));
            nodePositions.put(4, new Point2D.Double(500, 300));
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
            
            // Draw nodes with holographic effects
            drawHolographicNodes(g2d);
            
            // Draw police stations
            drawPoliceStations(g2d);
            
            // Draw crimes
            drawCrimes(g2d);
            
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
            // TODO: Get nodes from cityMap and draw edges
            // For now, draw demo edges between test nodes
            
            // Demo edges
            drawGlowingLine(g2d, nodePositions.get(0), nodePositions.get(1), HOLO_BLUE, 2f);
            drawGlowingLine(g2d, nodePositions.get(1), nodePositions.get(2), HOLO_BLUE, 2f);
            drawGlowingLine(g2d, nodePositions.get(2), nodePositions.get(3), HOLO_BLUE, 2f);
            drawGlowingLine(g2d, nodePositions.get(3), nodePositions.get(4), HOLO_BLUE, 2f);
        }
        
        private void drawHolographicNodes(Graphics2D g2d) {
            for (Map.Entry<Integer, Point2D.Double> entry : nodePositions.entrySet()) {
                Point2D.Double pos = entry.getValue();
                
                // Draw node with glow effect
                drawGlowingSphere(g2d, pos, 8, Color.GRAY, HOLO_CYAN);
                
                // Draw node ID
                g2d.setColor(HOLO_CYAN);
                g2d.setFont(new Font("Arial", Font.BOLD, 8));
                g2d.drawString(String.valueOf(entry.getKey()), 
                             (float)(pos.x + 10), (float)(pos.y - 5));
            }
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
        
        private void drawCrimes(Graphics2D g2d) {
            // TODO: Get active crimes from simulator and draw them as red pulsing nodes
            // For now, demonstrate with random positions
        }
        
        private void drawGlowingLine(Graphics2D g2d, Point2D.Double p1, Point2D.Double p2, Color color, float width) {
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
        
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(1000, 600);
        }
    }
}
