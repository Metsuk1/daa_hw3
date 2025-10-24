package hw3.printer;

import edu.princeton.cs.algorithms.EdgeWeightedGraph;
import hw3.algorithms.Kruskal;
import hw3.algorithms.Prim;
import hw3.utils.Metrics;

import java.util.Map;

public class ResultsPrinter {
    private final Map<Integer, EdgeWeightedGraph> graphs;
    private final Map<Integer, Prim> primMSTs;
    private final Map<Integer, Kruskal> kruskalMSTs;

    public ResultsPrinter(Map<Integer, EdgeWeightedGraph> graphs,
                          Map<Integer, Prim> primMSTs,
                          Map<Integer, Kruskal> kruskalMSTs) {
        this.graphs = graphs;
        this.primMSTs = primMSTs;
        this.kruskalMSTs = kruskalMSTs;
    }

    public void print() {
        System.out.println("\nDETAILED MST RESULTS");
        System.out.println("=".repeat(60));

        for (int graphId : graphs.keySet().stream().sorted().toList()) {
            Prim prim = primMSTs.get(graphId);
            Kruskal kruskal = kruskalMSTs.get(graphId);

            System.out.printf("\nGraph ID: %d%n", graphId);
            System.out.printf("Vertices: %d | Edges: %d%n", graphs.get(graphId).V(), graphs.get(graphId).E());
            System.out.println("-".repeat(60));

            System.out.println("Prim MST:");
            printMST(prim);
            System.out.println();

            System.out.println("Kruskal MST:");
            printMST(kruskal);
            System.out.println();

            printComparison(prim.getMetrics(), kruskal.getMetrics());
            System.out.println("=".repeat(60));
        }
    }

    private void printMST(Object mstObj) {
        Metrics metrics = (mstObj instanceof Prim)
                ? ((Prim) mstObj).getMetrics()
                : ((Kruskal) mstObj).getMetrics();

        System.out.printf("  Total cost: %.2f%n", metrics.getMstWeight());
        System.out.printf("  Operations: %d%n", metrics.getOperationCount());
        System.out.printf("  Execution time: %d ms%n", metrics.getExecutionTimeMs());
        System.out.println("  Edges:");

        metrics.getMstEdges().forEach(e -> {
            int v = e.either() + 1;          // convert raw index → district #
            int w = e.other(e.either()) + 1;
            System.out.printf("    District %d — District %d (cost %.2f)%n", v, w, e.weight());
        });
    }

    private void printComparison(Metrics prim, Metrics kruskal) {
        System.out.println("\nComparison:");
        System.out.println("----------");
        System.out.printf("Cost Match: %s%n",
                Math.abs(prim.getMstWeight() - kruskal.getMstWeight()) < 1e-12 ? "YES" : "NO");

        System.out.printf("Faster: %s (%d ms vs %d ms)%n",
                prim.getExecutionTimeMs() < kruskal.getExecutionTimeMs() ? "Prim" : "Kruskal",
                prim.getExecutionTimeMs(),
                kruskal.getExecutionTimeMs()
        );
    }
}
