package services;

import domain.Flight;
import domain.Passenger;
import persistence.FlightDataLoader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FlightService {

    private List<Flight> flightList;  // Lista de vuelos
    private static FlightService instance; // Instancia única del servicio

    private FlightService() {
        this.flightList = FlightDataLoader.loadFlights();
    }

    public static FlightService getInstance() {
        if (instance == null) {
            instance = new FlightService();
        }
        return instance;
    }

    // Método para crear un vuelo
    public Flight createFlight(int number, String origin, String destination, String departureTime, int capacity) {
        // Convertir departureTime a LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime parsedDepartureTime = LocalDateTime.parse(departureTime, formatter);

        // Crear el vuelo
        Flight flight = new Flight(number, origin, destination, parsedDepartureTime, capacity);

        // Añadirlo a la lista de vuelos
        flightList.add(flight);

        // Guardar los cambios en el archivo
        saveFlights();

        return flight;
    }

    // Método para agregar un pasajero al vuelo
    public boolean addPassengerToFlight(int flightNumber, Passenger passenger) {
        Flight flight = findFlight(flightNumber);
        if (flight != null) {
            boolean added = flight.addPassenger(passenger);
            if (added) {
                // Guardar los cambios en el archivo
                saveFlights();
            }
            return added;
        }
        return false;
    }

    // Buscar vuelo por número
    private Flight findFlight(int flightNumber) {
        for (Flight flight : flightList) {
            if (flight.getNumber() == flightNumber) {
                return flight;
            }
        }
        return null;
    }

    // Obtener vuelos activos
    public List<Flight> getActiveFlights() {
        List<Flight> activeFlights = new ArrayList<>();
        for (Flight flight : flightList) {
            if (flight.getOccupancy() < flight.getCapacity()) {
                activeFlights.add(flight);
            }
        }
        return activeFlights;
    }

    // Obtener vuelos completados
    public List<Flight> getCompletedFlights() {
        List<Flight> completedFlights = new ArrayList<>();
        for (Flight flight : flightList) {
            if (flight.getOccupancy() == flight.getCapacity()) {
                completedFlights.add(flight);
            }
        }
        return completedFlights;
    }

    // Obtener todos los vuelos
    public List<Flight> getAllFlights() {
        return flightList;
    }

    // Guardar vuelos en archivo
    public void saveFlights() {
        FlightDataLoader.saveFlights(flightList);
    }

    // Simular un vuelo (marcar como completado)
    public void simulateFlight(int flightNumber) {
        Flight flight = findFlight(flightNumber);
        if (flight != null) {
            // Si necesitas realizar acciones adicionales al simular un vuelo,
            // como actualizar su estado o eliminarlo de la lista de vuelos activos,
            // hazlo aquí

            // Marcar como completado (esto depende de tu modelo de datos)
            // Por ejemplo, si un vuelo completado se elimina de la lista principal:
            // flightList.remove(flight);

            // O si se guarda en otra lista:
            // completedFlights.add(flight);

            // En cualquier caso, guardar los cambios
            saveFlights();

            System.out.println("Vuelo #" + flightNumber + " simulado exitosamente");
        }
    }
}