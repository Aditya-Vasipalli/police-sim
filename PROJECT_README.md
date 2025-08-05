# Police Response Time Simulator

A comprehensive simulation system for analyzing police response times and dispatch optimization using various algorithms and data structures.

## Project Structure

```
src/
├── algorithms/          # Core algorithms
│   ├── Dijkstra.java          # Shortest path algorithm
│   ├── AStar.java             # A* pathfinding
│   ├── HungarianAlgorithm.java # Optimal assignment
│   └── FloydWarshall.java     # All-pairs shortest paths
├── database/            # Data access objects
│   ├── CrimeDAO.java
│   ├── UnitDAO.java
│   ├── AssignmentDAO.java
│   ├── CityMapDAO.java
│   └── StatDAO.java
├── models/              # Data models
│   ├── Crime.java
│   ├── Unit.java
│   ├── Assignment.java
│   ├── CityMapNode.java
│   └── Stat.java
├── services/            # Business logic
│   ├── CrimeAssignmentService.java
│   └── UnitBalancerService.java
├── gui/                 # User interface
│   ├── AdminControlPanel.java
│   ├── GraphVisualizer.java
│   ├── UnitTablePanel.java
│   ├── PerformanceGraphPanel.java
│   └── MapEditorGUI.java
├── simulation/          # Simulation components
├── reports/             # Report generation
├── Main.java           # Entry point
├── SimulatorCore.java  # Central simulation engine
├── CityMap.java        # City graph management
├── CrimeGenerator.java # Crime event generation
├── PathfindingService.java # Pathfinding service
├── PoliceManager.java  # Police unit management (stub)
└── Reporting.java      # Statistics and reporting
```

## Key Features Implemented

### Core Algorithms (DSA Focus)
1. **Hungarian Algorithm** - Optimal assignment of police units to crimes
2. **Dijkstra's Algorithm** - Shortest path finding with caching
3. **A* Algorithm** - Heuristic-based pathfinding
4. **Floyd-Warshall** - All-pairs shortest paths (precomputed)

### Simulation Engine
- Time-based simulation with configurable tick intervals
- Real-time crime generation with priority levels
- Optimal dispatch using multiple assignment strategies
- Comprehensive event logging and statistics

### Interactive Controls
- Start/Stop/Pause/Resume simulation
- Generate test crimes for demonstration
- Real-time statistics monitoring
- Emergency stop functionality

## How to Run

### Prerequisites
- Java 8 or higher
- All Java files are in the `src/` directory

### Compilation
```bash
# Navigate to project directory
cd "c:\Aditya\college\sy sem1\FODS\Project"

# Compile all Java files
javac -cp . src/*.java src/**/*.java

# Or compile main class (will compile dependencies)
javac -cp . src/Main.java
```

### Execution
```bash
# Run the main simulation
java -cp src Main

# Or run test setup to create sample map file
javac TestSetup.java
java TestSetup
```

## Current Implementation Status

### ✅ Completed (Ready to Use)
- **SimulatorCore.java** - Full simulation engine with tick-based processing
- **HungarianAlgorithm.java** - Complete optimal assignment implementation
- **CrimeAssignmentService.java** - Advanced assignment logic with Hungarian + greedy fallback
- **Main.java** - Interactive console interface with full menu system
- **AdminControlPanel.java** - GUI control panel (needs import fixes)
- **Models** - Complete Crime, Unit, Assignment POJOs with getters/setters
- **CityMap integration** - Works with Aaysha's graph implementation
- **CrimeGenerator integration** - Works with Param's crime generation
- **PathfindingService integration** - Works with Kaivalya's pathfinding

### 🚧 Stubbed (Waiting for Team)
- **PoliceManager.java** - Set to null, simulation handles gracefully
- **GUI Components** - Need import path fixes when integrated

### 🎯 Key Integration Points
1. **PoliceManager Interface Needed:**
   ```java
   List<Unit> getAvailableUnits()
   List<Unit> getDispatchedUnits() 
   void dispatchUnit(int unitId, int crimeId)
   void updateAllUnits()
   List<Unit> getArrivedUnits()
   List<Assignment> getCompletedAssignments()
   void returnUnitToService(int unitId)
   ```

2. **Database Integration** - DAOs are stubbed, can be implemented later

## Usage Examples

### Console Mode
```
=== SIMULATION CONTROL MENU ===
1. Start Simulation
2. Stop Simulation  
5. Show Statistics
6. Generate Test Crimes
9. Test Pathfinding
0. Exit
```

### Sample Output
```
[Tick 1] New crime generated: ROBBERY (HIGH) at location 3
[Tick 1] Hungarian assignment: 2 units assigned to 2 crimes (total cost: 8.50)
[Tick 1] Dispatched Unit 101 to Crime 5 (ETA: 4.2 minutes)
[Tick 10] Crime 5 resolved by Unit 101
```

## Team Integration Notes

### For Anushri (PoliceManager):
- Implement the interface methods listed above
- Unit status should be: "AVAILABLE", "DISPATCHED", "RETURNING"
- SimulatorCore will call your methods during each tick

### For Integration Testing:
1. Run `TestSetup.java` to create sample map file
2. Run `Main.java` and try option 6 (Generate Test Crimes)
3. Try option 9 (Test Pathfinding) to verify Kaivalya's module
4. Use option 1 to start simulation with crime generation

## Algorithm Details

### Hungarian Algorithm Usage
- Triggers when multiple units AND crimes exist
- Builds cost matrix using pathfinding + priority weighting
- Returns globally optimal assignments
- Fallback to greedy for single unit/crime scenarios

### Assignment Cost Calculation
```
cost = responseTime / priorityWeight
where priorityWeight: CRITICAL=4.0, HIGH=3.0, MEDIUM=2.0, LOW=1.0
```

### Performance Optimizations
- LRU cache for pathfinding results
- Precomputed station-to-station distances
- Efficient Hungarian implementation with O(n³) complexity

## Troubleshooting

### Common Issues
1. **Import errors in GUI** - Classes not in packages yet, will resolve with proper compilation
2. **PoliceManager null errors** - Expected, simulation handles gracefully
3. **File not found errors** - Run TestSetup.java first to create city_map.csv

### Debug Mode
- All events logged to console with tick timestamps
- Statistics updated every 10 ticks
- Full event log available through menu option 7

---

**Project Team:** Aditya (Simulation Core), Kaivalya (Pathfinding), Param (Crime Generation), Anushri (Police Management), Aaysha (City Map)
