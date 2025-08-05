package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * GUI panel that displays police units in a table format
 * Shows unit ID, status, current location, and assignment details
 */
public class UnitTablePanel extends JPanel {
    private JTable unitTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JLabel statusLabel;
    private JButton refreshButton;
    
    private static final String[] COLUMN_NAMES = {
        "Unit ID", "Status", "Location", "Assigned Crime", "Response Time", "Distance to Target"
    };
    
    // Status colors
    private static final Color AVAILABLE_COLOR = new Color(200, 255, 200); // Light green
    private static final Color DISPATCHED_COLOR = new Color(255, 255, 200); // Light yellow
    private static final Color EN_ROUTE_COLOR = new Color(200, 255, 255); // Light cyan
    private static final Color BUSY_COLOR = new Color(255, 200, 200); // Light red
    private static final Color OFFLINE_COLOR = new Color(220, 220, 220); // Light gray
    
    public UnitTablePanel() {
        initializePanel();
        loadSampleData();
    }
    
    private void initializePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Police Units Status"));
        
        // Create table model
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 4 || column == 5) { // Response Time and Distance columns
                    return Double.class;
                }
                return String.class;
            }
        };
        
        // Create table
        unitTable = new JTable(tableModel);
        unitTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        unitTable.setRowHeight(25);
        unitTable.setAutoCreateRowSorter(true);
        
        // Configure column widths
        configureColumnWidths();
        
        // Add custom cell renderer for status-based coloring
        unitTable.setDefaultRenderer(Object.class, new UnitStatusCellRenderer());
        
        // Create scroll pane
        scrollPane = new JScrollPane(unitTable);
        scrollPane.setPreferredSize(new Dimension(700, 300));
        
        // Create control panel
        JPanel controlPanel = createControlPanel();
        
        // Create status label
        statusLabel = new JLabel("Units loaded: 0");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        // Add components
        add(controlPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshData());
        
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearData());
        
        panel.add(refreshButton);
        panel.add(clearButton);
        
        return panel;
    }
    
    private void configureColumnWidths() {
        TableColumnModel columnModel = unitTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(80);  // Unit ID
        columnModel.getColumn(1).setPreferredWidth(100); // Status
        columnModel.getColumn(2).setPreferredWidth(100); // Location
        columnModel.getColumn(3).setPreferredWidth(120); // Assigned Crime
        columnModel.getColumn(4).setPreferredWidth(120); // Response Time
        columnModel.getColumn(5).setPreferredWidth(130); // Distance
    }
    
    /**
     * Update the table with new unit data
     * @param units List of unit information
     */
    public void updateUnits(List<UnitInfo> units) {
        tableModel.setRowCount(0);
        
        for (UnitInfo unit : units) {
            Object[] rowData = {
                "Unit " + unit.unitId,
                unit.status,
                "Node " + unit.location,
                unit.assignedCrime != null ? "Crime " + unit.assignedCrime : "None",
                unit.responseTime > 0 ? String.format("%.1f min", unit.responseTime) : "N/A",
                unit.distanceToTarget > 0 ? String.format("%.2f km", unit.distanceToTarget) : "N/A"
            };
            tableModel.addRow(rowData);
        }
        
        updateStatusLabel(units.size());
        unitTable.revalidate();
        unitTable.repaint();
    }
    
    /**
     * Add a single unit to the table
     * @param unit Unit information to add
     */
    public void addUnit(UnitInfo unit) {
        Object[] rowData = {
            "Unit " + unit.unitId,
            unit.status,
            "Node " + unit.location,
            unit.assignedCrime != null ? "Crime " + unit.assignedCrime : "None",
            unit.responseTime > 0 ? String.format("%.1f min", unit.responseTime) : "N/A",
            unit.distanceToTarget > 0 ? String.format("%.2f km", unit.distanceToTarget) : "N/A"
        };
        tableModel.addRow(rowData);
        updateStatusLabel(tableModel.getRowCount());
    }
    
    /**
     * Update status of a specific unit
     * @param unitId Unit ID to update
     * @param newStatus New status
     */
    public void updateUnitStatus(int unitId, String newStatus) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String unitCell = (String) tableModel.getValueAt(i, 0);
            if (unitCell.equals("Unit " + unitId)) {
                tableModel.setValueAt(newStatus, i, 1);
                break;
            }
        }
        unitTable.repaint();
    }
    
    /**
     * Update location of a specific unit
     * @param unitId Unit ID to update
     * @param newLocation New location
     */
    public void updateUnitLocation(int unitId, int newLocation) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String unitCell = (String) tableModel.getValueAt(i, 0);
            if (unitCell.equals("Unit " + unitId)) {
                tableModel.setValueAt("Node " + newLocation, i, 2);
                break;
            }
        }
        unitTable.repaint();
    }
    
    private void refreshData() {
        // In a real implementation, this would fetch fresh data from the police manager
        // For now, just repaint the table
        unitTable.repaint();
    }
    
    private void clearData() {
        tableModel.setRowCount(0);
        updateStatusLabel(0);
    }
    
    private void updateStatusLabel(int unitCount) {
        statusLabel.setText("Units loaded: " + unitCount);
    }
    
    private void loadSampleData() {
        // Load some sample data for demonstration
        List<UnitInfo> sampleUnits = new ArrayList<>();
        sampleUnits.add(new UnitInfo(1, "Available", 5, null, 0, 0));
        sampleUnits.add(new UnitInfo(2, "Dispatched", 12, 101, 3.5, 2.1));
        sampleUnits.add(new UnitInfo(3, "En Route", 8, 102, 5.2, 1.8));
        sampleUnits.add(new UnitInfo(4, "Busy", 15, 103, 8.1, 0.0));
        sampleUnits.add(new UnitInfo(5, "Available", 3, null, 0, 0));
        
        updateUnits(sampleUnits);
    }
    
    // Custom cell renderer for status-based coloring
    private class UnitStatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (!isSelected) {
                String status = (String) table.getValueAt(row, 1); // Status column
                switch (status.toLowerCase()) {
                    case "available":
                        c.setBackground(AVAILABLE_COLOR);
                        break;
                    case "dispatched":
                        c.setBackground(DISPATCHED_COLOR);
                        break;
                    case "en route":
                        c.setBackground(EN_ROUTE_COLOR);
                        break;
                    case "busy":
                    case "responding":
                        c.setBackground(BUSY_COLOR);
                        break;
                    case "offline":
                    case "maintenance":
                        c.setBackground(OFFLINE_COLOR);
                        break;
                    default:
                        c.setBackground(Color.WHITE);
                }
            }
            
            return c;
        }
    }
    
    // Data class for unit information
    public static class UnitInfo {
        public int unitId;
        public String status;
        public int location;
        public Integer assignedCrime;
        public double responseTime;
        public double distanceToTarget;
        
        public UnitInfo(int unitId, String status, int location, Integer assignedCrime, 
                       double responseTime, double distanceToTarget) {
            this.unitId = unitId;
            this.status = status;
            this.location = location;
            this.assignedCrime = assignedCrime;
            this.responseTime = responseTime;
            this.distanceToTarget = distanceToTarget;
        }
    }
}
