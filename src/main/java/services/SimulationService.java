package services;

import domain.Flight;
import domain.Passenger;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SimulationService {
    private FlightService flightService;
    private static SimulationService instance;

    private SimulationService() {
        this.flightService = FlightService.getInstance();
    }

    public static synchronized SimulationService getInstance() {
        if (instance == null) {
            instance = new SimulationService();
        }
        return instance;
    }

    /**
     * Simula un vuelo completo
     * @param flightNumber Número del vuelo a simular
     * @return Reporte de la simulación
     */
    public String simulateFlight(int flightNumber) {
        List<Flight> flights = flightService.getAllFlights();
        Flight flight = null;

        // Buscar el vuelo por su número
        for (Flight f : flights) {
            if (f.getNumber() == flightNumber) {
                flight = f;
                break;
            }
        }

        if (flight == null) {
            return "Error: Vuelo no encontrado";
        }

        StringBuilder report = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Información del vuelo
        report.append("SIMULACIÓN DE VUELO #").append(flight.getNumber()).append("\n\n");
        report.append("Información del vuelo:\n");
        report.append("Origen: ").append(flight.getOrigin()).append("\n");
        report.append("Destino: ").append(flight.getDestination()).append("\n");
        report.append("Salida: ").append(flight.getDepartureTime().format(formatter)).append("\n");
        report.append("Capacidad: ").append(flight.getCapacity()).append("\n");
        report.append("Ocupación: ").append(flight.getOccupancy()).append(" pasajeros\n\n");

        // Etapa 1: Embarque
        report.append("ETAPA 1: EMBARQUE DE PASAJEROS\n");
        List<Passenger> passengers = flight.getPassengers();
        for (Passenger p : passengers) {
            report.append("Embarcando pasajero: ").append(p.getId()).append(" - ")
                    .append(p.getName()).append(" (").append(p.getNationality()).append(")\n");
        }
        report.append("\n");

        // Etapa 2: Despegue
        report.append("ETAPA 2: DESPEGUE\n");
        report.append("El vuelo ").append(flight.getNumber()).append(" ha despegado desde ")
                .append(flight.getOrigin()).append("\n\n");

        // Etapa 3: Vuelo en progreso
        report.append("ETAPA 3: VUELO EN PROGRESO\n");
        report.append("El vuelo ").append(flight.getNumber()).append(" está en ruta de ")
                .append(flight.getOrigin()).append(" a ").append(flight.getDestination()).append("\n\n");

        // Etapa 4: Aterrizaje
        report.append("ETAPA 4: ATERRIZAJE\n");
        report.append("El vuelo ").append(flight.getNumber()).append(" ha aterrizado en ")
                .append(flight.getDestination()).append("\n\n");

        // Etapa 5: Desembarque
        report.append("ETAPA 5: DESEMBARQUE DE PASAJEROS\n");
        for (Passenger p : passengers) {
            report.append("Desembarcando pasajero: ").append(p.getId()).append(" - ")
                    .append(p.getName()).append(" (").append(p.getNationality()).append(")\n");
        }

        // Marcar el vuelo como simulado/completado
        flightService.simulateFlight(flightNumber);

        return report.toString();
    }
}