// PoliceAssignmentPanel.java
// GUI: Displays police unit assignments to crimes
// Fixed duplicate method errors

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;

public class PoliceAssignmentPanel extends JPanel {
    private JTable assignmentTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JLabel statusLabel;
    
    private static final String[] COLUMN_NAMES = {
        "Unit ID", "Crime ID", "Crime Type", "Location", "Priority", 
        "Status", "Response Time", "Distance"
    };
    
    public PoliceAssignmentPanel() {
        initializePanel();
    }
    
    private void initializePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Police Unit Assignments"));
        
        // Create table model
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        // Create table
        assignmentTable = new JTable(tableModel);
        assignmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        assignmentTable.setRowHeight(25);
        
        // Configure column widths
        configureColumnWidths();
        
        // Add color coding for status
        assignmentTable.setDefaultRenderer(Object.class, new StatusCellRenderer());
        
        // Create scroll pane
        scrollPane = new JScrollPane(assignmentTable);
        scrollPane.setPreferredSize(new Dimension(800, 300));
        
        // Create status label
        statusLabel = new JLabel("No assignments");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        // Add components
        add(scrollPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    private void configureColumnWidths() {
        TableColumnModel columnModel = assignmentTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(80);  // Unit ID
        columnModel.getColumn(1).setPreferredWidth(80);  // Crime ID
        columnModel.getColumn(2).setPreferredWidth(100); // Crime Type
        columnModel.getColumn(3).setPreferredWidth(80);  // Location
        columnModel.getColumn(4).setPreferredWidth(80);  // Priority
        columnModel.getColumn(5).setPreferredWidth(100); // Status
        columnModel.getColumn(6).setPreferredWidth(120); // Response Time
        columnModel.getColumn(7).setPreferredWidth(100); // Distance
    }
    
    public void updateAssignments(Map<Integer, AssignmentInfo> assignments) {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Add new assignments
        for (AssignmentInfo assignment : assignments.values()) {
            Object[] rowData = {
                "Unit " + assignment.unitId,
                "Crime " + assignment.crimeId,
                assignment.crimeType,
                "Node " + assignment.location,
                assignment.priority,
                assignment.status,
                assignment.responseTime + " min",
                String.format("%.2f km", assignment.distance)
            };
            tableModel.addRow(rowData);
        }
        
        // Update status
        updateStatusLabel(assignments.size());
        
        // Refresh table
        assignmentTable.revalidate();
        assignmentTable.repaint();
    }
    
    public void addAssignment(AssignmentInfo assignment) {
        Object[] rowData = {
            "Unit " + assignment.unitId,
            "Crime " + assignment.crimeId,
            assignment.crimeType,
            "Node " + assignment.location,
            assignment.priority,
            assignment.status,
            assignment.responseTime + " min",
            String.format("%.2f km", assignment.distance)
        };
        tableModel.addRow(rowData);
        updateStatusLabel(tableModel.getRowCount());
    }
    
    public void updateAssignmentStatus(int unitId, String newStatus) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String unitCell = (String) tableModel.getValueAt(i, 0);
            if (unitCell.equals("Unit " + unitId)) {
                tableModel.setValueAt(newStatus, i, 5);
                break;
            }
        }
        assignmentTable.repaint();
    }
    
    private void updateStatusLabel(int assignmentCount) {
        if (assignmentCount == 0) {
            statusLabel.setText("No active assignments");
        } else {
            statusLabel.setText(assignmentCount + " active assignment(s)");
        }
    }
    
    // Custom cell renderer for status-based coloring
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (!isSelected) {
                String status = (String) table.getValueAt(row, 5); // Status column
                switch (status) {
                    case "Dispatched":
                        c.setBackground(new Color(255, 255, 200)); // Light yellow
                        break;
                    case "En Route":
                        c.setBackground(new Color(200, 255, 200)); // Light green
                        break;
                    case "Arrived":
                        c.setBackground(new Color(200, 200, 255)); // Light blue
                        break;
                    case "Resolved":
                        c.setBackground(new Color(220, 255, 220)); // Light green
                        break;
                    case "Unresolved":
                        c.setBackground(new Color(255, 200, 200)); // Light red
                        break;
                    default:
                        c.setBackground(Color.WHITE);
                }
            }
            
            return c;
        }
    }
    
    // Data class for assignment information
    public static class AssignmentInfo {
        public int unitId;
        public int crimeId;
        public String crimeType;
        public int location;
        public String priority;
        public String status;
        public double responseTime;
        public double distance;
        
        public AssignmentInfo(int unitId, int crimeId, String crimeType, int location,
                             String priority, String status, double responseTime, double distance) {
            this.unitId = unitId;
            this.crimeId = crimeId;
            this.crimeType = crimeType;
            this.location = location;
            this.priority = priority;
            this.status = status;
            this.responseTime = responseTime;
            this.distance = distance;
        }
    }
}
