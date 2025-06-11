package datastructure.graph;

import com.google.gson.Gson;
import datastructure.graph.*;
import datastructure.list.ListException;
import datastructure.list.SinglyLinkedList;

import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DijkstraRealTest {
    public static void main(String[] args) {
        try {
            // 1. Leer aeropuertos desde JSON
            Gson gson = new Gson();
            Reader airportReader = new FileReader("src/main/java/data/airports.json");
            AirportData[] airports = gson.fromJson(airportReader, AirportData[].class);

            // Crear grafo con tamaño dinámico
            AdjacencyListGraph graph = new AdjacencyListGraph(airports.length);
            Map<String, Vertex> airportMap = new HashMap<>();

            // Insertar vértices
            for (AirportData airport : airports) {
                Vertex v = new Vertex(airport.getCode());
                graph.insertVertex(v);
                airportMap.put(airport.getCode(), v);
            }

            // 2. Leer rutas desde JSON y agregarlas al grafo
            Reader routeReader = new FileReader("src/main/java/data/routes.json");
            RouteData[] routeArray = gson.fromJson(routeReader, RouteData[].class);

            for (RouteData route : routeArray) {
                Vertex origin = airportMap.get(route.getOrigin());
                for (int j = 0; j < route.getDestinations().length; j++) {
                    String destCode = route.getDestinations()[j];
                    int distance = route.getDistances()[j];
                    Vertex destination = airportMap.get(destCode);
                    graph.insertEdge(origin, destination, distance);
                }
            }

            // 3. Solicitar entrada desde consola
            Scanner scanner = new Scanner(System.in);
            System.out.print("Ingrese código del aeropuerto origen: ");
            String origenCode = scanner.nextLine().toUpperCase();
            System.out.print("Ingrese código del aeropuerto destino: ");
            String destinoCode = scanner.nextLine().toUpperCase();

            Vertex origen = airportMap.get(origenCode);
            Vertex destino = airportMap.get(destinoCode);

            if (origen == null || destino == null) {
                System.out.println("Código de aeropuerto inválido.");
                return;
            }

            // 4. Ejecutar Dijkstra
            GraphAlgorithms algorithms = new GraphAlgorithms(graph);
            SinglyLinkedList<Vertex> path = algorithms.dijkstra(origen, destino);

            System.out.println("\nRuta más corta desde " + origenCode + " hasta " + destinoCode + ":");
            for (int i = 1; i <= path.size(); i++) {
                System.out.print(path.getNode(i).data);
                if (i < path.size()) System.out.print(" -> ");
            }
            System.out.println();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Clases auxiliares para JSON
    static class AirportData {
        private String code;
        public String getCode() { return code; }
    }

    static class RouteData {
        private String origin;
        private String[] destinations;
        private int[] distances;

        public String getOrigin() { return origin; }
        public String[] getDestinations() { return destinations; }
        public int[] getDistances() { return distances; }
    }
}
