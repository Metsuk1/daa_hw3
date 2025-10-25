package hw3.solver;

import edu.princeton.cs.algorithms.EdgeWeightedGraph;
import hw3.algorithms.Kruskal;
import hw3.algorithms.Prim;
/**
 * Solves the MST problem using both Prim and Kruskal algorithms.
 */
public class MSTSolver {
    private final Prim primMST;
    private final Kruskal kruskalMST;

    public MSTSolver(EdgeWeightedGraph graph) {
        this.primMST = new Prim(graph);
        this.kruskalMST = new Kruskal(graph);
    }

    public Prim getPrimMST() { return primMST; }
    public Kruskal getKruskalMST() { return kruskalMST; }
}
