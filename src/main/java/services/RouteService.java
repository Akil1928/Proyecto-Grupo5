package services;

import datastructure.graph.DirectedSinglyLinkedListGraph;
import datastructure.graph.GraphException;
import datastructure.list.SinglyLinkedList;
import domain.Airport;
import domain.Route;
import persistence.AirportDataLoader;
import persistence.RouteManager;

import java.util.*;

public class RouteService {
    private static RouteService instance;

    private final List<Route> routeList = new ArrayList<>();
    private DirectedSinglyLinkedListGraph graph;

    private RouteService() {
        loadRoutes();
    }

    public static RouteService getInstance() {
        if (instance == null) instance = new RouteService();
        return instance;
    }

    public List<Route> getRoutes() {
        return new ArrayList<>(routeList);
    }

    public void addOrUpdateRoute(Route newRoute) {
        for (int i = 0; i < routeList.size(); i++) {
            Route existing = routeList.get(i);
            if (existing.getOrigin().equals(newRoute.getOrigin()) &&
                    existing.getDestination().equals(newRoute.getDestination())) {
                routeList.set(i, newRoute);
                buildGraph();
                return;
            }
        }
        routeList.add(newRoute);
        buildGraph();
    }

    public void removeRoute(Route route) {
        routeList.removeIf(r ->
                r.getOrigin().equals(route.getOrigin()) &&
                        r.getDestination().equals(route.getDestination()));
        buildGraph();
    }

    public void saveRoutes() {
        try {
            RouteManager.saveRoutesAsGroups(routeList);
        } catch (Exception e) {
            System.err.println("Error al guardar rutas: " + e.getMessage());
        }
    }

    public void loadRoutes() {
        routeList.clear();
        try {
            List<Route> loadedRoutes = RouteManager.loadRoutesFromGroups();
            routeList.addAll(loadedRoutes);
            buildGraph();
        } catch (Exception e) {
            System.err.println("Error al cargar rutas: " + e.getMessage());
        }
    }

    private void buildGraph() {
        try {
            SinglyLinkedList<Airport> airports = AirportDataLoader.loadAirports();
            graph = new DirectedSinglyLinkedListGraph(airports.size());

            for (int i = 1; i <= airports.size(); i++) {
                graph.addVertex(airports.get(i));
            }

            for (Route route : routeList) {
                Airport origin = findAirport(airports, route.getOrigin());
                Airport destination = findAirport(airports, route.getDestination());
                if (origin != null && destination != null) {
                    graph.addEdgeWeight(origin, destination, route.getDistance());
                }
            }
        } catch (GraphException | datastructure.list.ListException e) {
            System.err.println("Error al construir grafo: " + e.getMessage());
        }
    }

    private Airport findAirport(SinglyLinkedList<Airport> list, String code) throws datastructure.list.ListException {
        for (int i = 1; i <= list.size(); i++) {
            Airport a = list.get(i);
            if (a.getCode().equals(code)) return a;
        }
        return null;
    }

    public RouteResult findShortestRoute(String originCode, String destinationCode) {
        try {
            Airport origin = findAirport(graph.getVertices(), originCode);
            Airport destination = findAirport(graph.getVertices(), destinationCode);

            if (origin == null || destination == null) return null;

            Object[] prev = graph.dijkstra(origin);
            double[] distances = graph.getDistances(origin);
            int destIndex = graph.indexOf(destination);

            if (Double.isInfinite(distances[destIndex])) return null;

            List<String> path = new ArrayList<>();
            Object current = destination;
            while (current != null) {
                path.add(0, current.toString());
                current = findPrevious(prev, current);
            }

            String pathStr = String.join(" -> ", path);
            return new RouteResult(pathStr, (int) distances[destIndex]);

        } catch (Exception e) {
            System.err.println("Error en búsqueda de ruta más corta: " + e.getMessage());
            return null;
        }
    }

    private Object findPrevious(Object[] prev, Object current) {
        for (int i = 0; i < prev.length; i++) {
            if (prev[i] != null && prev[i].equals(current)) {
                return prev[i];
            }
        }
        return null;
    }

    // Clase auxiliar para encapsular resultados de Dijkstra
    public static class RouteResult {
        private final String path;
        private final int totalDistance;

        public RouteResult(String path, int totalDistance) {
            this.path = path;
            this.totalDistance = totalDistance;
        }

        public String getPathAsString() {
            return path;
        }

        public int getTotalDistance() {
            return totalDistance;
        }
    }
}
