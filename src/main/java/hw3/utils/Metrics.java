package hw3.utils;

import edu.princeton.cs.algorithms.Edge;
import edu.princeton.cs.algorithms.EdgeWeightedGraph;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
/**
 * Tracks performance metrics for MST algorithms.
 */
public class Metrics {
    private long operationCount = 0;
    private long startTime = 0;
    private long comparisons = 0;
    private long finds = 0;
    private long unions = 0;
    private final String algorithmName;
    private  double mstWeight;
    private  List<Edge> mstEdges;
    private  EdgeWeightedGraph graph;

    /**
     * Constructs a Metrics object for a given algorithm and graph.
     *
     * @param algorithmName Name of the algorithm.
     * @param graph         Graph being processed.
     */
    public Metrics(String algorithmName, EdgeWeightedGraph graph) {
        this.algorithmName = algorithmName;
        this.graph = graph;
        this.startTime = System.nanoTime();
    }
    /**
     * Sets the MST results.
     *
     * @param mstWeight Total weight of the MST.
     * @param mstEdges  List of edges in the MST.
     */
    public void setResults(double mstWeight, List<Edge> mstEdges) {
        this.mstWeight = mstWeight;
        this.mstEdges = mstEdges;
    }


    public void countOperation() { operationCount++; }
    public void countComparison() { comparisons++; }
    public void countComparison(long n) { comparisons += n; }
    public void countFind() { finds++; }
    public void countUnion() { unions++; }


    public long getOperationCount() { return operationCount; }
    public long getComparisons() { return comparisons; }
    public long getFinds() { return finds; }
    public long getUnions() { return unions; }

    public double getMstWeight() { return mstWeight; }
    public List<Edge> getMstEdges() { return mstEdges; }
    public String getAlgorithmName() { return algorithmName; }
    public int getGraphV() { return graph.V(); }
    public int getGraphE() { return graph.E(); }
    public long getExecutionTimeMs() { return (System.nanoTime() - startTime) / 1_000_000; }

    // CSV EXPORT
    public static void createCSVHeader(String filename) throws IOException {
        try (PrintWriter w = new PrintWriter(new FileWriter(filename, false))) {
            w.println("timestamp,algorithm,graph_id,dataset,vertices,edges,comparisons,finds,unions,operations,time_ms,mst_weight");
        }
    }
/**
 * Exports metrics to a CSV file with graph details.
 */
    public void exportToCSVWithGraph(String filename, String datasetName, int graphId) throws IOException {
        try (var writer = new PrintWriter(new FileWriter(filename, true))) {
            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            writer.printf("%s,%s,%d,%s,%d,%d,%d,%d,%d,%d,%d,%.2f%n",
                    ts, algorithmName, graphId, datasetName,
                    getGraphV(), getGraphE(),
                    comparisons, finds, unions, operationCount,
                    getExecutionTimeMs(), mstWeight
            );
        }
    }
    @Override
    public String toString() {
        return String.format("%s: %.5f (%.0f ops, %.0f ms)",
                algorithmName, mstWeight, (double)operationCount, (double)getExecutionTimeMs());
    }
}
