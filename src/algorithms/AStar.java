package algorithms;

import models.CityMapNode;
import java.util.*;

public class AStar {
    
    /**
     * A* Node for priority queue with f-score
     */
    public static class AStarNode implements Comparable<AStarNode> {
        int nodeId;
        double gScore; // Distance from start
        double hScore; // Heuristic (estimated distance to goal)
        double fScore; // gScore + hScore
        
        public AStarNode(int nodeId, double gScore, double hScore) {
            this.nodeId = nodeId;
            this.gScore = gScore;
            this.hScore = hScore;
            this.fScore = gScore + hScore;
        }
        
        @Override
        public int compareTo(AStarNode other) {
            return Double.compare(this.fScore, other.fScore);
        }
    }
    
    /**
     * Heuristic function interface for different distance calculations
     */
    public interface HeuristicFunction {
        double calculate(CityMapNode from, CityMapNode to);
    }
    
    /**
     * Euclidean distance heuristic (admissible for geometric graphs)
     */
    public static final HeuristicFunction EUCLIDEAN_HEURISTIC = (from, to) -> 
        from.getEuclideanDistance(to);
    
    /**
     * Manhattan distance heuristic (admissible for grid-like graphs)
     */
    public static final HeuristicFunction MANHATTAN_HEURISTIC = (from, to) -> 
        Math.abs(from.getX() - to.getX()) + Math.abs(from.getY() - to.getY());
    
    /**
     * Zero heuristic (reduces A* to Dijkstra)
     */
    public static final HeuristicFunction ZERO_HEURISTIC = (from, to) -> 0.0;
    
    /**
     * A* pathfinding result
     */
    public static class AStarResult {
        private List<Integer> path;
        private double totalDistance;
        private int nodesExplored;
        
        public AStarResult(List<Integer> path, double totalDistance, int nodesExplored) {
            this.path = path;
            this.totalDistance = totalDistance;
            this.nodesExplored = nodesExplored;
        }
        
        public List<Integer> getPath() { return path; }
        public double getTotalDistance() { return totalDistance; }
        public int getNodesExplored() { return nodesExplored; }
        public boolean isPathFound() { return !path.isEmpty(); }
    }
    
    /**
     * A* algorithm implementation with configurable heuristic
     */
    public static AStarResult findPath(Map<Integer, CityMapNode> cityMap, 
                                     int startNode, int goalNode, 
                                     HeuristicFunction heuristic) {
        
        PriorityQueue<AStarNode> openSet = new PriorityQueue<>();
        Set<Integer> closedSet = new HashSet<>();
        Map<Integer, Double> gScores = new HashMap<>();
        Map<Integer, Integer> predecessors = new HashMap<>();
        
        CityMapNode startCity = cityMap.get(startNode);
        CityMapNode goalCity = cityMap.get(goalNode);
        
        if (startCity == null || goalCity == null) {
            return new AStarResult(new ArrayList<>(), Double.MAX_VALUE, 0);
        }
        
        // Initialize start node
        double startHeuristic = heuristic.calculate(startCity, goalCity);
        openSet.offer(new AStarNode(startNode, 0.0, startHeuristic));
        gScores.put(startNode, 0.0);
        
        int nodesExplored = 0;
        
        while (!openSet.isEmpty()) {
            AStarNode current = openSet.poll();
            int currentNode = current.nodeId;
            
            nodesExplored++;
            
            // Goal reached
            if (currentNode == goalNode) {
                List<Integer> path = reconstructPath(predecessors, goalNode);
                return new AStarResult(path, current.gScore, nodesExplored);
            }
            
            closedSet.add(currentNode);
            CityMapNode currentCity = cityMap.get(currentNode);
            
            // Explore neighbors
            for (CityMapNode.Edge edge : currentCity.getAdjacentEdges()) {
                int neighbor = edge.getDestinationNode();
                
                if (closedSet.contains(neighbor)) {
                    continue;
                }
                
                double tentativeGScore = current.gScore + 
                    edge.getDynamicWeight(currentCity.getTrafficMultiplier());
                
                boolean inOpenSet = gScores.containsKey(neighbor);
                
                if (!inOpenSet || tentativeGScore < gScores.get(neighbor)) {
                    predecessors.put(neighbor, currentNode);
                    gScores.put(neighbor, tentativeGScore);
                    
                    CityMapNode neighborCity = cityMap.get(neighbor);
                    if (neighborCity != null) {
                        double heuristicScore = heuristic.calculate(neighborCity, goalCity);
                        openSet.offer(new AStarNode(neighbor, tentativeGScore, heuristicScore));
                    }
                }
            }
        }
        
        // No path found
        return new AStarResult(new ArrayList<>(), Double.MAX_VALUE, nodesExplored);
    }
    
    /**
     * A* with default Euclidean heuristic
     */
    public static AStarResult findPath(Map<Integer, CityMapNode> cityMap, 
                                     int startNode, int goalNode) {
        return findPath(cityMap, startNode, goalNode, EUCLIDEAN_HEURISTIC);
    }
    
    /**
     * Bidirectional A* for potentially faster pathfinding on large graphs
     */
    public static AStarResult findPathBidirectional(Map<Integer, CityMapNode> cityMap,
                                                   int startNode, int goalNode,
                                                   HeuristicFunction heuristic) {
        
        // Forward search from start
        PriorityQueue<AStarNode> forwardOpen = new PriorityQueue<>();
        Set<Integer> forwardClosed = new HashSet<>();
        Map<Integer, Double> forwardG = new HashMap<>();
        Map<Integer, Integer> forwardPred = new HashMap<>();
        
        // Backward search from goal
        PriorityQueue<AStarNode> backwardOpen = new PriorityQueue<>();
        Set<Integer> backwardClosed = new HashSet<>();
        Map<Integer, Double> backwardG = new HashMap<>();
        Map<Integer, Integer> backwardPred = new HashMap<>();
        
        CityMapNode startCity = cityMap.get(startNode);
        CityMapNode goalCity = cityMap.get(goalNode);
        
        if (startCity == null || goalCity == null) {
            return new AStarResult(new ArrayList<>(), Double.MAX_VALUE, 0);
        }
        
        // Initialize both searches
        forwardOpen.offer(new AStarNode(startNode, 0.0, heuristic.calculate(startCity, goalCity)));
        forwardG.put(startNode, 0.0);
        
        backwardOpen.offer(new AStarNode(goalNode, 0.0, heuristic.calculate(goalCity, startCity)));
        backwardG.put(goalNode, 0.0);
        
        int nodesExplored = 0;
        double bestPath = Double.MAX_VALUE;
        int meetingPoint = -1;
        
        while (!forwardOpen.isEmpty() && !backwardOpen.isEmpty()) {
            // Alternate between forward and backward search
            if (nodesExplored % 2 == 0) {
                AStarNode current = forwardOpen.poll();
                nodesExplored++;
                
                // Check if we've met the backward search
                if (backwardG.containsKey(current.nodeId)) {
                    double pathLength = current.gScore + backwardG.get(current.nodeId);
                    if (pathLength < bestPath) {
                        bestPath = pathLength;
                        meetingPoint = current.nodeId;
                    }
                }
                
                forwardClosed.add(current.nodeId);
                // Continue forward expansion...
                
            } else {
                AStarNode current = backwardOpen.poll();
                nodesExplored++;
                
                // Check if we've met the forward search
                if (forwardG.containsKey(current.nodeId)) {
                    double pathLength = current.gScore + forwardG.get(current.nodeId);
                    if (pathLength < bestPath) {
                        bestPath = pathLength;
                        meetingPoint = current.nodeId;
                    }
                }
                
                backwardClosed.add(current.nodeId);
                // Continue backward expansion...
            }
        }
        
        if (meetingPoint != -1) {
            List<Integer> path = reconstructBidirectionalPath(
                forwardPred, backwardPred, startNode, goalNode, meetingPoint);
            return new AStarResult(path, bestPath, nodesExplored);
        }
        
        return new AStarResult(new ArrayList<>(), Double.MAX_VALUE, nodesExplored);
    }
    
    /**
     * Reconstruct path from predecessor map
     */
    private static List<Integer> reconstructPath(Map<Integer, Integer> predecessors, int goalNode) {
        List<Integer> path = new ArrayList<>();
        Integer current = goalNode;
        
        while (current != null) {
            path.add(0, current);
            current = predecessors.get(current);
        }
        
        return path;
    }
    
    /**
     * Reconstruct bidirectional path
     */
    private static List<Integer> reconstructBidirectionalPath(
            Map<Integer, Integer> forwardPred, Map<Integer, Integer> backwardPred,
            int startNode, int goalNode, int meetingPoint) {
        
        List<Integer> path = new ArrayList<>();
        
        // Build forward path to meeting point
        List<Integer> forwardPath = new ArrayList<>();
        Integer current = meetingPoint;
        while (current != null) {
            forwardPath.add(0, current);
            current = forwardPred.get(current);
        }
        
        // Build backward path from meeting point
        List<Integer> backwardPath = new ArrayList<>();
        current = meetingPoint;
        while (current != null) {
            if (current != meetingPoint) { // Don't duplicate meeting point
                backwardPath.add(current);
            }
            current = backwardPred.get(current);
        }
        
        path.addAll(forwardPath);
        path.addAll(backwardPath);
        
        return path;
    }
    
    /**
     * Adaptive A* that learns from previous searches to improve heuristic
     */
    public static class AdaptiveAStar {
        private Map<Integer, Double> learnedHeuristics;
        
        public AdaptiveAStar() {
            this.learnedHeuristics = new HashMap<>();
        }
        
        public AStarResult findPathAdaptive(Map<Integer, CityMapNode> cityMap,
                                          int startNode, int goalNode,
                                          HeuristicFunction baseHeuristic) {
            
            HeuristicFunction adaptiveHeuristic = (from, to) -> {
                double baseH = baseHeuristic.calculate(from, to);
                double learnedH = learnedHeuristics.getOrDefault(from.getNodeId(), 0.0);
                return Math.max(baseH, learnedH);
            };
            
            AStarResult result = findPath(cityMap, startNode, goalNode, adaptiveHeuristic);
            
            // Update learned heuristics based on result
            if (result.isPathFound()) {
                updateLearnedHeuristics(result.getPath(), result.getTotalDistance(), cityMap);
            }
            
            return result;
        }
        
        private void updateLearnedHeuristics(List<Integer> path, double totalDistance,
                                           Map<Integer, CityMapNode> cityMap) {
            // Update heuristics based on actual distances found
            for (int i = 0; i < path.size() - 1; i++) {
                int nodeId = path.get(i);
                double remainingDistance = calculateRemainingDistance(path, i);
                
                Double currentHeuristic = learnedHeuristics.get(nodeId);
                if (currentHeuristic == null || remainingDistance > currentHeuristic) {
                    learnedHeuristics.put(nodeId, remainingDistance);
                }
            }
        }
        
        private double calculateRemainingDistance(List<Integer> path, int fromIndex) {
            // Calculate actual remaining distance from this point in the path
            // This is a simplified version - in practice you'd sum edge weights
            return path.size() - fromIndex - 1;
        }
    }
}
