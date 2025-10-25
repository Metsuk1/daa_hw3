# City Transportation MST Solver

This project implements and analyzes two classical Minimum Spanning Tree (MST) algorithms — **Prim’s algorithm** and **Kruskal’s algorithm** — applied to the optimization of a city transportation network. The objective is to determine the minimum-cost set of roads needed to connect all city districts.

The project loads transportation graph data from JSON, constructs MSTs using both algorithms, gathers execution metrics, and exports results in JSON and CSV formats. A performance comparison is then conducted to evaluate efficiency differences between the two algorithms.

---

## **Project Overview**

The project is written in **Java**  and  The MST analysis includes:

- Total MST cost
- Execution time (ms)
- Number of operations
- Comparisons (where applicable)
- Union-Find operations (for Kruskal)
- List of edges selected in the MST

The program supports dataset selection through a simple CLI interface:            
Small (4–6 vertices)            
Medium (10–15 vertices)          
Large (20–30+ vertices)        
All datasets        

## **Dataset Example (Medium Input)**

The medium dataset contains **12 districts(vertex) (A–L)** and **16 possible roads(edges)**, each with a construction cost:
it's the input

```json
{
  "graphs": [
    {
      "id": 1,
      "nodes": ["A","B","C","D","E","F","G","H","I","J","K","L"],
      "edges": [
        {"from":"A","to":"B","weight":2.1}, {"from":"A","to":"C","weight":3.2},
        {"from":"B","to":"D","weight":1.8}, {"from":"B","to":"E","weight":4.5},
        {"from":"C","to":"F","weight":2.9}, {"from":"D","to":"G","weight":3.7},
        {"from":"E","to":"H","weight":2.3}, {"from":"F","to":"I","weight":4.1},
        {"from":"G","to":"J","weight":1.9}, {"from":"H","to":"K","weight":3.4},
        {"from":"I","to":"L","weight":2.7}, {"from":"J","to":"K","weight":2.2},
        {"from":"K","to":"L","weight":3.8}, {"from":"A","to":"L","weight":10.0},
        {"from":"E","to":"I","weight":5.6}, {"from":"G","to":"L","weight":4.2}
      ]
    }
  ]
}

JSON Output Example
{
  "results": [
    {
      "graph_id": 1,
      "input_stats": {
        "vertices": 12,
        "edges": 16
      },
      "prim": {
        "operations_count": 181,
        "execution_time_ms": 8,
        "total_cost": 30.0,
        "mst_edges": [
          {"weight": 2.1, "to": "B", "from": "A"},
          {"weight": 3.2, "to": "C", "from": "A"},
          {"weight": 1.8, "to": "D", "from": "B"},
          {"weight": 2.3, "to": "H", "from": "E"},
          {"weight": 2.9, "to": "F", "from": "C"},
          {"weight": 3.7, "to": "G", "from": "D"},
          {"weight": 3.4, "to": "K", "from": "H"},
          {"weight": 2.7, "to": "L", "from": "I"},
          {"weight": 1.9, "to": "J", "from": "G"},
          {"weight": 2.2, "to": "K", "from": "J"},
          {"weight": 3.8, "to": "L", "from": "K"}
        ]
      },
      "kruskal": {
        "operations_count": 51,
        "execution_time_ms": 6,
        "total_cost": 30.0,
        "mst_edges": [
          {"weight": 1.8, "to": "D", "from": "B"},
          {"weight": 1.9, "to": "J", "from": "G"},
          {"weight": 2.1, "to": "B", "from": "A"},
          {"weight": 2.2, "to": "K", "from": "J"},
          {"weight": 2.3, "to": "H", "from": "E"},
          {"weight": 2.7, "to": "L", "from": "I"},
          {"weight": 2.9, "to": "F", "from": "C"},
          {"weight": 3.2, "to": "C", "from": "A"},
          {"weight": 3.4, "to": "K", "from": "H"},
          {"weight": 3.7, "to": "G", "from": "D"},
          {"weight": 3.8, "to": "L", "from": "K"}
        ]
      }
    }
  ]
}
```

       
## Comparison Between Prim’s and Kruskal’s Algorithms

### Efficiency and Performance Metrics

### Prim’s Algorithm
- **Operations:** 181  
- **Comparisons:** 31  
- **Finds / Unions:** 0 / 0  
- **Execution Time:** 2 ms (CSV) / 8 ms (JSON)  
- **Approach:** Uses a **priority queue** to grow the MST starting from an initial vertex.
- The relatively high operation count is due to frequent **priority queue updates** and **adjacency list scans**.
- **Time Complexity:** \( O(E \log V) \)

### Kruskal’s Algorithm
- **Operations:** 51  
- **Comparisons:** 75  
- **Finds / Unions:** 11 / 11  
- **Execution Time:** 2 ms (CSV) / 6 ms (JSON)  
- **Approach:** Sorts all edges, then uses **Union-Find** to avoid cycles.
- More comparisons occur because sorting dominates computation.
- **Time Complexity:** \( O(E \log E) \), typically ≈ \( O(E \log V) \)

## Results Summary

| Algorithm | MST Cost | Operations | Comparisons | Finds | Unions | Execution Time (ms) |
|----------|---------:|-----------:|------------:|------:|-------:|--------------------:|
| **Prim** | 30.0     | 181        | 31          | 0     | 0      | 2 ms                |
| **Kruskal** | 30.0  | 51         | 75          | 11    | 11     | 2 ms                |              

this result you can see in the file .csv         
Execution times recorded in the CSV report correspond to the core MST computation only.
JSON output times are slightly higher because they also include result formatting, object construction, and serialization overhead.
Therefore, the CSV values should be used for performance comparison, while JSON times should be interpreted as full pipeline runtime.



### Performance Visualization

The chart below compares the number of operations performed by Prim’s and Kruskal’s algorithms on Graph 1 of the medium dataset:

| Algorithm | Operations |
|----------|------------|
| Prim     | 181        |
| Kruskal  | 51         |

This difference is clearly shown in the plot:
<img width="450" height="459" alt="изображение" src="https://github.com/user-attachments/assets/faae83da-ba90-4f90-8cd6-8afca8a0252a" />


Prim performs significantly more operations due to repeated updates in its priority queue, while Kruskal benefits from efficient Union-Find structure on a relatively sparse graph.

## Which Algorithm Is Better?

| Graph Type               | Recommended Algorithm | Reason                                           |
|-------------------------|----------------------|--------------------------------------------------|
| **Sparse Graphs**       | **Kruskal**          | Sorting dominates; Union-Find is efficient       |
| **Dense Graphs**        | **Prim**             | Priority queue operations scale better           |
| **Adjacency Matrix Storage** | **Prim**        | Fast access to neighboring vertices              |
| **Edge List Storage**   | **Kruskal**          | Sorting edges is natural and efficient            



## Conclusions

### Algorithm Preference Under Different Conditions

#### Graph Density
- **Sparse Graphs (few edges relative to vertices):**  
  **Kruskal** is preferable due to its complexity of **O(E log E)**, where sorting is manageable and Union-Find operations are efficient.  
  Example: For the medium dataset (12 vertices, 16 edges), Kruskal required **51 operations**, compared to **181** for Prim.

- **Dense Graphs (many edges):**  
  **Prim** may be more effective with its **O(E log V)** performance, avoiding sorting overhead and efficiently managing edge exploration via a priority queue.  
  This advantage becomes more noticeable as the number of edges approaches \( V^2 \).

---

### Edge Representation
- **Adjacency List (Prim):**  
  Ideal for Prim’s algorithm, as it allows fast access to adjacent edges and minimizes traversal cost. Best suited for vertex-connectivity-focused graph structures.

- **Edge List (Kruskal):**  
  Fits Kruskal’s approach, which relies on an already-formed edge list for sorting. Efficient when edge data is readily available.

---

### Implementation Complexity
- **Prim’s Algorithm:**  
  Requires maintaining a *priority queue*, adding moderate complexity. Cycle prevention is simple via a marked (visited) array.

- **Kruskal’s Algorithm:**  
  Needs *sorting* and *Union-Find*. While optimization (path compression, union by rank) increases implementation effort, the overall workflow is clean: **sort → connect edges**.

---

### Execution Environment
- For **small to medium graphs** (e.g., the 12-vertex example), both algorithms execute in about **2 ms**, but Kruskal’s **lower operation count** indicates *better resource utilization*.
- For **large-scale or time-sensitive** systems:
  - Prim offers predictable tree growth.
  - Kruskal benefits from *parallelizable sorting* and decoupled processing.

---

### Recommendation
- Choose **Kruskal** when working with **sparse graphs** or **edge-list representations**.  
  As seen in the project dataset, it achieved **better efficiency (51 operations)** with equal execution time.
- Choose **Prim** when working with **dense graphs** or **adjacency-list-based systems**, where its priority queue management minimizes redundant checks.


### References

- Sedgewick, R., & Wayne, K. (2011). *Algorithms* (4th ed.). Addison-Wesley.

- **“Difference between Prim’s and Kruskal’s algorithm for MST.”**  
  *GeeksforGeeks* — статья о сравнении подходов, структурах данных и сложности.  
  Автор: Arghadip Chakraborty, 12 Jul 2025.  
  [link to the article](https://www.geeksforgeeks.org/difference-between-prims-and-kruskals-algorithm-for-mst/)

