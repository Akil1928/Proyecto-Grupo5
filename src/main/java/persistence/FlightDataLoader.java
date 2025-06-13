
package persistence;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import domain.Flight;
import domain.Passenger;
import services.PassengerService;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FlightDataLoader {
    private static final String FLIGHTS_FILE = "data/flights.json";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Carga todos los vuelos desde el archivo JSON.
     * @return Lista de vuelos
     */
    public static List<Flight> loadFlights() {
        List<Flight> flights = new ArrayList<>();
        File file = new File(FLIGHTS_FILE);

        if (!file.exists()) {
            // Si el archivo no existe, crear el directorio y retornar lista vacía
            File directory = new File("data");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            return flights;
        }

        try (Reader reader = new FileReader(FLIGHTS_FILE)) {
            // Crear un deserializador personalizado para LocalDateTime
            JsonDeserializer<LocalDateTime> dateTimeDeserializer = (json, typeOfT, context) ->
                    LocalDateTime.parse(json.getAsString(), formatter);

            // Crear Gson con adaptadores personalizados
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, dateTimeDeserializer)
                    .create();

            // Definir el tipo para la deserialización
            Type flightListType = new TypeToken<List<JsonFlight>>(){}.getType();

            // Leer la lista de JsonFlight desde el archivo
            List<JsonFlight> jsonFlights = gson.fromJson(reader, flightListType);

            if (jsonFlights != null) {
                PassengerService passengerService = PassengerService.getInstance();

                for (JsonFlight jsonFlight : jsonFlights) {
                    // Crear el vuelo
                    Flight flight = new Flight(
                            jsonFlight.number,
                            jsonFlight.origin,
                            jsonFlight.destination,
                            LocalDateTime.parse(jsonFlight.departureTime, formatter),
                            jsonFlight.capacity
                    );

                    // Agregar pasajeros al vuelo
                    if (jsonFlight.passengerIds != null) {
                        for (int passengerId : jsonFlight.passengerIds) {
                            Passenger passenger = passengerService.getPassengerById(passengerId);
                            if (passenger != null) {
                                flight.addPassenger(passenger);
                            }
                        }
                    }

                    flights.add(flight);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar vuelos: " + e.getMessage());
            e.printStackTrace();
        }

        return flights;
    }

    /**
     * Guarda todos los vuelos en el archivo JSON.
     * @param flights Lista de vuelos a guardar
     */
    public static void saveFlights(List<Flight> flights) {
        try {
            // Crear directorio si no existe
            File directory = new File("data");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Crear serializer personalizado para LocalDateTime
            JsonSerializer<LocalDateTime> dateTimeSerializer = (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.format(formatter));

            // Crear Gson con adaptadores personalizados
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, dateTimeSerializer)
                    .setPrettyPrinting()
                    .create();

            // Convertir vuelos a formato JSON simplificado
            List<JsonFlight> jsonFlights = new ArrayList<>();
            for (Flight flight : flights) {
                JsonFlight jsonFlight = new JsonFlight();
                jsonFlight.number = flight.getNumber();
                jsonFlight.origin = flight.getOrigin();
                jsonFlight.destination = flight.getDestination();
                jsonFlight.departureTime = flight.getDepartureTime().format(formatter);
                jsonFlight.capacity = flight.getCapacity();
                jsonFlight.occupancy = flight.getOccupancy();

                // Guardar solo los IDs de los pasajeros
                List<Integer> passengerIds = new ArrayList<>();
                for (Passenger passenger : flight.getPassengers()) {
                    passengerIds.add(passenger.getId());
                }
                jsonFlight.passengerIds = passengerIds;

                jsonFlights.add(jsonFlight);
            }

            // Escribir al archivo
            try (Writer writer = new FileWriter(FLIGHTS_FILE)) {
                gson.toJson(jsonFlights, writer);
            }

        } catch (Exception e) {
            System.err.println("Error al guardar vuelos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Clase auxiliar para serialización/deserialización
    private static class JsonFlight {
        int number;
        String origin;
        String destination;
        String departureTime;
        int capacity;
        int occupancy;
        List<Integer> passengerIds;
    }
}