package models;

// This class is for the road between two places
public class Edge {
    private int destination;     // TODO: where the road goes
    private double weight;       // TODO: how long the road is (like distance)

    public Edge(int destination, double weight) {
        // TODO: store road details
        this.destination = destination;
        this.weight = weight;
    }

    public int getDestination() {
        // TODO: give the end point of the road
        return destination;
    }

    public double getWeight() {
        // TODO: give the weight (like time/distance)
        return weight;
    }
}
