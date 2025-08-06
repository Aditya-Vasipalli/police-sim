package models;

// This class represents a road/connection between two places in the city
public class Edge {
    private int destination;     // The node where this edge leads
    private double weight;       // The cost/distance of traversing this edge

    public Edge(int destination, double weight) {
        // Store the edge details
        this.destination = destination;
        this.weight = weight;
    }

    public int getDestination() {
        // Return the destination node of this edge
        return destination;
    }

    public double getWeight() {
        // Return the weight/cost of this edge
        return weight;
    }
}
