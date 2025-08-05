package services;

import models.Graph;
import java.io.*;

// This class loads the map from a file
public class CityMapBuilder {
    private Graph graph;

    public CityMapBuilder() {
        graph = new Graph(); // TODO: start with an empty map
    }

    public void loadFromCSV(String filePath) throws IOException {
        // TODO: read CSV file and build the graph
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;

        while ((line = br.readLine()) != null) {
            String[] tokens = line.split(","); // TODO: split by comma
            int from = Integer.parseInt(tokens[0].trim());
            int to = Integer.parseInt(tokens[1].trim());
            double weight = Double.parseDouble(tokens[2].trim());

            graph.addEdge(from, to, weight); // TODO: add road
        }

        br.close(); // TODO: stop reading file
    }

    public Graph getGraph() {
        return graph; // TODO: give the built city map
    }
}
