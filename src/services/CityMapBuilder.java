package services;

import models.Graph;
import java.io.*;

// This class loads the city map from a CSV file
public class CityMapBuilder {
    private Graph graph;

    public CityMapBuilder() {
        graph = new Graph(); // Initialize with an empty graph
    }

    public void loadFromCSV(String filePath) throws IOException {
        // Read CSV file and build the graph from edge data
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;

        while ((line = br.readLine()) != null) {
            String[] tokens = line.split(","); // Parse comma-separated values
            int from = Integer.parseInt(tokens[0].trim());
            int to = Integer.parseInt(tokens[1].trim());
            double weight = Double.parseDouble(tokens[2].trim());

            graph.addEdge(from, to, weight); // Add bidirectional edge to graph
        }

        br.close(); // Close the file reader
    }

    public Graph getGraph() {
        return graph; // Return the constructed city graph
    }
}
