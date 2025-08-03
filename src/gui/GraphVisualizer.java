package gui;

import models.Edge;
import models.Graph;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class GraphVisualizer extends JPanel {
    private Graph graph;

    public GraphVisualizer(Graph graph) {
        this.graph = graph; // TODO: save the map to draw
        setPreferredSize(new Dimension(600, 600));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Map<Integer, Point> nodePositions = new HashMap<>();
        int spacing = 100;
        int index = 0;

        for (Integer node : graph.getAllEdges().keySet()) {
            int x = spacing + (index % 5) * spacing;
            int y = spacing + (index / 5) * spacing;
            nodePositions.put(node, new Point(x, y));
            index++;
        }

        for (Map.Entry<Integer, List<Edge>> entry : graph.getAllEdges().entrySet()) {
            int from = entry.getKey();
            for (Edge edge : entry.getValue()) {
                int to = edge.getDestination();
                Point p1 = nodePositions.get(from);
                Point p2 = nodePositions.get(to);
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }

        for (Map.Entry<Integer, Point> entry : nodePositions.entrySet()) {
            int nodeId = entry.getKey();
            Point p = entry.getValue();
            g.setColor(Color.BLUE);
            g.fillOval(p.x - 5, p.y - 5, 10, 10);
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(nodeId), p.x + 5, p.y - 5);
        }
    }
}
