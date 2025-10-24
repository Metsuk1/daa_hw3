import edu.princeton.cs.algorithms.EdgeWeightedGraph;
import hw3.algorithms.Kruskal;
import hw3.algorithms.Prim;
import hw3.exporter.CSVExporter;
import hw3.exporter.JSONExporter;
import hw3.loader.GraphLoader;
import hw3.printer.ResultsPrinter;
import hw3.solver.MSTSolver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CityTransportationMST {
    private static final String[] DATASETS = {
            "assign_3_input_small.json",
            "assign_3_input_medium.json",
            "assign_3_input_large.json"
    };

    public static void main(String[] args) {
        try {
            showWelcomeScreen();
            String choice = getUserChoice(args);
            processChoice(choice);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void showWelcomeScreen() {
        System.out.println("""
           Assignment 3 - DAA
           CITY TRANSPORTATION MST SOLVER
           Available datasets:
           Option   │   Dataset    │  Vertices     │
            1       │   Small      │   4-6         │
            2       │  Medium      │  10-15        │
            3       │   Large      │  20-30+       │
            4       │   ALL        │  all of these │
            """);
    }

    private static String getUserChoice(String[] args) {
        if (args.length > 0) {
            String arg = args[0].toLowerCase();
            return switch (arg) {
                case "1", "small" -> DATASETS[0];
                case "2", "medium" -> DATASETS[1];
                case "3", "large" -> DATASETS[2];
                case "4", "all" -> "ALL";
                default -> DATASETS[0]; // by default it's - small
            };
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("\nSelect dataset (1-4): ");
            String input = scanner.nextLine().trim();

            return switch (input.toLowerCase()) {
                case "1", "small" -> DATASETS[0];
                case "2", "medium" -> DATASETS[1];
                case "3", "large" -> DATASETS[2];
                case "4", "all" -> "ALL";
                default -> {
                    System.out.println("Invalid choice! Try 1-4");
                    yield null;
                }
            };
        }
    }

    private static void processChoice(String choice) throws IOException {
        if ("ALL".equals(choice)) {
            processAllDatasets();
        } else {
            processSingleDataset(choice);
        }
    }

    private static void processAllDatasets() throws IOException {
        System.out.println("\nRunning ALL datasets for comparison...");
        System.out.println("=".repeat(80));

        Map<String, Map<Integer, Prim>> allPrim = new HashMap<>();
        Map<String, Map<Integer, Kruskal>> allKruskal = new HashMap<>();
        Map<String, Map<Integer, EdgeWeightedGraph>> allGraphs = new HashMap<>();

        for (String dataset : DATASETS) {
            var result = processDataset(dataset);
            allPrim.put(dataset, result.primMSTs);
            allKruskal.put(dataset, result.kruskalMSTs);
            allGraphs.put(dataset, result.graphs);
        }

        printOverallComparison(allPrim, allKruskal);
        CSVExporter.exportSummary(allPrim,allKruskal,"summary_report.csv");
    }

    private static void processSingleDataset(String filename) throws IOException {
        System.out.println("\nProcessing: " + filename);
        System.out.println("=".repeat(80));

        DatasetResult result = processDataset(filename);
        CSVExporter.exportMultiple(result.primMSTs, result.kruskalMSTs,
                "results_" + filename.replace(".json", ".csv"), filename);

        new ResultsPrinter(result.graphs, result.primMSTs, result.kruskalMSTs).print();
        JSONExporter.export("results.json", result.primMSTs, result.kruskalMSTs);
    }

    private static DatasetResult processDataset(String filename) throws IOException {
        Map<Integer, EdgeWeightedGraph> graphs = GraphLoader.loadMultipleGraphs(filename);

        Map<Integer, Prim> primMSTs = new HashMap<>();
        Map<Integer, Kruskal> kruskalMSTs = new HashMap<>();

        for (int graphId : graphs.keySet().stream().sorted().toList()) {
            EdgeWeightedGraph graph = graphs.get(graphId);
            MSTSolver solver = new MSTSolver(graph);
            primMSTs.put(graphId, solver.getPrimMST());
            kruskalMSTs.put(graphId, solver.getKruskalMST());
        }

        return new DatasetResult(graphs, primMSTs, kruskalMSTs);
    }

    private static void printOverallComparison(Map<String, Map<Integer, Prim>> allPrim,
                                               Map<String, Map<Integer, Kruskal>> allKruskal) {
        System.out.println("\n OVERALL PERFORMANCE COMPARISON");
        System.out.println("=".repeat(80));
        System.out.printf("%-12s%-8s%-10s%-10s%-12s%-12s%n",
                "Dataset", "V", "E", "Prim(ms)", "Kruskal(ms)", "Winner");
        System.out.println("-".repeat(80));

        for (String dataset : DATASETS) {
            Prim prim = allPrim.get(dataset).values().iterator().next();
            Kruskal kruskal = allKruskal.get(dataset).values().iterator().next();

            String winner = prim.getMetrics().getExecutionTimeMs() < kruskal.getMetrics().getExecutionTimeMs()
                    ? "Prim" : "Kruskal";

            System.out.printf("%-12s%-8s%-10s%-10.2f%-12.2f%s%n",
                    dataset,
                    "?", "?",
                    prim.getMetrics().getGraphV(),
                    prim.getMetrics().getGraphE(),
                    prim.getMetrics().getExecutionTimeMs(),
                    kruskal.getMetrics().getExecutionTimeMs(),
                    winner);
        }
    }


    //helper class
    private record DatasetResult(Map<Integer, EdgeWeightedGraph> graphs,
                                 Map<Integer, Prim> primMSTs,
                                 Map<Integer, Kruskal> kruskalMSTs) {}
}
