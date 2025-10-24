package hw3.algorithms;

import edu.princeton.cs.algorithms.*;
import hw3.utils.Metrics;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Prim {
    private static final double FLOATING_POINT_EPSILON = 1.0E-12;

    private Metrics metrics;
    private Edge[] edgeTo;        // edgeTo[v] = shortest edge from tree vertex to non-tree vertex
    private double[] distTo;      // distTo[v] = weight of shortest such edge
    private boolean[] marked;     // marked[v] = true if v on tree, false otherwise
    private IndexMinPQ<Double> pq;

    /**
     * Compute a minimum spanning tree (or forest) of an edge-weighted graph.
     * @param G the edge-weighted graph
     */
    public Prim(EdgeWeightedGraph G) {
        this.metrics = new Metrics("Prim", G);

        edgeTo = new Edge[G.V()];
        distTo = new double[G.V()];
        marked = new boolean[G.V()];
        pq = new IndexMinPQ<Double>(G.V());

        for (int v = 0; v < G.V(); v++) {
            distTo[v] = Double.POSITIVE_INFINITY;
            metrics.countOperation(); // initialization
        }
        for (int v = 0; v < G.V(); v++)      // run from each vertex to find
            if (!marked[v]){      // minimum spanning forest
                metrics.countOperation(); // component check
                prim(G, v);
        }

        // Calculate final weight and edges for metrics
        double weight = 0.0;
        List<Edge> mstEdges = new ArrayList<>();
        for (int v = 0; v < edgeTo.length; v++) {
            Edge e = edgeTo[v];
            if (e != null) {
                mstEdges.add(e);
                weight += e.weight();
            }
        }
        metrics.setResults(weight, mstEdges);

        // check optimality conditions
        assert check(G);
    }

    // run Prim's algorithm in graph G, starting from vertex s
    private void prim(EdgeWeightedGraph G, int s) {
        metrics.countOperation(); // prim call
        distTo[s] = 0.0;
        pq.insert(s, distTo[s]);
        metrics.countOperation(); // insert operation

        while (!pq.isEmpty()) {
            metrics.countOperation(); // pq check
            int v = pq.delMin();
            metrics.countOperation(); // delete min
            scan(G, v);
        }
    }

    // scan vertex v
    private void scan(EdgeWeightedGraph G, int v) {
        metrics.countOperation(); // scan call
        marked[v] = true;

        for (edu.princeton.cs.algorithms.Edge e : G.adj(v)) {
            metrics.countOperation(); // edge iteration
            int w = e.other(v);
            metrics.countOperation(); // other call

            if (marked[w]){
                metrics.countOperation(); // marked check
                metrics.countComparison();
                continue;
            }

            metrics.countOperation(); // weight comparison
            if (e.weight() < distTo[w]) {
                metrics.countComparison();
                distTo[w] = e.weight();
                edgeTo[w] = e;
                metrics.countOperation(); // assignment

                if (pq.contains(w)) {
                    metrics.countOperation(); // contains check
                    pq.decreaseKey(w, distTo[w]);
                    metrics.countOperation(); // decreaseKey
                } else {
                    pq.insert(w, distTo[w]);
                    metrics.countOperation(); // insert
                }
            }
        }
    }


    /**
     * Returns the edges in a minimum spanning tree (or forest).
     * @return the edges in a minimum spanning tree (or forest) as
     *    an iterable of edges
     */
    public Iterable<Edge> edges() {
        Queue<Edge> mst = new Queue<Edge>();
        for (int v = 0; v < edgeTo.length; v++) {
            Edge e = edgeTo[v];
            if (e != null) {
                mst.enqueue(e);
            }
        }
        return mst;
    }

    /**
     * Returns the sum of the edge weights in a minimum spanning tree (or forest).
     * @return the sum of the edge weights in a minimum spanning tree (or forest)
     */
    public double weight() {
        double weight = 0.0;
        for (Edge e : edges())
            weight += e.weight();
        return weight;
    }


    // check optimality conditions (takes time proportional to E V lg* V)
    private boolean check(EdgeWeightedGraph G) {

        // check weight
        double totalWeight = 0.0;
        for (Edge e : edges()) {
            totalWeight += e.weight();
        }
        if (Math.abs(totalWeight - weight()) > FLOATING_POINT_EPSILON) {
            System.err.printf("Weight of edges does not equal weight(): %f vs. %f\n", totalWeight, weight());
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
            for (Edge f : edges()) {
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
