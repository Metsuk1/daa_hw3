package hw3.algorithms;

import edu.princeton.cs.algorithms.Edge;
import edu.princeton.cs.algorithms.EdgeWeightedGraph;
import edu.princeton.cs.algorithms.Queue;
import edu.princeton.cs.algorithms.UF;
import hw3.utils.Metrics;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class Kruskal {
    private static final double FLOATING_POINT_EPSILON = 1.0E-12;

    private Metrics metrics;
    private double weight;                        // weight of MST
    private Queue<Edge> mst = new Queue<Edge>();  // edges in MST

    /**
     * Compute a minimum spanning tree (or forest) of an edge-weighted graph.
     * @param G the edge-weighted graph
     */
    public Kruskal(EdgeWeightedGraph G) {
        this.metrics = new Metrics("Kruskal", G);

        List<Edge> mstEdgesList = new ArrayList<>();

        // create array of edges, sorted by weight
        Edge[] edges = new Edge[G.E()];
        int t = 0;
        for (Edge e: G.edges()) {
            edges[t++] = e;
            metrics.countOperation(); // collecting edges
        }
        Arrays.sort(edges);
        metrics.countOperation();
        metrics.countComparison((long) (G.E() * (Math.log(G.E()) / Math.log(2))));

        // run greedy algorithm
        UF uf = new UF(G.V());
        metrics.countOperation();
        weight = 0.0;

        for (int i = 0; i < G.E() && mst.size() < G.V() - 1; i++) {
            metrics.countOperation(); // loop step
            Edge e = edges[i];
            int v = e.either();
            int w = e.other(v);
            metrics.countOperation(); // check endpoints

            metrics.countFind();
            // v-w does not create a cycle
            if (uf.find(v) != uf.find(w)) {
                metrics.countComparison();
                metrics.countUnion();
                metrics.countOperation(); // union decision
                uf.union(v, w);     // merge v and w components
                mst.enqueue(e);// add edge e to ms
                mstEdgesList.add(e);// add edge e to mst
                weight += e.weight();
            }
        }

        metrics.setResults(weight, mstEdgesList);

        // check optimality conditions
        assert check(G);
    }

    /**
     * Returns the edges in a minimum spanning tree (or forest).
     * @return the edges in a minimum spanning tree (or forest) as
     *    an iterable of edges
     */
    public Iterable<Edge> edges() {
        return mst;
    }

    /**
     * Returns the sum of the edge weights in a minimum spanning tree (or forest).
     * @return the sum of the edge weights in a minimum spanning tree (or forest)
     */
    public double weight() {
        return metrics.getMstWeight();
    }

    // check optimality conditions (takes time proportional to E V lg* V)
    private boolean check(EdgeWeightedGraph G) {

        // check total weight
        double total = 0.0;
        for (Edge e : edges()) {
            total += e.weight();
        }
        if (Math.abs(total - weight()) > FLOATING_POINT_EPSILON) {
            System.err.printf("Weight of edges does not equal weight(): %f vs. %f\n", total, weight());
            return false;
        }

        // check that it is acyclic
        UF uf = new UF(G.V());
        for (Edge e : edges()) {
            int v = e.either(), w = e.other(v);
            if (uf.find(v) == uf.find(w)) {
                System.err.println("Not a forest");
                return false;
            }
            uf.union(v, w);
        }

        // check that it is a spanning forest
        for (Edge e : G.edges()) {
            int v = e.either(), w = e.other(v);
            if (uf.find(v) != uf.find(w)) {
                System.err.println("Not a spanning forest");
                return false;
            }
        }

        // check that it is a minimal spanning forest (cut optimality conditions)
        for (Edge e : edges()) {

            // all edges in MST except e
            uf = new UF(G.V());
            for (Edge f : mst) {
                int x = f.either(), y = f.other(x);
                if (f != e) uf.union(x, y);
            }

            // check that e is min weight edge in crossing cut
            for (Edge f : G.edges()) {
                int x = f.either(), y = f.other(x);
                if (uf.find(x) != uf.find(y)) {
                    if (f.weight() < e.weight()) {
                        System.err.println("Edge " + f + " violates cut optimality conditions");
                        return false;
                    }
                }
            }

        }

        return true;
    }
}
