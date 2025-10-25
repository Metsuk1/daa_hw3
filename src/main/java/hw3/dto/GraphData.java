package hw3.dto;

import java.util.List;

/**
 * Represents a single graph with nodes and edges loaded from JSON.
 */
public class GraphData {
    public int id;
    public List<String> nodes;
    public List<EdgeData> edges;
}
