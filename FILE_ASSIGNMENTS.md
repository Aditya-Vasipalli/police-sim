# Police Dispatch Simulator - File Assignments

Assignment of each file to team members:

## **Aaysha - City Map & Graph Infrastructure**
**Primary Focus**: Graph Management

### Core Files:
- `src/CityMap.java` - Main module with Floyd-Warshall implementation
- `src/PathfindingPanel.java` - GUI component for path visualization
- `src/gui/GraphVisualizer.java` - Graph visualization
- `src/algorithms/FloydWarshall.java` - Algorithm implementation

### Supporting Files:
- `src/models/CityMapNode.java` - Graph node representation
- `src/database/CityMapDAO.java` - City map data access
- `src/gui/MapEditorGUI.java` - Map editing interface

---

## **Anushri - Police Management & Unit Tracking**
**Primary Focus**: Greedy Assignment Heuristics, Unit Management

### Core Files:
- `src/PoliceManager.java` - Main module for police unit management
- `src/PoliceAssignmentPanel.java` - GUI component for assignment display

### Supporting Files:
- `src/models/Unit.java` - Police unit model
- `src/models/Assignment.java` - Assignment tracking model
- `src/database/UnitDAO.java` - Unit data access
- `src/database/AssignmentDAO.java` - Assignment data access
- `src/gui/UnitTablePanel.java` - Unit table display
- `src/services/UnitBalancerService.java` - Unit balancing logic

---

## **Param - Crime Generation & Event Management**
**Primary Focus**: Priority Queue with Binary Heap, Crime Management

### Core Files:
- `src/CrimeGenerator.java` - Main module with priority queue implementation
- `src/CrimeFeedPanel.java` - GUI component for crime feed

### Supporting Files:
- `src/models/Crime.java` - Crime event model
- `src/database/CrimeDAO.java` - Crime data access

---

## **Kaivalya - Pathfinding Service & Route Optimization**
**Primary Focus**: Dijkstra's Algorithm with Min-Heap, A* Algorithm

### Core Files:
- `src/PathfindingService.java` - Main module with Dijkstra implementation
- `src/algorithms/Dijkstra.java` - Dijkstra algorithm implementation
- `src/algorithms/AStar.java` - A* algorithm implementation
- `src/Reporting.java` - Additional module with sliding window algorithms

### Supporting Files:
- `src/models/Stat.java` - Statistics model for reporting
- `src/database/StatDAO.java` - Statistics data access
- `src/gui/PerformanceGraphPanel.java` - Performance visualization

---

## **Aditya - Simulation Core & Dispatch Logic**
**Primary Focus**: Hungarian Algorithm, Simulation Orchestration

### Core Files:
- `src/SimulatorCore.java` - Main simulation orchestration
- `src/algorithms/HungarianAlgorithm.java` - Hungarian algorithm implementation
- `src/Main.java` - Application entry point


### Supporting Files:
- `src/services/CrimeAssignmentService.java` - Advanced dispatch logic
- `src/gui/AdminControlPanel.java` - Admin interface


---

## **Shared/Common Files**
These files may require collaboration or can be assigned based on team availability:

- `src/reports/` - Directory for report outputs (all team members, kaivalya manages)
- `src/simulation/` - Additional simulation components (Aditya's oversight)

---

## **Implementation Priority**

### Phase 1 (Foundation):
1. **Aaysha**: `CityMap.java`, `CityMapNode.java`
2. **Anushri**: `PoliceManager.java`, `Unit.java`
3. **Param**: `CrimeGenerator.java`, `Crime.java`
4. **Kaivalya**: `PathfindingService.java`, `Dijkstra.java`
5. **Aditya**: `SimulatorCore.java`, `Main.java`

### Phase 2 (Algorithms):
1. **Aaysha**: Complete Floyd-Warshall integration
2. **Param**: Priority queue implementation in `CrimeGenerator.java`
3. **Kaivalya**: `AStar.java`, `Reporting.java`
4. **Aditya**: `HungarianAlgorithm.java`

### Phase 3 (GUI & Services):
1. All team members work on their respective GUI components
2. Database access objects (DAO files)
3. Service layer implementations

---

## **Coordination Points**

- **Interface Design**: All team members should coordinate on method signatures between modules
- **Data Models**: Shared models in `/models` directory need consistent structure
- **GUI Integration**: GUI components should follow consistent design patterns
- **Testing**: Each member responsible for unit tests of their primary algorithms

---

*Note: This assignment ensures each team member has a clear primary algorithm focus while maintaining balanced workload distribution across the codebase.*
