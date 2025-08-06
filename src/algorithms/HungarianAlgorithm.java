package algorithms;

import java.util.*;

/**
 * Hungarian Algorithm for optimal assignment of police units to crimes
 * Minimizes total response time/cost when assigning multiple units to multiple crimes
 */
public class HungarianAlgorithm {
    
    public static class Assignment {
        private int unitId;
        private int crimeId;
        private double cost;
        
        public Assignment(int unitId, int crimeId, double cost) {
            this.unitId = unitId;
            this.crimeId = crimeId;
            this.cost = cost;
        }
        
        public int getUnitId() { return unitId; }
        public int getCrimeId() { return crimeId; }
        public double getCost() { return cost; }
        
        @Override
        public String toString() {
            return String.format("Unit %d -> Crime %d (cost: %.2f)", unitId, crimeId, cost);
        }
    }
    
    /**
     * Solves the assignment problem using Hungarian Algorithm
     * @param costMatrix [units][crimes] - cost of assigning unit i to crime j
     * @param unitIds Array of unit IDs corresponding to rows
     * @param crimeIds Array of crime IDs corresponding to columns
     * @return List of optimal assignments
     */
    public static List<Assignment> solve(double[][] costMatrix, int[] unitIds, int[] crimeIds) {
        if (costMatrix == null || costMatrix.length == 0 || costMatrix[0].length == 0) {
            return new ArrayList<>();
        }
        
        int numUnits = costMatrix.length;
        int numCrimes = costMatrix[0].length;
        
        // Make the matrix square by padding with high cost values
        int maxDimension = Math.max(numUnits, numCrimes);
        double[][] squareMatrix = new double[maxDimension][maxDimension];
        double maxCost = getMaxCost(costMatrix) * 2; // High cost for dummy assignments
        
        // Fill the square matrix
        for (int i = 0; i < maxDimension; i++) {
            for (int j = 0; j < maxDimension; j++) {
                if (i < numUnits && j < numCrimes) {
                    squareMatrix[i][j] = costMatrix[i][j];
                } else {
                    squareMatrix[i][j] = maxCost; // Dummy assignment cost
                }
            }
        }
        
        // Apply Hungarian algorithm
        int[] assignment = hungarianAlgorithm(squareMatrix);
        
        // Convert result to Assignment objects (excluding dummy assignments)
        List<Assignment> result = new ArrayList<>();
        for (int i = 0; i < numUnits; i++) {
            int crimeIndex = assignment[i];
            if (crimeIndex >= 0 && crimeIndex < numCrimes && costMatrix[i][crimeIndex] < maxCost) {
                result.add(new Assignment(unitIds[i], crimeIds[crimeIndex], costMatrix[i][crimeIndex]));
            }
        }
        
        return result;
    }
    
    /**
     * Core Hungarian algorithm implementation
     */
    private static int[] hungarianAlgorithm(double[][] costMatrix) {
        int n = costMatrix.length;
        double[][] matrix = new double[n][n];
        
        // Copy matrix
        for (int i = 0; i < n; i++) {
            System.arraycopy(costMatrix[i], 0, matrix[i], 0, n);
        }
        
        // Step 1: Subtract row minima
        for (int i = 0; i < n; i++) {
            double rowMin = Arrays.stream(matrix[i]).min().orElse(0);
            for (int j = 0; j < n; j++) {
                matrix[i][j] -= rowMin;
            }
        }
        
        // Step 2: Subtract column minima
        for (int j = 0; j < n; j++) {
            double colMin = Double.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                colMin = Math.min(colMin, matrix[i][j]);
            }
            for (int i = 0; i < n; i++) {
                matrix[i][j] -= colMin;
            }
        }
        
        // Step 3: Find optimal assignment using augmenting path method
        return findOptimalAssignment(matrix);
    }
    
    /**
     * Find optimal assignment using augmenting path method
     */
    private static int[] findOptimalAssignment(double[][] matrix) {
        int n = matrix.length;
        int[] assignment = new int[n];
        Arrays.fill(assignment, -1);
        
        boolean[] rowCovered = new boolean[n];
        boolean[] colCovered = new boolean[n];
        
        // Try to find initial assignments using zeros
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == 0 && !rowCovered[i] && !colCovered[j]) {
                    assignment[i] = j;
                    rowCovered[i] = true;
                    colCovered[j] = true;
                    break;
                }
            }
        }
        
        // If not all rows are assigned, use more sophisticated matching
        for (int i = 0; i < n; i++) {
            if (assignment[i] == -1) {
                // Find augmenting path for unassigned row
                boolean[] visited = new boolean[n];
                findAugmentingPath(matrix, i, assignment, visited);
            }
        }
        
        return assignment;
    }
    
    /**
     * Find augmenting path for better assignment
     */
    private static boolean findAugmentingPath(double[][] matrix, int row, int[] assignment, boolean[] visited) {
        int n = matrix.length;
        
        for (int col = 0; col < n; col++) {
            if (matrix[row][col] == 0 && !visited[col]) {
                visited[col] = true;
                
                // If column is unassigned or we can find augmenting path for assigned row
                int assignedRow = -1;
                for (int r = 0; r < n; r++) {
                    if (assignment[r] == col) {
                        assignedRow = r;
                        break;
                    }
                }
                
                if (assignedRow == -1 || findAugmentingPath(matrix, assignedRow, assignment, visited)) {
                    assignment[row] = col;
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Helper method to find maximum cost in matrix
     */
    private static double getMaxCost(double[][] matrix) {
        double max = 0;
        for (double[] row : matrix) {
            for (double cost : row) {
                max = Math.max(max, cost);
            }
        }
        return max;
    }
    
    /**
     * Calculate total cost of an assignment
     */
    public static double calculateTotalCost(List<Assignment> assignments) {
        return assignments.stream().mapToDouble(Assignment::getCost).sum();
    }
}
