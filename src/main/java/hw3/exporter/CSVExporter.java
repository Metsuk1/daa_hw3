package hw3.exporter;

import hw3.algorithms.Kruskal;
import hw3.algorithms.Prim;
import hw3.utils.Metrics;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * CSV Exporter for MST results with full dataset support
 */
public class CSVExporter {

    /**
     *  Export results for MULTIPLE graphs in ONE dataset (NEW)
     */
    public static void exportMultiple(Map<Integer, Prim> primMSTs,
                                      Map<Integer, Kruskal> kruskalMSTs,
                                      String filename, String datasetName) throws IOException {

        createCSVHeader(filename);

        for (int graphId : primMSTs.keySet().stream().sorted().toList()) {
            Metrics primMetrics = primMSTs.get(graphId).getMetrics();
            Metrics kruskalMetrics = kruskalMSTs.get(graphId).getMetrics();

            // Добавляем информацию о датасете и графе
            primMetrics.exportToCSVWithGraph(filename, datasetName, graphId);
            kruskalMetrics.exportToCSVWithGraph(filename, datasetName, graphId);
        }
    }

    /**
     * create  CSV header
     */
    public static void createCSVHeader(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, false))) {
            writer.println("timestamp,algorithm,graph_id,dataset,vertices,edges,comparisons,finds,unions,operations,time_ms,mst_weight");
        }
    }

    /**
     * Summary report - comprasion Prim vs Kruskal
     */
    public static void exportSummary(Map<String, Map<Integer, Prim>> allPrim,
                                     Map<String, Map<Integer, Kruskal>> allKruskal,
                                     String filename) throws IOException {

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, false))) {
            writer.println("dataset,graph_id,vertices,edges," +
                    "prim_time_ms,kruskal_time_ms,prim_ops,kruskal_ops," +
                    "prim_cost,kruskal_cost,winner");

            for (Map.Entry<String, Map<Integer, Prim>> datasetEntry : allPrim.entrySet()) {
                String dataset = datasetEntry.getKey();
                Map<Integer, Prim> primGraphs = datasetEntry.getValue();
                Map<Integer, Kruskal> kruskalGraphs = allKruskal.get(dataset);

                for (int graphId : primGraphs.keySet()) {
                    Metrics prim = primGraphs.get(graphId).getMetrics();
                    Metrics kruskal = kruskalGraphs.get(graphId).getMetrics();

                    String winner = prim.getExecutionTimeMs() < kruskal.getExecutionTimeMs()
                            ? "Prim" : "Kruskal";

                    writer.printf("%s,%d,%d,%d,%.2f,%.2f,%d,%d,%.2f,%.2f,%s%n",
                            dataset, graphId,
                            prim.getGraphV(), prim.getGraphE(),
                            prim.getExecutionTimeMs(), kruskal.getExecutionTimeMs(),
                            prim.getOperationCount(), kruskal.getOperationCount(),
                            prim.getMstWeight(), kruskal.getMstWeight(),
                            winner);
                }
            }
        }

        System.out.printf("Summary report → %s%n", filename);
    }
}
