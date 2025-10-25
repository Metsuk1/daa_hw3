package hw3.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.princeton.cs.algorithms.Edge;
import edu.princeton.cs.algorithms.EdgeWeightedGraph;
import hw3.dto.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Loads graph data from JSON files into EdgeWeightedGraph objects.
 */
public class GraphLoader {
    private static final Map<Integer, Map<Integer, String>> graphIdToIndexMap = new HashMap<>();
    /**
     * Loads multiple graphs from a JSON file.
     *
     * @param filename Name of the JSON file.
     * @return Map of graph IDs to EdgeWeightedGraph objects.
     * @throws IOException If file loading fails.
     */
    public static Map<Integer, EdgeWeightedGraph> loadMultipleGraphs(String filename) throws IOException {
        MultipleGraphData data = readJSON(filename);
        Map<Integer, EdgeWeightedGraph> graphs = new HashMap<>();

        for (GraphData graphData : data.graphs) {
            graphs.put(graphData.id, buildGraph(graphData));
        }

        return graphs;
    }

    public static EdgeWeightedGraph loadSingleGraph(String filename) throws IOException {
        MultipleGraphData data = readJSON(filename);
        if (data.graphs.length == 0) {
            throw new IllegalArgumentException("No graphs found in JSON");
        }
        return buildGraph(data.graphs[0]);
    }
    /**
     * Reads JSON data from a file.
     *
     * @param filename Name of the JSON file.
     * @return MultipleGraphData object.
     * @throws IOException If file reading fails.
     */
    private static MultipleGraphData readJSON(String filename) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        try (InputStream inputStream = GraphLoader.class.getClassLoader().getResourceAsStream(filename)) {
            if (inputStream == null) {
                throw new IOException("File not found: " + filename);
            }

            return mapper.readValue(inputStream, MultipleGraphData.class);
        }
    }

    private static EdgeWeightedGraph buildGraph(GraphData graphData) {
        Map<String, Integer> nodeToIndex = new HashMap<>();
        Map<Integer, String> indexToNode = new HashMap<>();
        for (int i = 0; i < graphData.nodes.size(); i++) {
            nodeToIndex.put(graphData.nodes.get(i), i);
            indexToNode.put(i, graphData.nodes.get(i));
        }
        graphIdToIndexMap.put(graphData.id, indexToNode); // Сохраняем маппинг для каждого графа

        EdgeWeightedGraph graph = new EdgeWeightedGraph(graphData.nodes.size());

        for (EdgeData edgeData : graphData.edges) {
            int v = nodeToIndex.get(edgeData.from);
            int w = nodeToIndex.get(edgeData.to);
            double cost = edgeData.weight;
            Edge edge = new Edge(v, w, cost);
            graph.addEdge(edge);
        }

        System.out.printf("Loaded graph #%d: %d vertices, %d edges%n", graphData.id, graph.V(), graph.E());
        return graph;
    }
    /**
     * Retrieves the index-to-node mapping for a given graph ID.
     *
     * @param graphId ID of the graph.
     * @return Map of indices to node labels.
     */
    public static Map<Integer, String> getIndexToNode(int graphId) {
        return graphIdToIndexMap.get(graphId);
    }
}
