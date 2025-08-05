# Police Response Time Simulation - Pathfinding Service & Route Optimization

## 🚨 Project Overview
This project implements a comprehensive **Police Response Time Simulation** focusing on **Pathfinding Service & Route Optimization** using advanced Data Structures and Algorithms (DSA).

## 🎯 Core DSA Components Implemented

### 1. **Dijkstra's Algorithm with Custom Min-Heap** 
- **File**: `src/algorithms/Dijkstra.java`
- **Features**:
  - Custom min-heap implementation optimized for pathfinding
  - Single-source shortest path computation
  - Optimized decrease-key operations with O(log n) complexity
  - Support for dynamic edge reweighting (traffic conditions)

### 2. **A* Algorithm with Multiple Heuristics**
- **File**: `src/algorithms/AStar.java`
- **Features**:
  - Euclidean, Manhattan, and Zero heuristics
  - Bidirectional A* for large graphs
  - Adaptive A* that learns from previous searches
  - Performance comparison with different heuristic functions

### 3. **LRU Cache for Path Storage**
- **File**: `src/PathfindingService.java` (LRUCache class)
- **Features**:
  - Least Recently Used eviction policy
  - O(1) access and insertion using LinkedHashMap
  - Cache hit rate monitoring
  - Configurable capacity (default: 1000 paths)

### 4. **Sliding Window Algorithm for Real-time Analytics**
- **File**: `src/Reporting.java` (SlidingWindowStats class)
- **Features**:
  - Real-time response time tracking
  - Automatic expiration of old data
  - Running statistics (mean, standard deviation)
  - Crime type breakdown analysis

### 5. **Prefix Sum Arrays for Fast Statistical Queries**
- **File**: `src/Reporting.java` (PrefixSumStats class)
- **Features**:
  - O(1) range sum queries
  - O(1) range average calculations
  - O(1) range variance computations
  - Best performance window detection

### 6. **Quick Sort for Performance Ranking**
- **File**: `src/Reporting.java` (PerformanceRanking class)
- **Features**:
  - Custom comparator for multi-criteria sorting
  - Median-of-three pivot selection
  - Top-N performer selection
  - Performance scoring algorithms

## 📁 Project Structure

```
src/
├── algorithms/
│   ├── Dijkstra.java          # Custom min-heap + Dijkstra implementation
│   └── AStar.java              # A* with multiple heuristics
├── models/
│   ├── CityMapNode.java        # Graph node with coordinates and edges
│   └── Stat.java               # Statistics and performance data model
├── PathfindingService.java     # Main service with LRU cache
├── Reporting.java              # Analytics with sliding window & prefix sums
├── CityMap.java                # Graph management and loading
├── PathfindingDemo.java        # Comprehensive demonstration
└── ComponentTest.java          # Individual component verification
```

## 🔧 Key Features

### Dynamic Traffic Management
- Real-time traffic multiplier updates
- Path recalculation with updated conditions
- Cache invalidation on traffic changes

### Multi-Algorithm Support
- Automatic algorithm selection based on distance and strategy
- Performance comparison between Dijkstra and A*
- Alternative path generation for load balancing

### Advanced Analytics
- **Sliding Window**: 60-minute rolling statistics
- **Prefix Sums**: Fast historical data analysis
- **Performance Ranking**: Quick sort with custom criteria
- **Real-time Dashboard**: Live metrics and KPIs

### Route Optimization Strategies
- `FASTEST_PATH`: Minimize response time
- `SHORTEST_DISTANCE`: Minimize travel distance
- `BALANCED`: Balance time vs distance
- `AVOID_TRAFFIC`: Prefer less congested routes

## 🚀 Running the System

### 1. Compile All Components
```bash
javac -cp src src/models/*.java src/algorithms/*.java src/*.java
```

### 2. Run Complete Demonstration
```bash
java -cp src PathfindingDemo
```

### 3. Run Individual Component Tests
```bash
java -cp src ComponentTest
```

## 📊 Performance Metrics

### Cache Performance
- **Hit Rate**: 37.5% (from demo)
- **Average Computation**: 0.313 ms
- **Memory Usage**: Configurable LRU capacity

### Algorithm Comparison
- **Dijkstra**: Optimal for single-source all destinations
- **A* Euclidean**: 5 nodes explored vs 16 for Dijkstra-like
- **A* Adaptive**: Learning improves performance over iterations

### Real-time Analytics
- **Sliding Window**: Live 60-minute statistics
- **Response Time**: 4.24 min average (from simulation)
- **Resolution Rate**: 80% success rate
- **Crime Type Analysis**: Domestic (2.10 min) to Traffic (6.16 min)

## 🎯 DSA Concepts Demonstrated

1. **Graph Algorithms**: Dijkstra's and A* pathfinding
2. **Heap Operations**: Custom min-heap with decrease-key
3. **Caching Strategies**: LRU cache implementation
4. **Window Algorithms**: Sliding window for real-time data
5. **Array Techniques**: Prefix sums for fast queries
6. **Sorting Algorithms**: Quick sort with custom comparators
7. **Graph Traversal**: BFS for connectivity checking
8. **Heuristic Search**: Multiple heuristic functions in A*

## 🏆 Achievements

- ✅ **Custom Min-Heap**: Optimized for pathfinding with O(log n) operations
- ✅ **LRU Cache**: 37.5% hit rate improving response times
- ✅ **Sliding Window**: Real-time analytics with automatic data expiration
- ✅ **Prefix Sums**: O(1) statistical queries on historical data
- ✅ **Quick Sort**: Performance ranking with multi-criteria sorting
- ✅ **Dynamic Updates**: Traffic-aware pathfinding with cache management
- ✅ **Multiple Algorithms**: Dijkstra vs A* with performance comparison
- ✅ **Comprehensive Testing**: All components verified individually

## 🔄 System Workflow

1. **City Map Loading**: Graph initialization from CSV data
2. **Pathfinding Service**: Algorithm selection and caching
3. **Crime Response**: Simulation of police unit dispatch
4. **Real-time Tracking**: Sliding window analytics
5. **Performance Analysis**: Prefix sum computations
6. **Ranking Generation**: Quick sort for top performers
7. **Report Generation**: Comprehensive performance reports

## 📈 Scalability Features

- **Memory Efficient**: LRU cache prevents unbounded growth
- **Time Efficient**: O(1) statistical queries with prefix sums
- **Real-time Ready**: Sliding window for live data processing
- **Adaptive**: Learning algorithms improve over time
- **Configurable**: Flexible parameters for different scenarios

This implementation demonstrates advanced DSA concepts in a practical police response simulation, showcasing optimization techniques, real-time analytics, and performance monitoring suitable for large-scale emergency response systems.
