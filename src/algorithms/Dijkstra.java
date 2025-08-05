package algorithms;

import models.CityMapNode;
import java.util.*;

public class Dijkstra {
    
    /**
     * Custom Min-Heap implementation optimized for pathfinding
     */
    public static class MinHeap {
        private List<Node> heap;
        private Map<Integer, Integer> nodeToIndex; // Quick lookup for decrease-key
        
        public static class Node implements Comparable<Node> {
            public int nodeId;
            public double distance;
            
            public Node(int nodeId, double distance) {
                this.nodeId = nodeId;
                this.distance = distance;
            }
            
            @Override
            public int compareTo(Node other) {
                return Double.compare(this.distance, other.distance);
            }
        }
        
        public MinHeap() {
            this.heap = new ArrayList<>();
            this.nodeToIndex = new HashMap<>();
        }
        
        public void insert(int nodeId, double distance) {
            Node node = new Node(nodeId, distance);
            heap.add(node);
            int index = heap.size() - 1;
            nodeToIndex.put(nodeId, index);
            heapifyUp(index);
        }
        
        public Node extractMin() {
            if (heap.isEmpty()) return null;
            
            Node min = heap.get(0);
            Node last = heap.get(heap.size() - 1);
            
            heap.set(0, last);
            nodeToIndex.put(last.nodeId, 0);
            heap.remove(heap.size() - 1);
            nodeToIndex.remove(min.nodeId);
            
            if (!heap.isEmpty()) {
                heapifyDown(0);
            }
            
            return min;
        }
        
        public void decreaseKey(int nodeId, double newDistance) {
            Integer index = nodeToIndex.get(nodeId);
            if (index != null && newDistance < heap.get(index).distance) {
                heap.get(index).distance = newDistance;
                heapifyUp(index);
            }
        }
        
        public boolean isEmpty() {
            return heap.isEmpty();
        }
        
        public boolean contains(int nodeId) {
            return nodeToIndex.containsKey(nodeId);
        }
        
        private void heapifyUp(int index) {
            while (index > 0) {
                int parentIndex = (index - 1) / 2;
                if (heap.get(index).compareTo(heap.get(parentIndex)) >= 0) break;
                
                swap(index, parentIndex);
                index = parentIndex;
            }
        }
        
        private void heapifyDown(int index) {
            while (true) {
                int smallest = index;
                int leftChild = 2 * index + 1;
                int rightChild = 2 * index + 2;
                
                if (leftChild < heap.size() && 
                    heap.get(leftChild).compareTo(heap.get(smallest)) < 0) {
                    smallest = leftChild;
                }
                
                if (rightChild < heap.size() && 
                    heap.get(rightChild).compareTo(heap.get(smallest)) < 0) {
                    smallest = rightChild;
                }
                
                if (smallest == index) break;
                
                swap(index, smallest);
                index = smallest;
            }
        }
        
        private void swap(int i, int j) {
            Node temp = heap.get(i);
            heap.set(i, heap.get(j));
            heap.set(j, temp);
            
            // Update position mapping
            nodeToIndex.put(heap.get(i).nodeId, i);
            nodeToIndex.put(heap.get(j).nodeId, j);
        }
    }
    
    /**
     * Dijkstra's algorithm result container
     */
    public static class DijkstraResult {
        private Map<Integer, Double> distances;
        private Map<Integer, Integer> predecessors;
        private int sourceNode;
        
        public DijkstraResult(int sourceNode) {
            this.sourceNode = sourceNode;
            this.distances = new HashMap<>();
            this.predecessors = new HashMap<>();
        }
        
        public List<Integer> getPath(int targetNode) {
            List<Integer> path = new ArrayList<>();
            Integer current = targetNode;
            
            while (current != null) {
                path.add(0, current);
                current = predecessors.get(current);
            }
            
            return path.get(0) == sourceNode ? path : new ArrayList<>();
        }
        
        public double getDistance(int nodeId) {
            return distances.getOrDefault(nodeId, Double.MAX_VALUE);
        }
        
        public void setDistance(int nodeId, double distance) {
            distances.put(nodeId, distance);
        }
        
        public void setPredecessor(int nodeId, int predecessor) {
            predecessors.put(nodeId, predecessor);
        }
    }
    
    /**
     * Single-source shortest path using Dijkstra's algorithm
     */
    public static DijkstraResult findShortestPaths(Map<Integer, CityMapNode> cityMap, int sourceNode) {
        DijkstraResult result = new DijkstraResult(sourceNode);
        MinHeap minHeap = new MinHeap();
        Set<Integer> visited = new HashSet<>();
        
        // Initialize distances
        for (int nodeId : cityMap.keySet()) {
            result.setDistance(nodeId, nodeId == sourceNode ? 0.0 : Double.MAX_VALUE);
        }
        
        minHeap.insert(sourceNode, 0.0);
        
        while (!minHeap.isEmpty()) {
            MinHeap.Node current = minHeap.extractMin();
            int currentNode = current.nodeId;
            
            if (visited.contains(currentNode)) continue;
            visited.add(currentNode);
            
            CityMapNode cityNode = cityMap.get(currentNode);
            if (cityNode == null) continue;
            
            // Process all adjacent edges
            for (CityMapNode.Edge edge : cityNode.getAdjacentEdges()) {
                int neighbor = edge.getDestinationNode();
                if (visited.contains(neighbor)) continue;
                
                double edgeWeight = edge.getDynamicWeight(cityNode.getTrafficMultiplier());
                double newDistance = result.getDistance(currentNode) + edgeWeight;
                
                if (newDistance < result.getDistance(neighbor)) {
                    result.setDistance(neighbor, newDistance);
                    result.setPredecessor(neighbor, currentNode);
                    minHeap.decreaseKey(neighbor, newDistance);
                    
                    // If neighbor not in heap yet, insert it
                    if (result.getDistance(neighbor) == Double.MAX_VALUE) {
                        minHeap.insert(neighbor, newDistance);
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * Single target shortest path (optimized for single destination)
     */
    public static List<Integer> findShortestPath(Map<Integer, CityMapNode> cityMap, 
                                               int sourceNode, int targetNode) {
        MinHeap minHeap = new MinHeap();
        Map<Integer, Double> distances = new HashMap<>();
        Map<Integer, Integer> predecessors = new HashMap<>();
        Set<Integer> visited = new HashSet<>();
        
        // Initialize distances
        for (int nodeId : cityMap.keySet()) {
            distances.put(nodeId, nodeId == sourceNode ? 0.0 : Double.MAX_VALUE);
        }
        
        minHeap.insert(sourceNode, 0.0);
        
        while (!minHeap.isEmpty()) {
            MinHeap.Node current = minHeap.extractMin();
            int currentNode = current.nodeId;
            
            if (visited.contains(currentNode)) continue;
            visited.add(currentNode);
            
            // Early termination if we reached target
            if (currentNode == targetNode) {
                break;
            }
            
            CityMapNode cityNode = cityMap.get(currentNode);
            if (cityNode == null) continue;
            
            for (CityMapNode.Edge edge : cityNode.getAdjacentEdges()) {
                int neighbor = edge.getDestinationNode();
                if (visited.contains(neighbor)) continue;
                
                double edgeWeight = edge.getDynamicWeight(cityNode.getTrafficMultiplier());
                double newDistance = distances.get(currentNode) + edgeWeight;
                
                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    predecessors.put(neighbor, currentNode);
                    
                    if (!minHeap.contains(neighbor)) {
                        minHeap.insert(neighbor, newDistance);
                    } else {
                        minHeap.decreaseKey(neighbor, newDistance);
                    }
                }
            }
        }
        
        // Reconstruct path
        List<Integer> path = new ArrayList<>();
        Integer current = targetNode;
        
        while (current != null) {
            path.add(0, current);
            current = predecessors.get(current);
        }
        
        return path.size() > 1 ? path : new ArrayList<>();
    }
}
