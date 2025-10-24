package hw3.exporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.princeton.cs.algorithms.Edge;
import hw3.algorithms.Kruskal;
import hw3.algorithms.Prim;
import hw3.utils.Metrics;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JSONExporter {
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
                    "mst_edges", mstEdgesToList(primM)
            ));

            entry.put("kruskal", Map.of(
                    "total_cost", kruskalM.getMstWeight(),
                    "operations_count", kruskalM.getOperationCount(),
                    "execution_time_ms", kruskalM.getExecutionTimeMs(),
                    "mst_edges", mstEdgesToList(kruskalM)
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

    private static List<Map<String, Object>> mstEdgesToList(Metrics metrics) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Edge e : metrics.getMstEdges()) {
            int v = e.either() + 1;
            int w = e.other(e.either()) + 1;
            list.add(Map.of(
                    "from", v,
                    "to", w,
                    "weight", e.weight()
            ));
        }
        return list;
    }
}
