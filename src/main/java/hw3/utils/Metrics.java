package hw3.utils;

import edu.princeton.cs.algorithms.Edge;
import edu.princeton.cs.algorithms.EdgeWeightedGraph;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Metrics {
    private long operationCount = 0;
    private long startTime = 0;
    private final String algorithmName;
    private final double mstWeight;
    private final List<Edge> mstEdges;
    private final EdgeWeightedGraph graph;

    public Metrics(String algorithmName, EdgeWeightedGraph graph, double mstWeight, List<Edge> mstEdges) {
        this.algorithmName = algorithmName;
        this.graph = graph;
        this.mstWeight = mstWeight;
        this.mstEdges = mstEdges;
        this.startTime = System.nanoTime();
    }

    public void countOperation() {
        operationCount++;
    }

    public long getOperationCount() {
        return operationCount;
    }

    public long getExecutionTimeMs() {
        return (System.nanoTime() - startTime) / 1_000_000;
    }

    public double getMstWeight() {
        return mstWeight;
    }

    public List<Edge> getMstEdges() {
        return mstEdges;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public int getGraphV() {
        return graph.V();
    }

    public int getGraphE() {
        return graph.E();
    }

    /**
     * Записывает результаты в CSV файл
     */
    public void exportToCSV(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            writer.printf("%s,%s,%d,%d,%d,%.5f,%.5f%n",
                    timestamp,
                    algorithmName,
                    getGraphV(),
                    getGraphE(),
                    getOperationCount(),
                    getExecutionTimeMs(),
                    getMstWeight()
            );
        }
    }

    /**
     * Создает CSV заголовок если файл пустой
     */
    public static void createCSVHeader(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, false))) {
            writer.println("timestamp,algorithm,vertices,edges,operations,time_ms,mst_weight");
        }
    }

    @Override
    public String toString() {
        return String.format("%s: %.5f (%.0f ops, %.0f ms)",
                algorithmName, mstWeight, (double)operationCount, (double)getExecutionTimeMs());
    }
}
