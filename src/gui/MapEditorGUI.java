package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import models.*;

/**
 * Map Editor GUI for creating and editing city maps
 * Allows users to add/remove nodes and edges visually
 */
public class MapEditorGUI extends JFrame {
    private MapCanvas mapCanvas;
    private JPanel toolPanel;
    private JLabel statusLabel;
    private JTextField nodeIdField;
    private JTextField edgeWeightField;
    private JButton saveButton, loadButton, clearButton;
    private JRadioButton addNodeMode, addEdgeMode, deleteMode;
    private ButtonGroup modeGroup;
    
    private Graph currentGraph;
    private String currentFileName;
    
    // Drawing properties
    private static final int NODE_RADIUS = 20;
    private static final Color NODE_COLOR = new Color(100, 150, 255);
    private static final Color EDGE_COLOR = new Color(50, 50, 50);
    private static final Color SELECTED_COLOR = new Color(255, 100, 100);
    
    public MapEditorGUI() {
        currentGraph = new Graph();
        initializeGUI();
    }
    
    private void initializeGUI() {
        setTitle("Police Simulation - Map Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create components
        createMenuBar();
        createToolPanel();
        createMapCanvas();
        createStatusBar();
        
        // Layout components
        add(toolPanel, BorderLayout.NORTH);
        add(new JScrollPane(mapCanvas), BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        
        // Set window properties
        setSize(800, 600);
        setLocationRelativeTo(null);
        updateStatus("Ready - Click 'Add Node' and click on the canvas to add nodes");
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        
        JMenuItem newItem = new JMenuItem("New");
        newItem.addActionListener(e -> newMap());
        fileMenu.add(newItem);
        
        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(e -> loadMap());
        fileMenu.add(openItem);
        
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(e -> saveMap());
        fileMenu.add(saveItem);
        
        JMenuItem saveAsItem = new JMenuItem("Save As");
        saveAsItem.addActionListener(e -> saveMapAs());
        fileMenu.add(saveAsItem);
        
        fileMenu.addSeparator();
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        // Edit menu
        JMenu editMenu = new JMenu("Edit");
        
        JMenuItem clearAllItem = new JMenuItem("Clear All");
        clearAllItem.addActionListener(e -> clearMap());
        editMenu.add(clearAllItem);
        
        // View menu
        JMenu viewMenu = new JMenu("View");
        
        JMenuItem zoomInItem = new JMenuItem("Zoom In");
        zoomInItem.addActionListener(e -> mapCanvas.zoomIn());
        viewMenu.add(zoomInItem);
        
        JMenuItem zoomOutItem = new JMenuItem("Zoom Out");
        zoomOutItem.addActionListener(e -> mapCanvas.zoomOut());
        viewMenu.add(zoomOutItem);
        
        JMenuItem resetZoomItem = new JMenuItem("Reset Zoom");
        resetZoomItem.addActionListener(e -> mapCanvas.resetZoom());
        viewMenu.add(resetZoomItem);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void createToolPanel() {
        toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolPanel.setBorder(BorderFactory.createEtchedBorder());
        
        // Mode selection
        JLabel modeLabel = new JLabel("Mode:");
        toolPanel.add(modeLabel);
        
        addNodeMode = new JRadioButton("Add Node", true);
        addEdgeMode = new JRadioButton("Add Edge");
        deleteMode = new JRadioButton("Delete");
        
        modeGroup = new ButtonGroup();
        modeGroup.add(addNodeMode);
        modeGroup.add(addEdgeMode);
        modeGroup.add(deleteMode);
        
        toolPanel.add(addNodeMode);
        toolPanel.add(addEdgeMode);
        toolPanel.add(deleteMode);
        
        toolPanel.add(new JSeparator(SwingConstants.VERTICAL));
        
        // Node ID input
        toolPanel.add(new JLabel("Node ID:"));
        nodeIdField = new JTextField("1", 5);
        toolPanel.add(nodeIdField);
        
        // Edge weight input
        toolPanel.add(new JLabel("Edge Weight:"));
        edgeWeightField = new JTextField("1.0", 5);
        toolPanel.add(edgeWeightField);
        
        toolPanel.add(new JSeparator(SwingConstants.VERTICAL));
        
        // Action buttons
        saveButton = new JButton("Save Map");
        saveButton.addActionListener(e -> saveMap());
        toolPanel.add(saveButton);
        
        loadButton = new JButton("Load Map");
        loadButton.addActionListener(e -> loadMap());
        toolPanel.add(loadButton);
        
        clearButton = new JButton("Clear All");
        clearButton.addActionListener(e -> clearMap());
        toolPanel.add(clearButton);
    }
    
    private void createMapCanvas() {
        mapCanvas = new MapCanvas();
        mapCanvas.setPreferredSize(new Dimension(600, 400));
        mapCanvas.setBackground(Color.WHITE);
    }
    
    private void createStatusBar() {
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
    }
    
    private void newMap() {
        currentGraph = new Graph();
        currentFileName = null;
        mapCanvas.clearNodes();
        updateStatus("New map created");
        mapCanvas.repaint();
    }
    
    private void saveMap() {
        if (currentFileName != null) {
            saveMapToFile(new File(currentFileName));
        } else {
            saveMapAs();
        }
    }
    
    private void saveMapAs() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".csv");
            }
            public String getDescription() {
                return "CSV Map Files (*.csv)";
            }
        });
        
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }
            saveMapToFile(file);
        }
    }
    
    private void saveMapToFile(File file) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // Write header
            writer.println("from,to,weight");
            
            // Write edges
            Map<Integer, List<Edge>> allEdges = currentGraph.getAllEdges();
            Set<String> writtenEdges = new HashSet<>();
            
            for (Map.Entry<Integer, List<Edge>> entry : allEdges.entrySet()) {
                int from = entry.getKey();
                for (Edge edge : entry.getValue()) {
                    int to = edge.getDestination();
                    double weight = edge.getWeight();
                    
                    // Avoid duplicate edges (since graph is undirected)
                    String edgeKey = Math.min(from, to) + "," + Math.max(from, to);
                    if (!writtenEdges.contains(edgeKey)) {
                        writer.println(from + "," + to + "," + weight);
                        writtenEdges.add(edgeKey);
                    }
                }
            }
            
            currentFileName = file.getAbsolutePath();
            updateStatus("Map saved to " + file.getName());
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage(), 
                                        "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadMap() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".csv");
            }
            public String getDescription() {
                return "CSV Map Files (*.csv)";
            }
        });
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            loadMapFromFile(chooser.getSelectedFile());
        }
    }
    
    private void loadMapFromFile(File file) {
        try {
            currentGraph = new Graph();
            mapCanvas.clearNodes();
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine(); // Skip header
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        int from = Integer.parseInt(parts[0].trim());
                        int to = Integer.parseInt(parts[1].trim());
                        double weight = Double.parseDouble(parts[2].trim());
                        
                        currentGraph.addEdge(from, to, weight);
                        mapCanvas.addNodeIfNotExists(from);
                        mapCanvas.addNodeIfNotExists(to);
                    }
                }
            }
            
            currentFileName = file.getAbsolutePath();
            updateStatus("Map loaded from " + file.getName());
            mapCanvas.repaint();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage(), 
                                        "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearMap() {
        int result = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to clear the entire map?", 
            "Clear Map", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            currentGraph = new Graph();
            mapCanvas.clearNodes();
            updateStatus("Map cleared");
            mapCanvas.repaint();
        }
    }
    
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }
    
    // Inner class for the map drawing canvas
    private class MapCanvas extends JPanel {
        private Map<Integer, Point> nodePositions;
        private Integer selectedNode;
        private Integer firstSelectedNode; // For edge creation
        private double zoomFactor = 1.0;
        private Point panOffset = new Point(0, 0);
        
        public MapCanvas() {
            nodePositions = new HashMap<>();
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    handleMouseClick(e);
                }
            });
            
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    // Update cursor based on mode and hover
                    setCursor(getCursorForMode());
                }
            });
        }
        
        private Cursor getCursorForMode() {
            if (addNodeMode.isSelected()) {
                return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
            } else if (deleteMode.isSelected()) {
                return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
            } else {
                return Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
            }
        }
        
        private void handleMouseClick(MouseEvent e) {
            Point clickPoint = e.getPoint();
            
            if (addNodeMode.isSelected()) {
                addNode(clickPoint);
            } else if (addEdgeMode.isSelected()) {
                handleEdgeMode(clickPoint);
            } else if (deleteMode.isSelected()) {
                deleteAtPoint(clickPoint);
            }
        }
        
        private void addNode(Point point) {
            try {
                int nodeId = Integer.parseInt(nodeIdField.getText());
                if (nodePositions.containsKey(nodeId)) {
                    updateStatus("Node " + nodeId + " already exists");
                    return;
                }
                
                nodePositions.put(nodeId, point);
                updateStatus("Added node " + nodeId);
                
                // Auto-increment node ID
                nodeIdField.setText(String.valueOf(nodeId + 1));
                repaint();
                
            } catch (NumberFormatException ex) {
                updateStatus("Invalid node ID");
            }
        }
        
        private void handleEdgeMode(Point point) {
            Integer clickedNode = findNodeAtPoint(point);
            
            if (clickedNode == null) {
                updateStatus("Click on a node to create edge");
                return;
            }
            
            if (firstSelectedNode == null) {
                firstSelectedNode = clickedNode;
                selectedNode = clickedNode;
                updateStatus("Selected node " + clickedNode + ". Click another node to create edge.");
                repaint();
            } else {
                if (clickedNode.equals(firstSelectedNode)) {
                    // Clicked same node, deselect
                    firstSelectedNode = null;
                    selectedNode = null;
                    updateStatus("Deselected node");
                } else {
                    // Create edge
                    createEdge(firstSelectedNode, clickedNode);
                    firstSelectedNode = null;
                    selectedNode = null;
                }
                repaint();
            }
        }
        
        private void createEdge(int from, int to) {
            try {
                double weight = Double.parseDouble(edgeWeightField.getText());
                currentGraph.addEdge(from, to, weight);
                updateStatus("Created edge from " + from + " to " + to + " (weight: " + weight + ")");
                repaint();
            } catch (NumberFormatException ex) {
                updateStatus("Invalid edge weight");
            }
        }
        
        private void deleteAtPoint(Point point) {
            Integer nodeToDelete = findNodeAtPoint(point);
            if (nodeToDelete != null) {
                nodePositions.remove(nodeToDelete);
                // Note: We can't easily remove from Graph, so just remove visually
                updateStatus("Deleted node " + nodeToDelete + " (visual only)");
                repaint();
            }
        }
        
        private Integer findNodeAtPoint(Point point) {
            for (Map.Entry<Integer, Point> entry : nodePositions.entrySet()) {
                Point nodePos = entry.getValue();
                double distance = point.distance(nodePos);
                if (distance <= NODE_RADIUS) {
                    return entry.getKey();
                }
            }
            return null;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            
            // Enable antialiasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Apply zoom and pan
            g2d.scale(zoomFactor, zoomFactor);
            g2d.translate(panOffset.x, panOffset.y);
            
            // Draw edges first
            drawEdges(g2d);
            
            // Draw nodes
            drawNodes(g2d);
            
            g2d.dispose();
        }
        
        private void drawEdges(Graphics2D g2d) {
            g2d.setColor(EDGE_COLOR);
            g2d.setStroke(new BasicStroke(2.0f));
            
            Map<Integer, List<Edge>> allEdges = currentGraph.getAllEdges();
            Set<String> drawnEdges = new HashSet<>();
            
            for (Map.Entry<Integer, List<Edge>> entry : allEdges.entrySet()) {
                int from = entry.getKey();
                Point fromPos = nodePositions.get(from);
                if (fromPos == null) continue;
                
                for (Edge edge : entry.getValue()) {
                    int to = edge.getDestination();
                    Point toPos = nodePositions.get(to);
                    if (toPos == null) continue;
                    
                    // Avoid drawing duplicate edges
                    String edgeKey = Math.min(from, to) + "," + Math.max(from, to);
                    if (drawnEdges.contains(edgeKey)) continue;
                    drawnEdges.add(edgeKey);
                    
                    // Draw edge
                    g2d.drawLine(fromPos.x, fromPos.y, toPos.x, toPos.y);
                    
                    // Draw weight label
                    int midX = (fromPos.x + toPos.x) / 2;
                    int midY = (fromPos.y + toPos.y) / 2;
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(String.format("%.1f", edge.getWeight()), midX, midY);
                    g2d.setColor(EDGE_COLOR);
                }
            }
        }
        
        private void drawNodes(Graphics2D g2d) {
            for (Map.Entry<Integer, Point> entry : nodePositions.entrySet()) {
                int nodeId = entry.getKey();
                Point pos = entry.getValue();
                
                // Choose color
                if (Integer.valueOf(nodeId).equals(selectedNode)) {
                    g2d.setColor(SELECTED_COLOR);
                } else {
                    g2d.setColor(NODE_COLOR);
                }
                
                // Draw node circle
                g2d.fillOval(pos.x - NODE_RADIUS, pos.y - NODE_RADIUS, 
                           2 * NODE_RADIUS, 2 * NODE_RADIUS);
                
                // Draw node border
                g2d.setColor(Color.BLACK);
                g2d.drawOval(pos.x - NODE_RADIUS, pos.y - NODE_RADIUS, 
                           2 * NODE_RADIUS, 2 * NODE_RADIUS);
                
                // Draw node ID
                FontMetrics fm = g2d.getFontMetrics();
                String idStr = String.valueOf(nodeId);
                int textWidth = fm.stringWidth(idStr);
                int textHeight = fm.getHeight();
                g2d.drawString(idStr, pos.x - textWidth / 2, pos.y + textHeight / 4);
            }
        }
        
        public void addNodeIfNotExists(int nodeId) {
            if (!nodePositions.containsKey(nodeId)) {
                // Generate a random position
                Random rand = new Random();
                int x = 50 + rand.nextInt(Math.max(1, getWidth() - 100));
                int y = 50 + rand.nextInt(Math.max(1, getHeight() - 100));
                nodePositions.put(nodeId, new Point(x, y));
            }
        }
        
        public void clearNodes() {
            nodePositions.clear();
            selectedNode = null;
            firstSelectedNode = null;
        }
        
        public void zoomIn() {
            zoomFactor *= 1.2;
            repaint();
        }
        
        public void zoomOut() {
            zoomFactor /= 1.2;
            repaint();
        }
        
        public void resetZoom() {
            zoomFactor = 1.0;
            panOffset = new Point(0, 0);
            repaint();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MapEditorGUI().setVisible(true);
        });
    }
}
