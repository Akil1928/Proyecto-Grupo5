package persistence;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import domain.Airport;
import domain.Destination;
import domain.Route;
import datastructure.list.SinglyLinkedList;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class RouteDataLoader {
    private static final String ROUTES_FILE = "data/routes.json";

    /**
     * Carga todas las rutas del archivo JSON.
     * @param airportMap Mapa de códigos de aeropuertos a objetos Airport
     * @return Lista de objetos Route
     * @throws IOException si ocurre un error de I/O
     */
    public static SinglyLinkedList<Route> loadAll(Map<String, Airport> airportMap) throws IOException {
        Gson gson = new Gson();
        SinglyLinkedList<Route> routes = new SinglyLinkedList<>();

        try (FileReader reader = new FileReader(ROUTES_FILE)) {
            JsonArray jsonArray = gson.fromJson(reader, JsonArray.class);

            for (JsonElement element : jsonArray) {
                JsonObject routeObj = element.getAsJsonObject();

                // Obtener el aeropuerto de origen
                String originCode = routeObj.get("origin").getAsString();
                Airport origin = airportMap.get(originCode);

                if (origin == null) {
                    // Si el aeropuerto no existe, continuamos con la siguiente ruta
                    System.err.println("Aeropuerto de origen no encontrado: " + originCode);
                    continue;
                }

                // Crear la lista de destinos
                SinglyLinkedList<Destination> destinationList = new SinglyLinkedList<>();

                // Obtener arrays de destinos y distancias
                JsonArray destinationCodes = routeObj.getAsJsonArray("destinations");
                JsonArray distances = routeObj.getAsJsonArray("distances");

                // Asegurarse de que los arrays tengan el mismo tamaño
                if (destinationCodes.size() != distances.size()) {
                    System.err.println("Error en formato de ruta para " + originCode);
                    continue;
                }

                for (int i = 0; i < destinationCodes.size(); i++) {
                    String destCode = destinationCodes.get(i).getAsString();
                    double distance = distances.get(i).getAsDouble();

                    Airport destAirport = airportMap.get(destCode);
                    if (destAirport != null) {
                        Destination destination = new Destination(destAirport, distance);
                        destinationList.add(destination);
                    } else {
                        System.err.println("Aeropuerto de destino no encontrado: " + destCode);
                    }
                }

                // Crear y agregar la ruta
                Route route = new Route(origin, destinationList);
                routes.add(route);
            }
        }

        return routes;
    }

    /**
     * Método auxiliar para obtener un mapa de códigos de aeropuertos a objetos Airport
     * @return Mapa de códigos de aeropuertos a objetos Airport
     * @throws IOException si ocurre un error de I/O
     */
    public static Map<String, Airport> getAirportMap() throws IOException {
        List<Airport> airports = AirportDataLoader.loadAll();
        Map<String, Airport> airportMap = new HashMap<>();

        for (Airport airport : airports) {
            airportMap.put(airport.getCode(), airport);
        }

        return airportMap;
    }
}