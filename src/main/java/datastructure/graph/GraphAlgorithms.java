package datastructure.graph;

import datastructure.list.*;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Comparator;

public class GraphAlgorithms {

    private AdjacencyListGraph graph;

    public GraphAlgorithms(AdjacencyListGraph graph) {
        this.graph = graph;
    }

    public SinglyLinkedList<Vertex> dijkstra(Vertex source, Vertex target) throws ListException {
        Map<Vertex, Integer> distances = new HashMap<>();
        Map<Vertex, Vertex> previous = new HashMap<>();
        PriorityQueue<Vertex> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        for (int i = 0; i < graph.size(); i++) {
            Vertex v = graph.getVertex(i);
            distances.put(v, v.equals(source) ? 0 : Integer.MAX_VALUE);
            queue.add(v);
        }

        while (!queue.isEmpty()) {
            Vertex current = queue.poll();

            if (current.equals(target)) break;

            List<EdgeWeight> neighbors = (List<EdgeWeight>) graph.getSuccessors(current);
            for (int i = 1; i <= neighbors.size(); i++) {
                EdgeWeight ew = neighbors.getNode(i).data;
                Vertex neighbor = (Vertex) ew.getEdge();
                int weight = (int) ew.getWeight();
                int alt = distances.get(current) + weight;
                if (alt < distances.get(neighbor)) {
                    distances.put(neighbor, alt);
                    previous.put(neighbor, current);
                    // Reinsertar para actualizar prioridad
                    queue.remove(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        // Construir la ruta
        SinglyLinkedList<Vertex> path = new SinglyLinkedList<>();
        for (Vertex at = target; at != null; at = previous.get(at)) {
            path.addFirst(at);
        }

        if (!path.isEmpty() && path.getFirst().equals(source)) {
            return path;
        } else {
            throw new ListException("No se encontró ruta desde el aeropuerto origen al destino.");
        }
    }
}
