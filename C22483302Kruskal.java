import java.io.*;
import java.util.*;

public class C22483302Kruskal {
    static class Edge implements Comparable<Edge> {
        public int src, dest, weight;

        public Edge(int src, int dest, int weight) {
            this.src = src;
            this.dest = dest;
            this.weight = weight;
        }

        @Override
        public int compareTo(Edge other) {
            return this.weight - other.weight;
        }
    }

    static class Graph {
        private int V, E;
        private Edge[] edges;
        private int[] parent;
        private int[] rank;
        private char[] vertices;

        public Graph(String graphFile) throws IOException {
            FileReader fr = new FileReader(graphFile);
            BufferedReader reader = new BufferedReader(fr);

            String splits = " +"; // multiple whitespace as delimiter
            String line = reader.readLine();
            String[] parts = line.split(splits);

            V = Integer.parseInt(parts[0]);
            E = Integer.parseInt(parts[1]);

            edges = new Edge[E];

            vertices = new char[V + 1]; // +1 for 1-based indexing
            for (int i = 1; i <= V; i++) {
                vertices[i] = (char) (64 + i); // Convert integer to corresponding letter
            }

            for (int i = 0; i < E; i++) {
                line = reader.readLine();
                parts = line.split(splits);
                int src = Integer.parseInt(parts[0]);
                int dest = Integer.parseInt(parts[1]);
                int weight = Integer.parseInt(parts[2]);
                edges[i] = new Edge(src, dest, weight);
            }

            reader.close();

            // Initialize parent and rank arrays
            parent = new int[V];
            rank = new int[V];
            for (int i = 0; i < V; i++) {
                parent[i] = i;
                rank[i] = 0;
            }
        }

        private int find(int x) {
            if (x < 0 || x >= V) {
                throw new IllegalArgumentException("Vertex index out of bounds");
            }
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        private void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);
            if (rootX == rootY) return;
            if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
            } else if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
            } else {
                parent[rootY] = rootX;
                rank[rootX]++;
            }
        }

        public void kruskalMST() {
            Arrays.sort(edges); // Sort edges based on weight
            System.out.println("\nEdges of MST using Kruskal's Algorithm:");

            int totalweight = 0; // Initialize total weight

            for (int i = 0; i < E; i++) {
                Edge edge = edges[i];
                int srcParent = find(edge.src - 1);
                int destParent = find(edge.dest - 1);
                if (srcParent != destParent) {
                    System.out.println(vertices[edge.src] + " - " + vertices[edge.dest] + " : " + edge.weight);
                    totalweight += edge.weight; // Update total weight
                    union(srcParent, destParent);
                }
            }

            // Print the total weight of the MST
            System.out.println("Total weight of MST: " + totalweight);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // Reads user's file
            System.out.print("Enter the file name: ");
            String fname = scanner.nextLine();

            Graph g = new Graph(fname);
            g.kruskalMST();
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid input data: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
