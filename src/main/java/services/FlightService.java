package services;

import domain.Flight;
import domain.Passenger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FlightService {

    private List<Flight> flightList;  // Lista de vuelos
    private static FlightService instance; // Instancia única del servicio

    private FlightService() {
        this.flightList = new ArrayList<>();
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
        return flight;
    }

    // Método para agregar un pasajero al vuelo
    public boolean addPassengerToFlight(int flightNumber, Passenger passenger) {
        Flight flight = findFlight(flightNumber);
        if (flight != null) {
            return flight.addPassenger(passenger);
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

    // Métodos faltantes llamados desde el controlador
    public void displayActiveFlights() {
        // Implementación para mostrar vuelos activos
        System.out.println("Mostrando vuelos activos");
    }

    public void displayCompletedFlights() {
        // Implementación para mostrar vuelos completados
        System.out.println("Mostrando vuelos completados");
    }

    public void simulateFlight(int flightNumber) {
        // Implementación de la simulación de vuelo
        System.out.println("Simulando vuelo #" + flightNumber);
    }
}