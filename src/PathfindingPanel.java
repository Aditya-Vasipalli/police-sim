// PathfindingPanel.java
// GUI: Visualizes police unit paths on the city map

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;
import models.CityMapNode;

public class PathfindingPanel extends JPanel {
    private Map<Integer, CityMapNode> cityNodes;
    private List<PathVisualization> activePaths;
    private PathVisualization selectedPath;
    private double mapWidth = 400.0;
    private double mapHeight = 400.0;
    private double margin = 50.0;
    
    public PathfindingPanel() {
        initializePanel();
    }
    
    private void initializePanel() {
        setPreferredSize(new Dimension(500, 500));
        setBorder(BorderFactory.createTitledBorder("Path Visualization"));
        setBackground(Color.WHITE);
        
        cityNodes = new HashMap<>();
        activePaths = new ArrayList<>();
        
        // Add mouse listener for node selection
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });
    }
    
    public void setCityMap(Map<Integer, CityMapNode> nodes) {
        this.cityNodes = new HashMap<>(nodes);
        calculateMapBounds();
        repaint();
    }
    
    public void drawPath(List<Integer> path, Color pathColor, String label) {
        if (path.isEmpty()) return;
        
        PathVisualization pathViz = new PathVisualization(path, pathColor, label);
        activePaths.add(pathViz);
        repaint();
    }
    
    public void clearPaths() {
        activePaths.clear();
        selectedPath = null;
        repaint();
    }
    
    public void highlightPath(String label) {
        selectedPath = null;
        for (PathVisualization path : activePaths) {
            if (path.label.equals(label)) {
                selectedPath = path;
                break;
            }
        }
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Enable antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (cityNodes.isEmpty()) {
            drawNoMapMessage(g2d);
            return;
        }
        
        // Draw grid background
        drawGrid(g2d);
        
        // Draw edges first (so they appear behind nodes)
        drawEdges(g2d);
        
        // Draw nodes
        drawNodes(g2d);
        
        // Draw paths
        drawPaths(g2d);
        
        // Draw legend
        drawLegend(g2d);
        
        g2d.dispose();
    }
    
    private void drawNoMapMessage(Graphics2D g2d) {
        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        String message = "No city map loaded";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(message)) / 2;
        int y = getHeight() / 2;
        g2d.drawString(message, x, y);
    }
    
    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(new Color(240, 240, 240));
        g2d.setStroke(new BasicStroke(0.5f));
        
        int gridSpacing = 20;
        for (int x = 0; x < getWidth(); x += gridSpacing) {
            g2d.drawLine(x, 0, x, getHeight());
        }
        for (int y = 0; y < getHeight(); y += gridSpacing) {
            g2d.drawLine(0, y, getWidth(), y);
        }
    }
    
    private void drawEdges(Graphics2D g2d) {
        g2d.setColor(new Color(200, 200, 200));
        g2d.setStroke(new BasicStroke(1.0f));
        
        for (CityMapNode node : cityNodes.values()) {
            Point2D nodePos = mapToScreen(node.getX(), node.getY());
            
            for (CityMapNode.Edge edge : node.getAdjacentEdges()) {
                CityMapNode destNode = cityNodes.get(edge.getDestinationNode());
                if (destNode != null) {
                    Point2D destPos = mapToScreen(destNode.getX(), destNode.getY());
                    g2d.drawLine((int)nodePos.getX(), (int)nodePos.getY(),
                               (int)destPos.getX(), (int)destPos.getY());
                }
            }
        }
    }
    
    private void drawNodes(Graphics2D g2d) {
        for (CityMapNode node : cityNodes.values()) {
            Point2D pos = mapToScreen(node.getX(), node.getY());
            
            // Draw node circle
            g2d.setColor(new Color(100, 150, 200));
            g2d.fillOval((int)pos.getX() - 6, (int)pos.getY() - 6, 12, 12);
            
            // Draw node border
            g2d.setColor(Color.DARK_GRAY);
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.drawOval((int)pos.getX() - 6, (int)pos.getY() - 6, 12, 12);
            
            // Draw node ID
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            String nodeId = String.valueOf(node.getNodeId());
            FontMetrics fm = g2d.getFontMetrics();
            int textX = (int)pos.getX() - fm.stringWidth(nodeId) / 2;
            int textY = (int)pos.getY() + fm.getAscent() / 2;
            g2d.drawString(nodeId, textX, textY);
        }
    }
    
    private void drawPaths(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        for (PathVisualization pathViz : activePaths) {
            Color pathColor = pathViz.color;
            
            // Highlight selected path
            if (pathViz == selectedPath) {
                g2d.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.setColor(new Color(pathColor.getRed(), pathColor.getGreen(), pathColor.getBlue(), 255));
            } else {
                g2d.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.setColor(new Color(pathColor.getRed(), pathColor.getGreen(), pathColor.getBlue(), 180));
            }
            
            // Draw path segments
            for (int i = 0; i < pathViz.path.size() - 1; i++) {
                CityMapNode node1 = cityNodes.get(pathViz.path.get(i));
                CityMapNode node2 = cityNodes.get(pathViz.path.get(i + 1));
                
                if (node1 != null && node2 != null) {
                    Point2D pos1 = mapToScreen(node1.getX(), node1.getY());
                    Point2D pos2 = mapToScreen(node2.getX(), node2.getY());
                    
                    g2d.drawLine((int)pos1.getX(), (int)pos1.getY(),
                               (int)pos2.getX(), (int)pos2.getY());
                }
            }
            
            // Draw start and end markers
            if (!pathViz.path.isEmpty()) {
                drawPathMarker(g2d, pathViz.path.get(0), "S", Color.GREEN);
                drawPathMarker(g2d, pathViz.path.get(pathViz.path.size() - 1), "E", Color.RED);
            }
        }
    }
    
    private void drawPathMarker(Graphics2D g2d, int nodeId, String marker, Color color) {
        CityMapNode node = cityNodes.get(nodeId);
        if (node != null) {
            Point2D pos = mapToScreen(node.getX(), node.getY());
            
            g2d.setColor(color);
            g2d.fillOval((int)pos.getX() - 8, (int)pos.getY() - 8, 16, 16);
            
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            FontMetrics fm = g2d.getFontMetrics();
            int textX = (int)pos.getX() - fm.stringWidth(marker) / 2;
            int textY = (int)pos.getY() + fm.getAscent() / 2;
            g2d.drawString(marker, textX, textY);
        }
    }
    
    private void drawLegend(Graphics2D g2d) {
        if (activePaths.isEmpty()) return;
        
        int legendX = 10;
        int legendY = 10;
        int lineHeight = 20;
        
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.fillRect(legendX - 5, legendY - 5, 150, activePaths.size() * lineHeight + 10);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(legendX - 5, legendY - 5, 150, activePaths.size() * lineHeight + 10);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        for (int i = 0; i < activePaths.size(); i++) {
            PathVisualization path = activePaths.get(i);
            
            // Draw color indicator
            g2d.setColor(path.color);
            g2d.fillRect(legendX, legendY + i * lineHeight, 15, 10);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(legendX, legendY + i * lineHeight, 15, 10);
            
            // Draw label
            g2d.drawString(path.label, legendX + 20, legendY + i * lineHeight + 10);
        }
    }
    
    private Point2D mapToScreen(double mapX, double mapY) {
        double screenX = margin + (mapX / mapWidth) * (getWidth() - 2 * margin);
        double screenY = margin + (mapY / mapHeight) * (getHeight() - 2 * margin);
        return new Point2D.Double(screenX, screenY);
    }
    
    private void calculateMapBounds() {
        if (cityNodes.isEmpty()) return;
        
        double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;
        
        for (CityMapNode node : cityNodes.values()) {
            minX = Math.min(minX, node.getX());
            maxX = Math.max(maxX, node.getX());
            minY = Math.min(minY, node.getY());
            maxY = Math.max(maxY, node.getY());
        }
        
        mapWidth = maxX - minX;
        mapHeight = maxY - minY;
        
        if (mapWidth == 0) mapWidth = 1;
        if (mapHeight == 0) mapHeight = 1;
    }
    
    private void handleMouseClick(int screenX, int screenY) {
        // Find closest node to click
        CityMapNode closestNode = null;
        double closestDistance = Double.MAX_VALUE;
        
        for (CityMapNode node : cityNodes.values()) {
            Point2D nodePos = mapToScreen(node.getX(), node.getY());
            double distance = Point2D.distance(screenX, screenY, nodePos.getX(), nodePos.getY());
            
            if (distance < 15 && distance < closestDistance) {
                closestNode = node;
                closestDistance = distance;
            }
        }
        
        if (closestNode != null) {
            // Show node information
            showNodeInfo(closestNode);
        }
    }
    
    private void showNodeInfo(CityMapNode node) {
        String info = String.format("Node %d\nPosition: (%.1f, %.1f)\nConnections: %d",
                                   node.getNodeId(), node.getX(), node.getY(), 
                                   node.getAdjacentEdges().size());
        
        JOptionPane.showMessageDialog(this, info, "Node Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Inner class for path visualization data
    private static class PathVisualization {
        public List<Integer> path;
        public Color color;
        public String label;
        
        public PathVisualization(List<Integer> path, Color color, String label) {
            this.path = new ArrayList<>(path);
            this.color = color;
            this.label = label;
        }
    }
}
