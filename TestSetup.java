// Quick test to verify the project structure works
// This creates a simple map file for testing

public class TestSetup {
    public static void main(String[] args) {
        // Create a simple test map file
        try {
            java.io.PrintWriter writer = new java.io.PrintWriter("city_map.csv");
            
            // Simple 3x3 grid map
            writer.println("# Simple 3x3 grid for testing");
            writer.println("# Format: nodeId1,x1,y1,nodeId2,x2,y2,weight,roadType");
            
            // Horizontal connections
            writer.println("0,0,0,1,1,0,1.0,street");
            writer.println("1,1,0,2,2,0,1.0,street");
            writer.println("3,0,1,4,1,1,1.0,street");
            writer.println("4,1,1,5,2,1,1.0,street");
            writer.println("6,0,2,7,1,2,1.0,street");
            writer.println("7,1,2,8,2,2,1.0,street");
            
            // Vertical connections
            writer.println("0,0,0,3,0,1,1.0,street");
            writer.println("3,0,1,6,0,2,1.0,street");
            writer.println("1,1,0,4,1,1,1.0,street");
            writer.println("4,1,1,7,1,2,1.0,street");
            writer.println("2,2,0,5,2,1,1.0,street");
            writer.println("5,2,1,8,2,2,1.0,street");
            
            writer.close();
            System.out.println("Created test city_map.csv file");
            
        } catch (Exception e) {
            System.err.println("Error creating test map: " + e.getMessage());
        }
        
        System.out.println("Test setup complete!");
    }
}
