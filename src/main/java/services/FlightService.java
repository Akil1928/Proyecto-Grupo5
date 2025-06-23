package services;

import datastructure.queue.QueueException;
import domain.Flight;
import domain.Passenger;
import domain.Airport;
import persistence.FlightDataLoader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FlightService {

    private List<Flight> flightList;  // Lista de vuelos
    private static FlightService instance; // Instancia única del servicio
    private AirportService airportService; // Servicio de aeropuertos

    private FlightService() {
        this.flightList = FlightDataLoader.loadFlights();
        this.airportService = AirportService.getInstance();
    }

    public static FlightService getInstance() {
        if (instance == null) {
            instance = new FlightService();
        }
        return instance;
    }

    // ========== NUEVO MÉTODO: COMPRA DE TIQUETES CON ENCOLADO ==========

    /**
     * Trata de vender un tiquete a un pasajero.
     * Si el vuelo tiene espacio, agrega al pasajero directamente.
     * Si está lleno, encola al pasajero en el aeropuerto de origen.
     *
     * @param flightNumber Número del vuelo
     * @param passenger    Pasajero que compra el tiquete
     * @return true si se agregó al vuelo; false si quedó en cola
     * @throws IllegalArgumentException si el vuelo no existe
     */
    public boolean purchaseTicket(int flightNumber, Passenger passenger) throws QueueException {
        Flight flight = findFlight(flightNumber);
        if (flight == null) {
            throw new IllegalArgumentException("Vuelo no existe: " + flightNumber);
        }

        // Si el vuelo no está lleno, agregar pasajero directamente
        if (!flight.isFull()) {
            flight.addPassenger(passenger);
            saveFlights();
            System.out.println("✓ Tiquete vendido - Pasajero agregado al vuelo " + flightNumber);
            return true;
        } else {
            // Si está lleno, encolar en el aeropuerto de origen
            Airport originAirport = airportService.findAirportByCode(flight.getOrigin());
            if (originAirport != null) {
                originAirport.getBoardingQueue().enQueue(passenger);
                airportService.saveAirports(); // Guardar cambios en aeropuertos
                System.out.println("⏳ Vuelo lleno - Pasajero encolado en aeropuerto " + flight.getOrigin());
                return false;
            } else {
                throw new IllegalArgumentException("Aeropuerto de origen no encontrado: " + flight.getOrigin());
            }
        }
    }

    public int processFlightQueue(int flightNumber) {
        Flight flight = findFlight(flightNumber);
        if (flight == null) {
            throw new IllegalArgumentException("Vuelo no existe: " + flightNumber);
        }

        Airport originAirport = airportService.findAirportByCode(flight.getOrigin());
        if (originAirport == null) {
            throw new IllegalArgumentException("Aeropuerto de origen no encontrado: " + flight.getOrigin());
        }

        int processedCount = 0;

        // Procesar cola mientras haya espacio en el vuelo y pasajeros en cola
        while (!flight.isFull() && !originAirport.getBoardingQueue().isEmpty()) {
            Passenger passenger = null;
            try {
                passenger = (Passenger) originAirport.getBoardingQueue().deQueue();
            } catch (QueueException e) {
                throw new RuntimeException(e);
            }
            if (passenger != null) {
                flight.addPassenger(passenger);
                processedCount++;
                System.out.println("✓ Pasajero " + passenger.getName() + " procesado desde cola al vuelo " + flightNumber);
            }
        }

        if (processedCount > 0) {
            saveFlights(); // Guardar cambios en vuelos
            airportService.saveAirports(); // Guardar cambios en aeropuertos
            System.out.println("📋 Total procesados: " + processedCount + " pasajeros");
        } else {
            System.out.println("ℹ️ No hay pasajeros en cola o el vuelo está lleno");
        }

        return processedCount;
    }

    // ========== NUEVO MÉTODO: OBTENER ESTADO DE COLA ==========

    /**
     * Obtiene información sobre la cola de embarque de un vuelo
     *
     * @param flightNumber Número del vuelo
     * @return Información de la cola (número de pasajeros en espera)
     */
    public String getQueueStatus(int flightNumber) {
        Flight flight = findFlight(flightNumber);
        if (flight == null) {
            return "Vuelo no encontrado";
        }

        Airport originAirport = airportService.findAirportByCode(flight.getOrigin());
        if (originAirport == null) {
            return "Aeropuerto no encontrado";
        }

        int queueSize = originAirport.getBoardingQueue().size();
        return String.format("Vuelo %d: %d pasajeros en cola de embarque (Aeropuerto: %s)",
                flightNumber, queueSize, flight.getOrigin());
    }

    // ========== NUEVO MÉTODO: LIMPIAR COLA ==========

    /**
     * Limpia la cola de embarque de un aeropuerto específico
     *
     * @param flightNumber Número del vuelo (para identificar aeropuerto)
     * @return Número de pasajeros removidos de la cola
     */
    public int clearFlightQueue(int flightNumber) {
        Flight flight = findFlight(flightNumber);
        if (flight == null) {
            throw new IllegalArgumentException("Vuelo no existe: " + flightNumber);
        }

        Airport originAirport = airportService.findAirportByCode(flight.getOrigin());
        if (originAirport == null) {
            throw new IllegalArgumentException("Aeropuerto de origen no encontrado: " + flight.getOrigin());
        }

        int clearedCount = originAirport.getBoardingQueue().size();
        originAirport.getBoardingQueue().clear();
        airportService.saveAirports();

        System.out.println("🗑️ Cola limpiada: " + clearedCount + " pasajeros removidos");
        return clearedCount;
    }

    // ========== MÉTODO MEJORADO: BUSCAR VUELO ==========

    /**
     * Busca un vuelo por número (versión pública para uso externo)
     */
    public Flight findByNumber(int flightNumber) {
        return findFlight(flightNumber);
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

    // Método para agregar un pasajero al vuelo (método directo sin cola)
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

    // Buscar vuelo por número (método privado)
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
            // Antes de simular, procesar cola si hay pasajeros esperando
            int processed = processFlightQueue(flightNumber);
            if (processed > 0) {
                System.out.println("Procesados " + processed + " pasajeros desde cola antes del vuelo");
            }

            // Marcar como completado
            saveFlights();
            System.out.println("Vuelo #" + flightNumber + " simulado exitosamente");
        }
    }

    public boolean removePassengerFromFlight(Flight selectedFlight, Passenger selectedPassenger) {
        if (selectedFlight == null || selectedPassenger == null) {
            return false;
        }

        // Verificar que el vuelo exista en nuestra lista
        Flight flight = findFlight(selectedFlight.getNumber());
        if (flight == null) {
            return false;
        }

        // Intentar eliminar el pasajero del vuelo
        boolean removed = flight.removePassenger(selectedPassenger);

        // Si se eliminó correctamente, guardar los cambios
        if (removed) {
            saveFlights();
            System.out.println("✓ Pasajero " + selectedPassenger.getName() +
                    " eliminado del vuelo " + flight.getNumber());
        }

        return removed;
    }
}