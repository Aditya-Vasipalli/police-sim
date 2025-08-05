import algorithms.Dijkstra;
import algorithms.AStar;
import models.CityMapNode;
import java.util.*;

public class PathfindingService {
    
    private CityMap cityMap;
    private Map<Integer, CityMapNode> nodeMap;
    private LRUCache<String, PathResult> pathCache;
    private Map<String, Long> routeFrequency;
    
    private long totalPathRequests;
    private long cacheHits;
    private long totalComputationTime;
    
    public static class LRUCache<K, V> {
        private final int maxSize;
        private final LinkedHashMap<K, V> cache;
        
        public LRUCache(int maxSize) {
            this.maxSize = maxSize;
            this.cache = new LinkedHashMap<K, V>(16, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                    return size() > maxSize;
                }
            };
        }
        
        public synchronized V get(K key) {
            return cache.get(key);
        }
        
        public synchronized void put(K key, V value) {
            cache.put(key, value);
        }
        
        public synchronized boolean containsKey(K key) {
            return cache.containsKey(key);
        }
        
        public synchronized void clear() {
            cache.clear();
        }
        
        public synchronized int size() {
            return cache.size();
        }
        
        public synchronized Set<K> keySet() {
            return new LinkedHashSet<>(cache.keySet());
        }
    }
    
    public static class PathResult {
        private List<Integer> path;
        private double distance;
        private long computationTime;
        private String algorithm;
        private int nodesExplored;
        
        public PathResult(List<Integer> path, double distance, long computationTime, 
                         String algorithm, int nodesExplored) {
            this.path = new ArrayList<>(path);
            this.distance = distance;
            this.computationTime = computationTime;
            this.algorithm = algorithm;
            this.nodesExplored = nodesExplored;
        }
        
        public List<Integer> getPath() { return new ArrayList<>(path); }
        public double getDistance() { return distance; }
        public long getComputationTime() { return computationTime; }
        public String getAlgorithm() { return algorithm; }
        public int getNodesExplored() { return nodesExplored; }
        public boolean isValidPath() { return !path.isEmpty(); }
    }
    
    public enum OptimizationStrategy {
        FASTEST_PATH,
        SHORTEST_DISTANCE,
        BALANCED,
        AVOID_TRAFFIC
    }
    
    public PathfindingService(CityMap cityMap) {
        this.cityMap = cityMap;
        this.nodeMap = new HashMap<>();
        this.pathCache = new LRUCache<>(1000);
        this.routeFrequency = new HashMap<>();
        this.totalPathRequests = 0;
        this.cacheHits = 0;
        this.totalComputationTime = 0;
        
        initializeNodeMap();
    }
    
    public PathResult calculateShortestPath(int startNode, int endNode) {
        return calculateShortestPath(startNode, endNode, OptimizationStrategy.FASTEST_PATH);
    }
    
    public PathResult calculateShortestPath(int startNode, int endNode, 
                                          OptimizationStrategy strategy) {
        totalPathRequests++;
        
        String cacheKey = generateCacheKey(startNode, endNode, strategy);
        
        PathResult cachedResult = pathCache.get(cacheKey);
        if (cachedResult != null) {
            cacheHits++;
            updateRouteFrequency(cacheKey);
            return cachedResult;
        }
        
        long startTime = System.nanoTime();
        PathResult result;
        
        if (shouldUseAStar(startNode, endNode, strategy)) {
            result = calculatePathWithAStar(startNode, endNode, strategy);
        } else {
            result = calculatePathWithDijkstra(startNode, endNode, strategy);
        }
        
        long endTime = System.nanoTime();
        totalComputationTime += (endTime - startTime);
        
        pathCache.put(cacheKey, result);
        updateRouteFrequency(cacheKey);
        
        return result;
    }
    
    public List<PathResult> calculateAlternativePaths(int startNode, int endNode, 
                                                     int numAlternatives) {
        List<PathResult> alternatives = new ArrayList<>();
        
        PathResult primaryPath = calculateShortestPath(startNode, endNode);
        alternatives.add(primaryPath);
        
        if (primaryPath.isValidPath() && numAlternatives > 1) {
            Set<String> usedEdges = new HashSet<>();
            
            List<Integer> primaryRoute = primaryPath.getPath();
            for (int i = 0; i < primaryRoute.size() - 1; i++) {
                String edge = primaryRoute.get(i) + "->" + primaryRoute.get(i + 1);
                usedEdges.add(edge);
            }
            
            for (int i = 1; i < numAlternatives; i++) {
                PathResult alternative = calculateAlternativeWithPenalty(
                    startNode, endNode, usedEdges, i * 0.5);
                
                if (alternative.isValidPath()) {
                    alternatives.add(alternative);
                    
                    List<Integer> altRoute = alternative.getPath();
                    for (int j = 0; j < altRoute.size() - 1; j++) {
                        String edge = altRoute.get(j) + "->" + altRoute.get(j + 1);
                        usedEdges.add(edge);
                    }
                }
            }
        }
        
        return alternatives;
    }
    
    public void updateTrafficConditions(Map<Integer, Double> nodeTrafficMultipliers) {
        for (Map.Entry<Integer, Double> entry : nodeTrafficMultipliers.entrySet()) {
            CityMapNode node = nodeMap.get(entry.getKey());
            if (node != null) {
                node.setTrafficMultiplier(entry.getValue());
            }
        }
        
        pathCache.clear();
    }
    
    public Map<Integer, PathResult> calculateMultiplePaths(
            Map<Integer, Integer> unitToTargetMap) {
        
        Map<Integer, PathResult> results = new HashMap<>();
        
        Map<Integer, List<Integer>> targetToUnits = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : unitToTargetMap.entrySet()) {
            targetToUnits.computeIfAbsent(entry.getValue(), k -> new ArrayList<>())
                         .add(entry.getKey());
        }
        
        for (Map.Entry<Integer, Integer> entry : unitToTargetMap.entrySet()) {
            int unitLocation = entry.getKey();
            int targetLocation = entry.getValue();
            
            PathResult result = calculateShortestPath(unitLocation, targetLocation);
            results.put(unitLocation, result);
        }
        
        return results;
    }
    
    public PathfindingStats getPerformanceStats() {
        double cacheHitRate = totalPathRequests > 0 ? 
            (double) cacheHits / totalPathRequests : 0.0;
        
        double avgComputationTime = totalPathRequests > 0 ? 
            (double) totalComputationTime / (totalPathRequests - cacheHits) : 0.0;
        
        return new PathfindingStats(
            totalPathRequests,
            cacheHits,
            cacheHitRate,
            avgComputationTime,
            pathCache.size(),
            getMostFrequentRoutes(5)
        );
    }
    
    public static class PathfindingStats {
        public final long totalRequests;
        public final long cacheHits;
        public final double cacheHitRate;
        public final double avgComputationTimeNs;
        public final int cacheSize;
        public final List<String> mostFrequentRoutes;
        
        public PathfindingStats(long totalRequests, long cacheHits, double cacheHitRate,
                               double avgComputationTimeNs, int cacheSize,
                               List<String> mostFrequentRoutes) {
            this.totalRequests = totalRequests;
            this.cacheHits = cacheHits;
            this.cacheHitRate = cacheHitRate;
            this.avgComputationTimeNs = avgComputationTimeNs;
            this.cacheSize = cacheSize;
            this.mostFrequentRoutes = mostFrequentRoutes;
        }
    }
    
    private void initializeNodeMap() {
    }
    
    private String generateCacheKey(int start, int end, OptimizationStrategy strategy) {
        return start + "->" + end + ":" + strategy.name();
    }
    
    private void updateRouteFrequency(String cacheKey) {
        routeFrequency.merge(cacheKey, 1L, Long::sum);
    }
    
    private boolean shouldUseAStar(int start, int end, OptimizationStrategy strategy) {
        CityMapNode startNode = nodeMap.get(start);
        CityMapNode endNode = nodeMap.get(end);
        
        if (startNode == null || endNode == null) return false;
        
        double euclideanDistance = startNode.getEuclideanDistance(endNode);
        
        return euclideanDistance > 10.0 || strategy == OptimizationStrategy.FASTEST_PATH;
    }
    
    private PathResult calculatePathWithDijkstra(int start, int end, 
                                               OptimizationStrategy strategy) {
        long startTime = System.nanoTime();
        
        List<Integer> path = Dijkstra.findShortestPath(nodeMap, start, end);
        
        long endTime = System.nanoTime();
        long computationTime = endTime - startTime;
        
        double distance = calculatePathDistance(path);
        
        return new PathResult(path, distance, computationTime, "Dijkstra", 0);
    }
    
    private PathResult calculatePathWithAStar(int start, int end, 
                                            OptimizationStrategy strategy) {
        long startTime = System.nanoTime();
        
        AStar.HeuristicFunction heuristic = selectHeuristic(strategy);
        AStar.AStarResult result = AStar.findPath(nodeMap, start, end, heuristic);
        
        long endTime = System.nanoTime();
        long computationTime = endTime - startTime;
        
        return new PathResult(
            result.getPath(), 
            result.getTotalDistance(), 
            computationTime, 
            "A*", 
            result.getNodesExplored()
        );
    }
    
    private AStar.HeuristicFunction selectHeuristic(OptimizationStrategy strategy) {
        switch (strategy) {
            case FASTEST_PATH:
                return AStar.EUCLIDEAN_HEURISTIC;
            case SHORTEST_DISTANCE:
                return AStar.MANHATTAN_HEURISTIC;
            case AVOID_TRAFFIC:
                return AStar.EUCLIDEAN_HEURISTIC;
            default:
                return AStar.EUCLIDEAN_HEURISTIC;
        }
    }
    
    private PathResult calculateAlternativeWithPenalty(int start, int end, 
                                                     Set<String> penalizedEdges, 
                                                     double penaltyFactor) {
        return calculateShortestPath(start, end);
    }
    
    private double calculatePathDistance(List<Integer> path) {
        if (path.size() < 2) return 0.0;
        
        double totalDistance = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            CityMapNode current = nodeMap.get(path.get(i));
            CityMapNode next = nodeMap.get(path.get(i + 1));
            
            if (current != null && next != null) {
                for (CityMapNode.Edge edge : current.getAdjacentEdges()) {
                    if (edge.getDestinationNode() == next.getNodeId()) {
                        totalDistance += edge.getDynamicWeight(current.getTrafficMultiplier());
                        break;
                    }
                }
            }
        }
        
        return totalDistance;
    }
    
    private List<String> getMostFrequentRoutes(int topN) {
        return routeFrequency.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(topN)
            .map(Map.Entry::getKey)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}
