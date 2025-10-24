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

public class GraphLoader {
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
        Map<String, Integer> indexMap = new HashMap<>();
        for (int i = 0; i < graphData.nodes.size(); i++) {
            indexMap.put(graphData.nodes.get(i), i);
        }

        EdgeWeightedGraph graph = new EdgeWeightedGraph(graphData.nodes.size());

        for (EdgeData edgeData : graphData.edges) {
            int v = indexMap.get(edgeData.from);
            int w = indexMap.get(edgeData.to);
            double cost = edgeData.weight;
            Edge edge = new Edge(v, w, cost);
            graph.addEdge(edge);
        }

        return graph;
    }
}
