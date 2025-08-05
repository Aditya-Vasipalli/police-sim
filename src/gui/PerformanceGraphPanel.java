package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

/**
 * GUI panel that displays performance graphs for the police simulation
 * Shows average response times, crime resolution rates, and other metrics
 */
public class PerformanceGraphPanel extends JPanel {
    private java.util.List<Double> responseTimeData;
    private java.util.List<Double> resolutionRateData;
    private java.util.List<String> timeLabels;
    private JComboBox<String> metricSelector;
    private JLabel statsLabel;
    
    // Graph properties
    private static final int MARGIN = 50;
    private static final Color GRID_COLOR = new Color(200, 200, 200);
    private static final Color RESPONSE_TIME_COLOR = new Color(50, 150, 250);
    private static final Color RESOLUTION_RATE_COLOR = new Color(250, 100, 50);
    private static final Stroke GRAPH_STROKE = new BasicStroke(2.0f);
    private static final Stroke GRID_STROKE = new BasicStroke(1.0f);
    
    public PerformanceGraphPanel() {
        initializePanel();
        loadSampleData();
    }
    
    private void initializePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Performance Metrics"));
        setBackground(Color.WHITE);
        
        // Initialize data collections
        responseTimeData = new ArrayList<>();
        resolutionRateData = new ArrayList<>();
        timeLabels = new ArrayList<>();
        
        // Create control panel
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.NORTH);
        
        // Create stats panel
        statsLabel = new JLabel("Statistics will appear here");
        statsLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(statsLabel, BorderLayout.SOUTH);
        
        // Set preferred size
        setPreferredSize(new Dimension(600, 400));
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        panel.add(new JLabel("Metric:"));
        
        String[] metrics = {"Response Time", "Resolution Rate", "Both"};
        metricSelector = new JComboBox<>(metrics);
        metricSelector.addActionListener(e -> repaint());
        panel.add(metricSelector);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshData());
        panel.add(refreshButton);
        
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearData());
        panel.add(clearButton);
        
        return panel;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Enable antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Calculate drawing area
        int width = getWidth() - 2 * MARGIN;
        int height = getHeight() - 2 * MARGIN - 50; // Leave space for controls and stats
        
        if (width <= 0 || height <= 0) {
            g2d.dispose();
            return;
        }
        
        // Draw grid and axes
        drawGrid(g2d, width, height);
        
        // Draw graphs based on selected metric
        String selectedMetric = (String) metricSelector.getSelectedItem();
        switch (selectedMetric) {
            case "Response Time":
                if (!responseTimeData.isEmpty()) {
                    drawResponseTimeGraph(g2d, width, height);
                }
                break;
            case "Resolution Rate":
                if (!resolutionRateData.isEmpty()) {
                    drawResolutionRateGraph(g2d, width, height);
                }
                break;
            case "Both":
                if (!responseTimeData.isEmpty()) {
                    drawResponseTimeGraph(g2d, width, height);
                }
                if (!resolutionRateData.isEmpty()) {
                    drawResolutionRateGraph(g2d, width, height);
                }
                break;
        }
        
        // Draw legend
        drawLegend(g2d, selectedMetric);
        
        g2d.dispose();
    }
    
    private void drawGrid(Graphics2D g2d, int width, int height) {
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2.0f));
        
        // Draw axes
        g2d.drawLine(MARGIN, MARGIN + height, MARGIN + width, MARGIN + height); // X-axis
        g2d.drawLine(MARGIN, MARGIN, MARGIN, MARGIN + height); // Y-axis
        
        // Draw grid lines
        g2d.setColor(GRID_COLOR);
        g2d.setStroke(GRID_STROKE);
        
        // Vertical grid lines
        int numVerticalLines = 10;
        for (int i = 1; i < numVerticalLines; i++) {
            int x = MARGIN + (i * width / numVerticalLines);
            g2d.drawLine(x, MARGIN, x, MARGIN + height);
        }
        
        // Horizontal grid lines
        int numHorizontalLines = 8;
        for (int i = 1; i < numHorizontalLines; i++) {
            int y = MARGIN + (i * height / numHorizontalLines);
            g2d.drawLine(MARGIN, y, MARGIN + width, y);
        }
        
        // Draw axis labels
        g2d.setColor(Color.BLACK);
        FontMetrics fm = g2d.getFontMetrics();
        
        // Y-axis label
        g2d.drawString("Value", 10, MARGIN + height / 2);
        
        // X-axis label
        String xLabel = "Time";
        int xLabelWidth = fm.stringWidth(xLabel);
        g2d.drawString(xLabel, MARGIN + width / 2 - xLabelWidth / 2, getHeight() - 10);
        
        // Y-axis scale
        for (int i = 0; i <= 8; i++) {
            String label = String.format("%.1f", (8 - i) * 10.0 / 8);
            g2d.drawString(label, 5, MARGIN + (i * height / 8) + 5);
        }
    }
    
    private void drawResponseTimeGraph(Graphics2D g2d, int width, int height) {
        if (responseTimeData.size() < 2) return;
        
        g2d.setColor(RESPONSE_TIME_COLOR);
        g2d.setStroke(GRAPH_STROKE);
        
        // Find max value for scaling
        double maxValue = responseTimeData.stream().mapToDouble(Double::doubleValue).max().orElse(10.0);
        maxValue = Math.max(maxValue, 10.0); // Minimum scale
        
        // Draw the line graph
        GeneralPath path = new GeneralPath();
        for (int i = 0; i < responseTimeData.size(); i++) {
            double value = responseTimeData.get(i);
            int x = MARGIN + (i * width / Math.max(1, responseTimeData.size() - 1));
            int y = MARGIN + height - (int) ((value / maxValue) * height);
            
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
            
            // Draw data points
            g2d.fillOval(x - 3, y - 3, 6, 6);
        }
        
        g2d.draw(path);
    }
    
    private void drawResolutionRateGraph(Graphics2D g2d, int width, int height) {
        if (resolutionRateData.size() < 2) return;
        
        g2d.setColor(RESOLUTION_RATE_COLOR);
        g2d.setStroke(GRAPH_STROKE);
        
        // Resolution rate is typically 0-100%
        double maxValue = 100.0;
        
        // Draw the line graph
        GeneralPath path = new GeneralPath();
        for (int i = 0; i < resolutionRateData.size(); i++) {
            double value = resolutionRateData.get(i);
            int x = MARGIN + (i * width / Math.max(1, resolutionRateData.size() - 1));
            int y = MARGIN + height - (int) ((value / maxValue) * height);
            
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
            
            // Draw data points
            g2d.fillRect(x - 2, y - 2, 4, 4);
        }
        
        g2d.draw(path);
    }
    
    private void drawLegend(Graphics2D g2d, String selectedMetric) {
        int legendX = getWidth() - 150;
        int legendY = MARGIN;
        
        g2d.setColor(Color.WHITE);
        g2d.fillRect(legendX - 10, legendY - 5, 140, 60);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(legendX - 10, legendY - 5, 140, 60);
        
        if (selectedMetric.equals("Response Time") || selectedMetric.equals("Both")) {
            g2d.setColor(RESPONSE_TIME_COLOR);
            g2d.fillRect(legendX, legendY, 15, 3);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Response Time", legendX + 20, legendY + 10);
            legendY += 20;
        }
        
        if (selectedMetric.equals("Resolution Rate") || selectedMetric.equals("Both")) {
            g2d.setColor(RESOLUTION_RATE_COLOR);
            g2d.fillRect(legendX, legendY, 15, 3);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Resolution Rate", legendX + 20, legendY + 10);
        }
    }
    
    /**
     * Update response time data
     * @param newData List of response time values
     */
    public void updateResponseTimeData(java.util.List<Double> newData) {
        this.responseTimeData = new ArrayList<>(newData);
        updateStats();
        repaint();
    }
    
    /**
     * Update resolution rate data
     * @param newData List of resolution rate values (0-100)
     */
    public void updateResolutionRateData(java.util.List<Double> newData) {
        this.resolutionRateData = new ArrayList<>(newData);
        updateStats();
        repaint();
    }
    
    /**
     * Add a new response time data point
     * @param responseTime Response time in minutes
     */
    public void addResponseTimePoint(double responseTime) {
        responseTimeData.add(responseTime);
        // Keep only last 50 points to prevent overcrowding
        if (responseTimeData.size() > 50) {
            responseTimeData.remove(0);
        }
        updateStats();
        repaint();
    }
    
    /**
     * Add a new resolution rate data point
     * @param resolutionRate Resolution rate as percentage (0-100)
     */
    public void addResolutionRatePoint(double resolutionRate) {
        resolutionRateData.add(resolutionRate);
        // Keep only last 50 points
        if (resolutionRateData.size() > 50) {
            resolutionRateData.remove(0);
        }
        updateStats();
        repaint();
    }
    
    private void updateStats() {
        StringBuilder stats = new StringBuilder("Stats: ");
        
        if (!responseTimeData.isEmpty()) {
            double avgResponseTime = responseTimeData.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
            stats.append(String.format("Avg Response: %.1f min", avgResponseTime));
        }
        
        if (!resolutionRateData.isEmpty()) {
            double avgResolutionRate = resolutionRateData.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
            if (stats.length() > 7) stats.append(", ");
            stats.append(String.format("Avg Resolution: %.1f%%", avgResolutionRate));
        }
        
        if (stats.length() == 7) { // Only "Stats: " was added
            stats.append("No data available");
        }
        
        statsLabel.setText(stats.toString());
    }
    
    private void refreshData() {
        // In a real implementation, this would fetch fresh data from the simulator
        repaint();
    }
    
    private void clearData() {
        responseTimeData.clear();
        resolutionRateData.clear();
        timeLabels.clear();
        updateStats();
        repaint();
    }
    
    private void loadSampleData() {
        // Load sample data for demonstration
        Random random = new Random();
        
        // Generate sample response time data
        for (int i = 0; i < 20; i++) {
            responseTimeData.add(3.0 + random.nextGaussian() * 1.5);
            resolutionRateData.add(70.0 + random.nextGaussian() * 15.0);
            timeLabels.add("T" + i);
        }
        
        updateStats();
    }
}
