package hw3.exporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.princeton.cs.algorithms.Edge;
import hw3.algorithms.Kruskal;
import hw3.algorithms.Prim;
import hw3.loader.GraphLoader;
import hw3.utils.Metrics;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Exports MST results for Prim and Kruskal algorithms to a JSON file.
 */
public class JSONExporter {
    /**
     * Exports MST metrics and edge lists to a JSON file.
     *
     * @param filename    Name of the output JSON file.
     * @param primMSTs    Map of graph IDs to Prim MST objects.
     * @param kruskalMSTs Map of graph IDs to Kruskal MST objects.
     */
    public static void export(String filename,
                              Map<Integer, Prim> primMSTs,
                              Map<Integer, Kruskal> kruskalMSTs) {

        List<Object> resultList = new ArrayList<>();

        for (int graphId : primMSTs.keySet().stream().sorted().toList()) {
            Metrics primM = primMSTs.get(graphId).getMetrics();
            Metrics kruskalM = kruskalMSTs.get(graphId).getMetrics();

            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("graph_id", graphId);

            entry.put("input_stats", Map.of(
                    "vertices", primM.getGraphV(),
                    "edges", primM.getGraphE()
            ));

            entry.put("prim", Map.of(
                    "total_cost", primM.getMstWeight(),
                    "operations_count", primM.getOperationCount(),
                    "execution_time_ms", primM.getExecutionTimeMs(),
                    "mst_edges", mstEdgesToList(primM, graphId)
            ));

            entry.put("kruskal", Map.of(
                    "total_cost", kruskalM.getMstWeight(),
                    "operations_count", kruskalM.getOperationCount(),
                    "execution_time_ms", kruskalM.getExecutionTimeMs(),
                    "mst_edges", mstEdgesToList(kruskalM, graphId)
            ));

            resultList.add(entry);
        }

        Map<String, Object> output = Map.of("results", resultList);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(new File(filename), output);
            System.out.println("\nJSON report exported → " + filename);
        } catch (Exception e) {
            System.err.println("Failed to export JSON: " + e.getMessage());
        }
    }
    /**
     * Converts MST edges to a list of maps with node labels and weights.
     *
     * @param metrics Metrics object containing MST edges.
     * @param graphId ID of the graph.
     * @return List of edge data maps.
     */
    private static List<Map<String, Object>> mstEdgesToList(Metrics metrics, int graphId) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<Integer, String> indexToNode = GraphLoader.getIndexToNode(graphId);
        if (indexToNode == null) {
            System.err.println("Warning: No indexToNode mapping for graphId " + graphId);
            return list;
        }

        for (Edge e : metrics.getMstEdges()) {
            int v = e.either();
            int w = e.other(e.either());
            String from = indexToNode.get(v);
            String to = indexToNode.get(w);
            if (from == null || to == null) {
                System.err.println("Warning: No mapping for vertex index " + v + " or " + w + " in graphId " + graphId);
                continue;
            }
            list.add(Map.of(
                    "from", from,
                    "to", to,
                    "weight", e.weight()
            ));
        }
        return list;
    }
}