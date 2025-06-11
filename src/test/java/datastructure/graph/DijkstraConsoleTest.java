package datastructure.graph;

import datastructure.graph.*;
import datastructure.list.*;

public class DijkstraConsoleTest {
    public static void main(String[] args) {
        try {
            // Crear grafo con 5 aeropuertos (ejemplo)
            AdjacencyListGraph graph = new AdjacencyListGraph(5);

            Vertex a = new Vertex("A"), b = new Vertex("B"), c = new Vertex("C"),
                    d = new Vertex("D"), e = new Vertex("E");

            graph.insertVertex(a);
            graph.insertVertex(b);
            graph.insertVertex(c);
            graph.insertVertex(d);
            graph.insertVertex(e);

            graph.insertEdge(a, b, 10);
            graph.insertEdge(a, c, 3);
            graph.insertEdge(b, d, 2);
            graph.insertEdge(c, b, 1);
            graph.insertEdge(c, d, 8);
            graph.insertEdge(c, e, 2);
            graph.insertEdge(e, d, 9);
            graph.insertEdge(d, e, 7);

            // Aplicar Dijkstra desde A hasta D
            GraphAlgorithms algorithms = new GraphAlgorithms(graph);
            SinglyLinkedList<Vertex> path = algorithms.dijkstra(a, d);

            System.out.println("Ruta más corta desde A hasta D:");
            for (int i = 1; i <= path.size(); i++) {
                System.out.print(path.getNode(i).data);
                if (i < path.size()) System.out.print(" -> ");
            }
            System.out.println();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
