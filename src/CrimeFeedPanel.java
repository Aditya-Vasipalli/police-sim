// CrimeFeedPanel.java
// GUI: Displays new crimes in a scrollable feed

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CrimeFeedPanel extends JPanel {
    private JList<CrimeEvent> crimeList;
    private DefaultListModel<CrimeEvent> listModel;
    private JScrollPane scrollPane;
    private JLabel statusLabel;
    private int maxCrimes = 50; // Limit feed size
    
    public CrimeFeedPanel() {
        initializePanel();
    }
    
    private void initializePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Crime Feed"));
        
        // Create list model and list
        listModel = new DefaultListModel<>();
        crimeList = new JList<>(listModel);
        crimeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        crimeList.setCellRenderer(new CrimeListCellRenderer());
        
        // Create scroll pane
        scrollPane = new JScrollPane(crimeList);
        scrollPane.setPreferredSize(new Dimension(300, 400));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        // Create status label
        statusLabel = new JLabel("No crimes reported");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        // Add components
        add(scrollPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    public void addCrimeToFeed(CrimeEvent crime) {
        // Add to top of list
        listModel.insertElementAt(crime, 0);
        
        // Limit list size
        if (listModel.size() > maxCrimes) {
            listModel.removeElementAt(listModel.size() - 1);
        }
        
        // Update status
        updateStatusLabel();
        
        // Auto-scroll to top
        crimeList.ensureIndexIsVisible(0);
        
        // Refresh display
        crimeList.revalidate();
        crimeList.repaint();
    }
    
    public void updateCrimeStatus(int crimeId, String newStatus) {
        for (int i = 0; i < listModel.size(); i++) {
            CrimeEvent crime = listModel.getElementAt(i);
            if (crime.crimeId == crimeId) {
                crime.status = newStatus;
                listModel.setElementAt(crime, i);
                break;
            }
        }
        crimeList.repaint();
    }
    
    public void clearFeed() {
        listModel.clear();
        updateStatusLabel();
    }
    
    private void updateStatusLabel() {
        int totalCrimes = listModel.size();
        int activeCrimes = 0;
        int resolvedCrimes = 0;
        
        for (int i = 0; i < listModel.size(); i++) {
            CrimeEvent crime = listModel.getElementAt(i);
            if ("Resolved".equals(crime.status)) {
                resolvedCrimes++;
            } else if (!"Unresolved".equals(crime.status)) {
                activeCrimes++;
            }
        }
        
        statusLabel.setText(String.format("Total: %d | Active: %d | Resolved: %d", 
                                         totalCrimes, activeCrimes, resolvedCrimes));
    }
    
    // Custom cell renderer for crime list
    private class CrimeListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof CrimeEvent) {
                CrimeEvent crime = (CrimeEvent) value;
                
                // Create formatted text
                String timeStr = crime.timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                setText(String.format("<html><b>%s</b> - %s<br/><small>%s | Node %d | %s</small></html>",
                    crime.crimeType, timeStr, crime.priority, crime.location, crime.status));
                
                // Color coding based on priority and status
                if (!isSelected) {
                    switch (crime.priority) {
                        case "High":
                            setBackground(new Color(255, 230, 230)); // Light red
                            break;
                        case "Medium":
                            setBackground(new Color(255, 255, 230)); // Light yellow
                            break;
                        case "Low":
                            setBackground(new Color(230, 255, 230)); // Light green
                            break;
                        default:
                            setBackground(Color.WHITE);
                    }
                    
                    if ("Resolved".equals(crime.status)) {
                        setBackground(new Color(240, 240, 240)); // Gray for resolved
                    }
                }
            }
            
            return c;
        }
    }
    
    // Data class for crime events
    public static class CrimeEvent {
        public int crimeId;
        public String crimeType;
        public int location;
        public String priority;
        public String status;
        public LocalDateTime timestamp;
        public String description;
        
        public CrimeEvent(int crimeId, String crimeType, int location, String priority, 
                         String status, String description) {
            this.crimeId = crimeId;
            this.crimeType = crimeType;
            this.location = location;
            this.priority = priority;
            this.status = status;
            this.description = description;
            this.timestamp = LocalDateTime.now();
        }
        
        @Override
        public String toString() {
            return String.format("%s - %s (Node %d)", crimeType, priority, location);
        }
    }
}
