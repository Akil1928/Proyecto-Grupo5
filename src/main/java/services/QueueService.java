package services;

import domain.Airport;
import domain.Flight;
import domain.Passenger;
import datastructure.list.ListException;
import datastructure.list.SinglyLinkedList;

public class QueueService {
    private static QueueService instance;
    private FlightService flightService;
    private AirportService airportService;

    private QueueService() {
        this.flightService = FlightService.getInstance();
        this.airportService = AirportService.getInstance();
    }

    public static QueueService getInstance() {
        if (instance == null) {
            instance = new QueueService();
        }
        return instance;
    }

    public boolean purchaseTicket(Flight flight, Passenger passenger) {
        try {
            // Verificar capacidad del vuelo
            int currentPassengers = flight.getPassengers().size();
            int flightCapacity = flight.getCapacity();

            System.out.println("=== COMPRA DE TIQUETE ===");
            System.out.println("Vuelo: " + flight.getNumber());
            System.out.println("Pasajeros actuales: " + currentPassengers);
            System.out.println("Capacidad: " + flightCapacity);
            System.out.println("Pasajero: " + passenger.getName());

            if (currentPassengers < flightCapacity) {
                // Hay espacio disponible - agregar directamente al vuelo
                flight.getPassengers().add(passenger);
                System.out.println("✓ Pasajero agregado directamente al vuelo");

                // Guardar cambios
                flightService.saveFlights();
                return true;

            } else {
                // Vuelo lleno - agregar a la cola de embarque del aeropuerto
                Airport departureAirport = findAirportByCode(flight.getOrigin());
                if (departureAirport == null) {
                    throw new RuntimeException("No se pudo encontrar el aeropuerto de origen");
                }

                // Encolar pasajero en el aeropuerto
                departureAirport.getBoardingQueue().enQueue(passenger);
                System.out.println("✓ Pasajero encolado en aeropuerto " + departureAirport.getCode());

                // Mostrar estado de la cola
                int queueSize = departureAirport.getBoardingQueue().size();
                System.out.println("Cola de embarque actual: " + queueSize + " pasajeros");

                // Guardar cambios
                airportService.saveAirports();
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error en compra de tiquete: " + e.getMessage());
            throw new RuntimeException("Error al procesar la compra: " + e.getMessage());
        }
    }

    public int processQueue(Flight flight) {
        try {
            Airport departureAirport = findAirportByCode(flight.getOrigin());
            if (departureAirport == null) {
                throw new RuntimeException("No se pudo encontrar el aeropuerto de origen");
            }

            // Verificar si hay espacio en el vuelo y pasajeros en cola
            int currentPassengers = flight.getPassengers().size();
            int flightCapacity = flight.getCapacity();

            if (currentPassengers >= flightCapacity) {
                System.out.println("El vuelo está completo");
                return 0;
            }

            if (departureAirport.getBoardingQueue().isEmpty()) {
                System.out.println("No hay pasajeros en la cola de espera");
                return 0;
            }

            // Procesar pasajeros de la cola mientras haya espacio
            int processed = 0;
            while (currentPassengers < flightCapacity && !departureAirport.getBoardingQueue().isEmpty()) {
                try {
                    Passenger nextPassenger = (Passenger) departureAirport.getBoardingQueue().deQueue();
                    flight.getPassengers().add(nextPassenger);
                    currentPassengers++;
                    processed++;

                    System.out.println("✓ Pasajero procesado de la cola: " + nextPassenger.getName());

                } catch (Exception e) {
                    System.err.println("Error procesando cola: " + e.getMessage());
                    break;
                }
            }

            if (processed > 0) {
                // Guardar cambios
                flightService.saveFlights();
                airportService.saveAirports();
            }

            return processed;

        } catch (Exception e) {
            System.err.println("Error procesando cola: " + e.getMessage());
            throw new RuntimeException("Error al procesar la cola: " + e.getMessage());
        }
    }

    public String getQueueStatus(Flight flight) {
        try {
            Airport departureAirport = findAirportByCode(flight.getOrigin());
            if (departureAirport == null) {
                return "Error: No se pudo encontrar el aeropuerto de origen";
            }

            StringBuilder status = new StringBuilder();
            status.append("=== ESTADO DE COLA ===\n");
            status.append("Aeropuerto: ").append(departureAirport.getCode()).append(" - ").append(departureAirport.getName()).append("\n");
            status.append("Vuelo: ").append(flight.getNumber()).append("\n");
            status.append("Origen: ").append(flight.getOrigin()).append(" → Destino: ").append(flight.getDestination()).append("\n\n");

            // Estado del vuelo
            int currentPassengers = flight.getPassengers().size();
            int capacity = flight.getCapacity();
            int available = capacity - currentPassengers;

            status.append("ESTADO DEL VUELO:\n");
            status.append("Pasajeros confirmados: ").append(currentPassengers).append("/").append(capacity).append("\n");
            status.append("Asientos disponibles: ").append(available).append("\n\n");

            // Estado de la cola
            if (departureAirport.getBoardingQueue().isEmpty()) {
                status.append("LISTA DE ESPERA:\n");
                status.append("No hay pasajeros en lista de espera\n");
            } else {
                int queueSize = departureAirport.getBoardingQueue().size();
                status.append("LISTA DE ESPERA:\n");
                status.append("Pasajeros en espera: ").append(queueSize).append("\n\n");

                status.append("Próximos en la cola:\n");
                try {
                    // Mostrar los primeros 5 pasajeros en la cola
                    String queueDetails = departureAirport.getBoardingQueue().toString();
                    status.append(queueDetails);
                } catch (Exception e) {
                    status.append("Error al obtener detalles de la cola\n");
                }
            }

            return status.toString();

        } catch (Exception e) {
            return "Error al obtener estado de la cola: " + e.getMessage();
        }
    }


    public int clearQueue(String airportCode) {
        try {
            Airport airport = findAirportByCode(airportCode);
            if (airport == null) {
                throw new RuntimeException("Aeropuerto no encontrado: " + airportCode);
            }

            int queueSize = airport.getBoardingQueue().size();

            // Limpiar la cola
            while (!airport.getBoardingQueue().isEmpty()) {
                airport.getBoardingQueue().deQueue();
            }

            // Guardar cambios
            airportService.saveAirports();

            System.out.println("✓ Cola limpiada en aeropuerto " + airportCode + ". Pasajeros removidos: " + queueSize);
            return queueSize;

        } catch (Exception e) {
            System.err.println("Error limpiando cola: " + e.getMessage());
            throw new RuntimeException("Error al limpiar la cola: " + e.getMessage());
        }
    }


    private Airport findAirportByCode(String code) {
        try {
            SinglyLinkedList<Airport> airportList = airportService.listAirports("active");

            for (int i = 1; i <= airportList.size(); i++) {
                Airport airport = (Airport) airportList.getNode(i).data;
                if (airport.getCode().equals(code)) {
                    return airport;
                }
            }
            return null;
        } catch (ListException e) {
            System.err.println("Error buscando aeropuerto: " + e.getMessage());
            return null;
        }
    }
}