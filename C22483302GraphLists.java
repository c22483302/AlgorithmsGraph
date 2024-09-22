// Simple weighted graph representation 
// Uses an Adjacency Linked Lists, suitable for sparse graphs

import java.io.*;
import java.util.Scanner;
import java.util.Queue;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Arrays;

class Heap
{
    private int[] a;	   // heap array
    private int[] hPos;	   // hPos[h[k]] == k
    private int[] dist;    // dist[v] = priority of v

    private int N;         // heap size
   
    // The heap constructor gets passed from the Graph:
    //    1. maximum heap size
    //    2. reference to the dist[] array
    //    3. reference to the hPos[] array
    public Heap(int maxSize, int[] _dist, int[] _hPos) 
    {
        N = 0;
        a = new int[maxSize + 1];
        dist = _dist;
        hPos = _hPos;
    }


    public boolean isEmpty() 
    {
        return N == 0;
    }


    public void siftUp( int k) 
    {
        int v = a[k];
        a[0] = 0;  // consider 0 as a kind of dummy heap value
        dist[0] = Integer.MAX_VALUE; // smaller dist means higher priority

        while (k > 1 && dist[v] < dist[a[k/2]])
        {
            a[k] = a[k/2];
            hPos[a[k]] = k;
            k = k/2;
        }
        a[k] = v;
        hPos[v] = k;
    }


    public void siftDown( int k) 
    {
        int v, j;
        v = a[k];
        j = 2*k;

        while (j <= N)
        {
            if(j < N && dist[a[j]] > dist[a[j + 1]])
            {
                ++j;
            }
            if (dist[v] <= dist[a[j]]) 
            {
                break;
            }
            a[k] = a[j];
            hPos[a[k]] = k;
            k = j;
            j *= 2;
        }
        a[k] = v;
        hPos[v] = k;
    }


    public void insert( int x) 
    {
        a[++N] = x;
        siftUp( N);
    }


    public int remove() 
    {   
        int v = a[1];
        hPos[v] = 0; // v is no longer in heap
        a[N+1] = 0;  // put null node into empty spot
        
        a[1] = a[N--];
        siftDown(1);
        
        return v;
    }

}

class Graph 
{
    class Node 
    {
        public int vert;
        public int wgt;
        public Node next;
    }
    
    // V = number of vertices
    // E = number of edges
    // adj[] is the adjacency lists array
    private int V, E;
    private Node[] adj;
    private Node z;
    private int[] mst;
    
    // used for traversing graph
    private int[] visited;
    private int id;
    
    
    // default constructor
    public Graph(String graphFile)  throws IOException
    {
        int u, v;
        int e, wgt;

        FileReader fr = new FileReader(graphFile);
		BufferedReader reader = new BufferedReader(fr);
	           
        String splits = " +";  // multiple whitespace as delimiter
		String line = reader.readLine();        
        String[] parts = line.split(splits);
        System.out.println("Parts[] = " + parts[0] + " " + parts[1]);
        
        V = Integer.parseInt(parts[0]);
        E = Integer.parseInt(parts[1]);
        
        // create sentinel node
        z = new Node(); 
        z.next = z;
        
        // create adjacency lists, initialised to sentinel node z       
        adj = new Node[V+1];        
        for(v = 1; v <= V; ++v)
            adj[v] = z;               
        
       // read the edges
        System.out.println("Reading edges from text file");
        for(e = 1; e <= E; ++e)
        {
            line = reader.readLine();
            parts = line.split(splits);
            u = Integer.parseInt(parts[0]);
            v = Integer.parseInt(parts[1]); 
            wgt = Integer.parseInt(parts[2]);
            
            System.out.println("Edge " + toChar(u) + "--(" + wgt + ")--" + toChar(v));   

           
            
            // write code to put edge into adjacency matrix 
            Node newNode1 = new Node();
            newNode1.vert = v;
            newNode1.wgt = wgt;
            newNode1.next = adj[u];
            adj[u] = newNode1;
        
            Node newNode2 = new Node();
            newNode2.vert = u;
            newNode2.wgt = wgt;
            newNode2.next = adj[v];
            adj[v] = newNode2;   
        }	       
    }
   
    // convert vertex into char for pretty printing
    private char toChar(int u)
    {  
        return (char)(u + 64);
    }
    
    // method to display the graph representation
    public void display() 
    {
        int v;
        Node n;
        
        for(v=1; v<=V; ++v)
        {
            System.out.print("\nadj[" + toChar(v) + "] ->" );
            for(n = adj[v]; n != z; n = n.next) 
                System.out.print(" |" + toChar(n.vert) + " | " + n.wgt + "| ->");    
        }
        System.out.println("");
    }
    
    public void MST_Prim(int s)
    {
        int[] dist = new int[V + 1];
        int[] parent = new int[V + 1];
        boolean[] inMST = new boolean[V + 1];
        mst = new int[V + 1]; // Initialize mst array

        // Initialize distances to infinity
        for (int v = 1; v <= V; v++)
        {
            dist[v] = Integer.MAX_VALUE;
            inMST[v] = false;    
        }

        dist[s] = 0;
        parent[s] = 0;

        // Priority queue to store vertices based on their distances
        PriorityQueue<Integer> pq = new PriorityQueue<>((v1, v2) -> dist[v1] - dist[v2]);
        pq.offer(s);

        int mstSize = 0;
        int wgt_sum = 0;

        while (!pq.isEmpty() && mstSize < V) 
        {
            int v = pq.poll(); // Get the vertex with minimum distance from the priority queue
            if (inMST[v]) continue; // Skip if the vertex is already in MST
            inMST[v] = true;

            // Output step-by-step details
            System.out.println("Step " + (mstSize + 1) + ": Selected vertex " + toChar(v) + " (Distance: " + dist[v] + ")");
            System.out.println("Heap contents: " + Arrays.toString(pq.toArray()));
            System.out.println("Parent array: " + Arrays.toString(parent));
            System.out.println("Distance array: " + Arrays.toString(dist));

            // Update MST size and total weight
            mstSize++;
            wgt_sum += dist[v];

            // Traverse the adjacency list of the selected vertex
            for (Node t = adj[v]; t != z; t = t.next) 
            {
                int u = t.vert;
                int wgt = t.wgt;

                // If vertex u is not yet in MST and its distance can be updated
                if (!inMST[u] && wgt < dist[u]) 
                {
                    dist[u] = wgt; // Update the distance
                    parent[u] = v; // Update the parent
                    pq.offer(u); // Add u to the priority queue
                }
            }
        }

        System.out.print("\n\nWeight of MST = " + wgt_sum + "\n");
        showMST(parent);
    }

    
    public void showMST(int[] parent)
    {
        System.out.print("\n\nMinimum Spanning tree parent array is:\n");
        for(int v = 1; v <= V; ++v)
            System.out.println(toChar(v) + " -> " + toChar(parent[v]));
        System.out.println("");
    }

    public void SPT_Dijkstra(int s)
    {
        int[] dist = new int[V + 1];
        int[] parent = new int[V + 1];
        boolean[] visited = new boolean[V + 1];

        // Initialize distances to infinity
        for (int v = 1; v <= V; v++)
        {
            dist[v] = Integer.MAX_VALUE;
            parent[v] = -1; // Initialize parent array
            visited[v] = false;
        }

        dist[s] = 0;

        // Priority queue to store vertices based on their distances
        PriorityQueue<Integer> pq = new PriorityQueue<>((v1, v2) -> dist[v1] - dist[v2]);
        pq.offer(s);
        System.out.println("Steps: ");

        while (!pq.isEmpty())
        {
            int u = pq.poll(); // Get the vertex with minimum distance from the priority queue
            visited[u] = true;

            // Output step-by-step details
            System.out.println("Selected vertex " + toChar(u) + " (Distance: " + dist[u] + ")");
            System.out.println("Heap contents: " + Arrays.toString(pq.toArray()));
            System.out.println("Parent array: " + Arrays.toString(parent));
            System.out.println("Distance array: " + Arrays.toString(dist));

            // Traverse the adjacency list of the selected vertex
            for (Node t = adj[u]; t != z; t = t.next) 
            {
                int v = t.vert;
                int wgt = t.wgt;

                // If vertex v is not yet visited and its distance can be updated
                if (!visited[v] && dist[u] != Integer.MAX_VALUE && dist[u] + wgt < dist[v]) 
                {
                    // Output details for updating distance
                    System.out.println("  Updating distance to vertex " + toChar(v) + " from " + dist[v] + " to " + (dist[u] + wgt));

                    dist[v] = dist[u] + wgt; // Update the distance
                    parent[v] = u; // Update the parent
                    pq.offer(v); // Add v to the priority queue
                }
            }
        }

        // Display the shortest paths from the source vertex to all other vertices
        System.out.println("\nShortest Paths from vertex " + toChar(s) + " using Dijkstra's Algorithm:");
        for (int v = 1; v <= V; v++)
        {
            if (dist[v] == Integer.MAX_VALUE)
            {
                System.out.println(toChar(s) + " -> " + toChar(v) + ": No path exists");
            }
            else
            {
                System.out.print(toChar(s) + " -> " + toChar(v) + ": " + dist[v] + "  Path: ");
                // Display the path by backtracking through parent array
                int p = v;
                StringBuilder path = new StringBuilder();
                while (parent[p] != -1)
                {
                    path.insert(0, " -> " + toChar(p));
                    p = parent[p];
                }
                System.out.println(toChar(p) + path.toString());
            }
        }
    }

    public void depthFirstSearch(int s) 
    {
        visited = new int[V + 1];
        System.out.println("\nDepth First Search starting from vertex " + toChar(s) + ": ");
        dfs(s);
        System.out.println("");
    }
    
    private void dfs(int u) 
    {
        visited[u] = 1; // Mark the current vertex as visited
        System.out.print(toChar(u) + " "); // Print the current vertex
    
        // Visit all adjacent vertices of the current vertex recursively
        for (Node vNode = adj[u]; vNode != z; vNode = vNode.next) 
        {
            int v = vNode.vert;
            if (visited[v] == 0) {
                dfs(v);
            }
        }
    }

    public void breadthFirstSearch(int s)
    {
        visited = new int[V + 1];
        for (int v = 1; v <= V; v++) {
            visited[v] = 0; // Mark all vertices as not visited
        }
    
        Queue<Integer> queue = new LinkedList<>();
        visited[s] = 1; // Mark the source vertex as visited
        queue.add(s); // Enqueue the source vertex
    
        System.out.print("\nBreadth First Search starting from vertex " + toChar(s) + ":\n");
        while (!queue.isEmpty()) {
            int u = queue.poll(); // Dequeue a vertex
            System.out.print(toChar(u) + " ");
    
            // Visit all adjacent vertices of dequeued vertex u. If they are not visited, mark them visited and enqueue them.
            for (Node vNode = adj[u]; vNode != z; vNode = vNode.next) {
                if (visited[vNode.vert] == 0) {
                    visited[vNode.vert] = 1;
                    queue.add(vNode.vert);
                }
            }
        }
        System.out.println();
    }
    

}

public class C22483302GraphLists 
{
    public static void main(String[] args) throws IOException
    {
        Scanner scanner = new Scanner(System.in);
        int menuOption;
        
        // Reads user's file
        System.out.print("Enter the file name: ");
        String fname = scanner.nextLine();

        // Reads user's starting vertex number
        System.out.print("Enter the starting vertex number: ");
        int s = scanner.nextInt();

        // Displays the file's graph
        Graph g = new Graph(fname);
        g.display();

        do
        {
            System.out.println("\n1. Depth First\n2. Breadth First\n3. MST Prim\n4. SPT Dijkstra\n5. Exit");
            menuOption = scanner.nextInt(); // Reads user's input for menu
            switch(menuOption)
            {
                case 1:
                    g.depthFirstSearch(s);
                    break;
                case 2:
                    g.breadthFirstSearch(s);
                    break;
                case 3:
                    g.MST_Prim(s);
                    break;
                case 4:
                    g.SPT_Dijkstra(s);
                    break;
                case 5:
                    System.out.println("Exiting...");
                    break;
                default:
                System.out.println("\nError: Not a valid input\n");
            }
        } while (menuOption != 5);            
    }
}
