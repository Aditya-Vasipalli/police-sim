public class TestBigMap {
    public static void main(String[] args) {
        try {
            CityMap map = new CityMap("big_city_map.csv");
            System.out.println("Successfully loaded big city map!");
            System.out.println("Total nodes: " + map.getTotalNodes());
            System.out.println("Total edges: " + map.getTotalEdges());
        } catch (Exception e) {
            System.out.println("Failed to load big city map: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
