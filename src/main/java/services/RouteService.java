package services;

import datastructure.graph.DirectedSinglyLinkedListGraph;
import datastructure.graph.GraphException;
import datastructure.graph.Vertex;
import datastructure.list.ListException;
import datastructure.list.SinglyLinkedList;
import domain.Airport;
import domain.Destination;
import domain.Route;
import persistence.LoggerManager;
import persistence.RouteDataLoader;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteService {
    private static RouteService instance;
    private SinglyLinkedList<Route> routes;
    private DirectedSinglyLinkedListGraph routeGraph;
    private Map<String, Airport> airportMap;

    // Constructor privado para patrón Singleton
    private RouteService() {
        routes = new SinglyLinkedList<>();
        // Estimamos un máximo de 100 aeropuertos en el sistema
        routeGraph = new DirectedSinglyLinkedListGraph(100);
        airportMap = new HashMap<>();

        try {
            // Cargar aeropuertos
            airportMap = RouteDataLoader.getAirportMap();

            // Cargar rutas desde el JSON
            loadRoutesFromJson();

            // Construir el grafo con las rutas cargadas
            buildGraph();
        } catch (IOException | GraphException | ListException e) {
            System.err.println("Error al inicializar el servicio de rutas: " + e.getMessage());
        }
    }

    // Método para obtener la instancia única
    public static synchronized RouteService getInstance() {
        if (instance == null) {
            instance = new RouteService();
        }
        return instance;
    }

    /**
     * Carga las rutas desde el archivo JSON
     */
    private void loadRoutesFromJson() {
        try {
            routes = RouteDataLoader.loadAll(airportMap);
        } catch (IOException e) {
            System.err.println("Error al cargar las rutas: " + e.getMessage());
        }
    }

    /**
     * Construye el grafo dirigido ponderado a partir de las rutas cargadas
     */
    private void buildGraph() throws GraphException, ListException {
        // Limpiar el grafo
        routeGraph.clear();

        // Primero, agregar todos los aeropuertos como vértices
        for (String code : airportMap.keySet()) {
            routeGraph.addVertex(airportMap.get(code));
        }

        // Luego, agregar todas las rutas como aristas con sus pesos
        for (int i = 1; i <= routes.size(); i++) {
            try {
                Route route = routes.get(i);
                Airport origin = route.getOrigin();
                SinglyLinkedList<Destination> destinations = route.getDestinationList();

                for (int j = 1; j <= destinations.size(); j++) {
                    try {
                        Destination dest = destinations.get(j);
                        routeGraph.addEdgeWeight(origin, dest.getAirport(), dest.getDistance());
                    } catch (ListException e) {
                        System.err.println("Error al procesar destino: " + e.getMessage());
                    }
                }
            } catch (ListException e) {
                System.err.println("Error al procesar ruta: " + e.getMessage());
            }
        }
    }

    /**
     * a. Agregar ruta (con peso en kilómetros o minutos)
     */
    public boolean addRoute(Airport origin, Airport destination, double distance) {
        try {
            // Verificar si los aeropuertos existen
            if (!airportMap.containsKey(origin.getCode()) || !airportMap.containsKey(destination.getCode())) {
                return false;
            }

            // Buscar si ya existe una ruta desde el origen
            Route existingRoute = null;
            for (int i = 1; i <= routes.size(); i++) {
                Route route = routes.get(i);
                if (route.getOrigin().getCode().equals(origin.getCode())) {
                    existingRoute = route;
                    break;
                }
            }

            // Si no existe la ruta, crear una nueva
            if (existingRoute == null) {
                SinglyLinkedList<Destination> destinations = new SinglyLinkedList<>();
                destinations.add(new Destination(destination, distance));
                routes.add(new Route(origin, destinations));
            } else {
                // Si existe, verificar si ya tiene este destino
                SinglyLinkedList<Destination> destinations = existingRoute.getDestinationList();
                boolean destExists = false;

                for (int i = 1; i <= destinations.size(); i++) {
                    Destination dest = destinations.get(i);
                    if (dest.getAirport().getCode().equals(destination.getCode())) {
                        // El destino ya existe, actualizar distancia
                        dest.setDistance(distance);
                        destExists = true;
                        break;
                    }
                }

                // Si el destino no existe, agregarlo
                if (!destExists) {
                    destinations.add(new Destination(destination, distance));
                }
            }

            // Actualizar el grafo
            if (routeGraph.containsVertex(origin) && routeGraph.containsVertex(destination)) {
                if (routeGraph.containsEdge(origin, destination)) {
                    routeGraph.addWeight(origin, destination, distance);
                } else {
                    routeGraph.addEdgeWeight(origin, destination, distance);
                }
            } else {
                // Reconstruir el grafo si los vértices no existen
                buildGraph();
            }

            // Registrar en el log
            LoggerManager.logEvent(new domain.EventLog(
                    LocalDateTime.now(),
                    "SYSTEM",
                    "Se agregó una ruta desde " + origin.getCode() + " hasta " + destination.getCode() +
                            " con distancia " + distance
            ));

            return true;
        } catch (GraphException | ListException e) {
            System.err.println("Error al agregar ruta: " + e.getMessage());
            return false;
        }
    }

    /**
     * b. Modificar ruta (cambiar distancia)
     */
    public boolean updateRouteDistance(Airport origin, Airport destination, double newDistance) {
        try {
            // Verificar si los aeropuertos existen
            if (!airportMap.containsKey(origin.getCode()) || !airportMap.containsKey(destination.getCode())) {
                return false;
            }

            // Buscar la ruta desde el origen
            Route existingRoute = null;
            for (int i = 1; i <= routes.size(); i++) {
                Route route = routes.get(i);
                if (route.getOrigin().getCode().equals(origin.getCode())) {
                    existingRoute = route;
                    break;
                }
            }

            // Si no existe la ruta, retornar false
            if (existingRoute == null) {
                return false;
            }

            // Buscar el destino en la lista de destinos
            SinglyLinkedList<Destination> destinations = existingRoute.getDestinationList();
            boolean updated = false;

            for (int i = 1; i <= destinations.size(); i++) {
                Destination dest = destinations.get(i);
                if (dest.getAirport().getCode().equals(destination.getCode())) {
                    // Actualizar la distancia
                    dest.setDistance(newDistance);
                    updated = true;
                    break;
                }
            }

            // Si se actualizó, también actualizar el grafo
            if (updated) {
                routeGraph.addWeight(origin, destination, newDistance);

                // Registrar en el log
                LoggerManager.logEvent(new domain.EventLog(
                        LocalDateTime.now(),
                        "SYSTEM",
                        "Se modificó la distancia de la ruta desde " + origin.getCode() +
                                " hasta " + destination.getCode() + " a " + newDistance
                ));
            }

            return updated;
        } catch (GraphException | ListException e) {
            System.err.println("Error al modificar ruta: " + e.getMessage());
            return false;
        }
    }

    /**
     * c. Calcular la ruta más corta entre dos aeropuertos utilizando Dijkstra
     */
    public List<Airport> findShortestPath(Airport origin, Airport destination) {
        try {
            // Verificar si los aeropuertos existen
            if (!airportMap.containsKey(origin.getCode()) || !airportMap.containsKey(destination.getCode())) {
                return null;
            }

            // Ejecutar el algoritmo de Dijkstra
            Object[] previous = routeGraph.dijkstra(origin);

            // Reconstruir el camino
            List<Airport> path = new ArrayList<>();
            Airport current = destination;

            // Si no hay ruta al destino
            int destIndex = -1;
            for (int i = 0; i < previous.length; i++) {
                try {
                    // Usar data en lugar de getData()
                    if (routeGraph.getVertex(i).data.equals(destination)) {
                        destIndex = i;
                        break;
                    }
                } catch (ListException e) {
                    System.err.println("Error al buscar índice de destino: " + e.getMessage());
                }
            }

            if (destIndex == -1 || previous[destIndex] == null) {
                return null; // No hay ruta
            }

            // Reconstruir el camino desde el destino hasta el origen
            while (current != null && !current.equals(origin)) {
                path.add(0, current);

                // Buscar el predecesor
                Object pred = null;
                for (int i = 0; i < previous.length; i++) {
                    try {
                        // Usar data en lugar de getData()
                        if (routeGraph.getVertex(i).data.equals(current)) {
                            pred = previous[i];
                            break;
                        }
                    } catch (ListException e) {
                        System.err.println("Error al buscar predecesor: " + e.getMessage());
                    }
                }

                if (pred == null) break;
                current = (Airport) pred;
            }

            // Agregar el origen al inicio
            path.add(0, origin);

            // Registrar en el log
            LoggerManager.logEvent(new domain.EventLog(
                    LocalDateTime.now(),
                    "SYSTEM",
                    "Se calculó la ruta más corta desde " + origin.getCode() +
                            " hasta " + destination.getCode()
            ));

            return path;
        } catch (GraphException | ListException e) {
            System.err.println("Error al calcular ruta más corta: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obtener la distancia total de la ruta más corta
     */
    public double getShortestPathDistance(Airport origin, Airport destination) {
        try {
            // Verificar si los aeropuertos existen
            if (!airportMap.containsKey(origin.getCode()) || !airportMap.containsKey(destination.getCode())) {
                return -1;
            }

            // Obtener las distancias del algoritmo de Dijkstra
            double[] distances = routeGraph.getDistances(origin);

            // Buscar el índice del destino
            int destIndex = -1;
            for (int i = 0; i < routeGraph.size(); i++) {
                try {
                    // Usar data en lugar de getData()
                    if (routeGraph.getVertex(i).data.equals(destination)) {
                        destIndex = i;
                        break;
                    }
                } catch (ListException e) {
                    System.err.println("Error al buscar índice de destino: " + e.getMessage());
                }
            }

            if (destIndex == -1 || distances[destIndex] == Double.POSITIVE_INFINITY) {
                return -1; // No hay ruta
            }

            return distances[destIndex];
        } catch (GraphException | ListException e) {
            System.err.println("Error al obtener distancia de ruta más corta: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Obtener todas las rutas
     */
    public SinglyLinkedList<Route> getAllRoutes() {
        return routes;
    }

    /**
     * Obtener una ruta específica por aeropuerto de origen
     */
    public Route getRouteByOrigin(String originCode) {
        try {
            for (int i = 1; i <= routes.size(); i++) {
                Route route = routes.get(i);
                if (route.getOrigin().getCode().equals(originCode)) {
                    return route;
                }
            }
        } catch (ListException e) {
            System.err.println("Error al buscar ruta por origen: " + e.getMessage());
        }
        return null;
    }

    /**
     * Eliminar una ruta completa desde un aeropuerto de origen
     */
    public boolean deleteRoute(String originCode) {
        try {
            for (int i = 1; i <= routes.size(); i++) {
                Route route = routes.get(i);
                if (route.getOrigin().getCode().equals(originCode)) {
                    routes.remove(route);

                    // Reconstruir el grafo
                    buildGraph();

                    // Registrar en el log
                    LoggerManager.logEvent(new domain.EventLog(
                            LocalDateTime.now(),
                            "SYSTEM",
                            "Se eliminó la ruta desde " + originCode
                    ));

                    return true;
                }
            }
        } catch (ListException | GraphException e) {
            System.err.println("Error al eliminar ruta: " + e.getMessage());
        }
        return false;
    }

    /**
     * Eliminar un destino específico de una ruta
     */
    public boolean deleteDestination(String originCode, String destinationCode) {
        try {
            for (int i = 1; i <= routes.size(); i++) {
                Route route = routes.get(i);
                if (route.getOrigin().getCode().equals(originCode)) {
                    SinglyLinkedList<Destination> destinations = route.getDestinationList();
                    for (int j = 1; j <= destinations.size(); j++) {
                        Destination dest = destinations.get(j);
                        if (dest.getAirport().getCode().equals(destinationCode)) {
                            destinations.remove(dest);

                            // Si no quedan destinos, eliminar la ruta completa
                            if (destinations.isEmpty()) {
                                routes.remove(route);
                            }

                            // Reconstruir el grafo
                            buildGraph();

                            // Registrar en el log
                            LoggerManager.logEvent(new domain.EventLog(
                                    LocalDateTime.now(),
                                    "SYSTEM",
                                    "Se eliminó el destino " + destinationCode + " de la ruta desde " + originCode
                            ));

                            return true;
                        }
                    }
                }
            }
        } catch (ListException | GraphException e) {
            System.err.println("Error al eliminar destino: " + e.getMessage());
        }
        return false;
    }
}