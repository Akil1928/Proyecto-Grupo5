package persistence;

import domain.Flight;
import domain.Passenger;
import services.PersonService;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FlightDataLoader {
    private static final String FLIGHTS_FILE = "data/flights.json";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Carga todos los vuelos desde el archivo JSON usando parsing manual
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

        try (BufferedReader reader = new BufferedReader(new FileReader(FLIGHTS_FILE))) {
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line.trim());
            }

            String json = jsonBuilder.toString();
            if (json.isEmpty() || json.equals("[]")) return flights;

            // Remover corchetes externos
            if (json.startsWith("[")) json = json.substring(1);
            if (json.endsWith("]")) json = json.substring(0, json.length() - 1);

            // Dividir por objetos
            String[] objects = json.split("\\},\\s*\\{");

            // ✅ Usar PersonService correctamente
            PersonService personService = PersonService.getInstance();

            for (String obj : objects) {
                String clean = obj.replace("{", "").replace("}", "").trim();
                if (clean.isEmpty()) continue;

                // Parsear campos del vuelo
                int number = -1;
                String origin = "";
                String destination = "";
                String departureTime = "";
                int capacity = 0;
                List<Integer> passengerIds = new ArrayList<>(); // ✅ IDs como Integer

                String[] fields = clean.split(",");
                for (String field : fields) {
                    String[] keyValue = field.split(":", 2);
                    if (keyValue.length < 2) continue;

                    String key = keyValue[0].trim().replace("\"", "");
                    String value = keyValue[1].trim();

                    switch (key) {
                        case "number":
                            number = Integer.parseInt(value);
                            break;
                        case "origin":
                            origin = value.replace("\"", "");
                            break;
                        case "destination":
                            destination = value.replace("\"", "");
                            break;
                        case "departureTime":
                            departureTime = value.replace("\"", "");
                            break;
                        case "capacity":
                            capacity = Integer.parseInt(value);
                            break;
                        case "passengerIds":
                            value = value.replace("[", "").replace("]", "");
                            if (!value.trim().isEmpty()) {
                                String[] ids = value.split(",");
                                for (String id : ids) {
                                    String cleanId = id.trim().replace("\"", "");
                                    if (!cleanId.isEmpty()) {
                                        try {
                                            // ✅ Convertir a int
                                            int passengerId = Integer.parseInt(cleanId);
                                            passengerIds.add(passengerId);
                                        } catch (NumberFormatException e) {
                                            System.err.println("ID de pasajero inválido: " + cleanId);
                                        }
                                    }
                                }
                            }
                            break;
                    }
                }

                // Crear el vuelo si tenemos datos válidos
                if (number != -1 && !origin.isEmpty() && !destination.isEmpty() && !departureTime.isEmpty()) {
                    try {
                        Flight flight = new Flight(
                                number,
                                origin,
                                destination,
                                LocalDateTime.parse(departureTime, formatter),
                                capacity
                        );

                        // ✅ Agregar pasajeros al vuelo usando PersonService
                        for (Integer passengerId : passengerIds) {
                            Passenger passenger = personService.findPassengerById(passengerId);
                            if (passenger != null) {
                                flight.addPassenger(passenger);
                                System.out.println("✓ Pasajero " + passenger.getName() + " agregado al vuelo " + number);
                            } else {
                                System.out.println("⚠ Pasajero con ID " + passengerId + " no encontrado");
                            }
                        }

                        flights.add(flight);
                        System.out.println("✓ Vuelo " + number + " cargado con " + flight.getPassengers().size() + " pasajeros");

                    } catch (Exception e) {
                        System.err.println("Error creando vuelo: " + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error al cargar vuelos: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("✓ Total de vuelos cargados: " + flights.size());
        return flights;
    }

    /**
     * Guarda todos los vuelos en el archivo JSON usando escritura manual
     */
    public static void saveFlights(List<Flight> flights) {
        try {
            // Crear directorio si no existe
            File directory = new File("data");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FLIGHTS_FILE))) {
                writer.write("[\n");

                int size = flights.size();
                for (int i = 0; i < size; i++) {
                    Flight flight = flights.get(i);

                    writer.write("  {\n");
                    writer.write("    \"number\": " + flight.getNumber() + ",\n");
                    writer.write("    \"origin\": \"" + flight.getOrigin() + "\",\n");
                    writer.write("    \"destination\": \"" + flight.getDestination() + "\",\n");
                    writer.write("    \"departureTime\": \"" + flight.getDepartureTime().format(formatter) + "\",\n");
                    writer.write("    \"capacity\": " + flight.getCapacity() + ",\n");
                    writer.write("    \"occupancy\": " + flight.getOccupancy() + ",\n");

                    // Escribir array de passenger IDs
                    writer.write("    \"passengerIds\": [");
                    List<Passenger> passengers = flight.getPassengers();
                    for (int j = 0; j < passengers.size(); j++) {
                        // ✅ CORRECTO: getId() devuelve int, lo guardamos como número
                        writer.write(passengers.get(j).getId());
                        if (j < passengers.size() - 1) {
                            writer.write(", ");
                        }
                    }
                    writer.write("]\n");

                    writer.write("  }");
                    if (i < size - 1) {
                        writer.write(",");
                    }
                    writer.write("\n");
                }

                writer.write("]\n");
            }

            System.out.println("✓ Vuelos guardados exitosamente en " + FLIGHTS_FILE);

        } catch (Exception e) {
            System.err.println("Error al guardar vuelos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}