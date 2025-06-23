
package domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Flight {
    private int number;  // Número del vuelo
    private String origin;  // Aeropuerto de origen
    private String destination;  // Aeropuerto de destino
    private LocalDateTime departureTime;  // Hora de salida
    private int capacity;  // Capacidad máxima de pasajeros
    private int occupancy;  // Ocupación actual (número de pasajeros a bordo)
    private List<Passenger> passengers; // Lista de pasajeros

    // Constructor
    public Flight(int number, String origin, String destination, LocalDateTime departureTime, int capacity) {
        this.number = number;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.capacity = capacity;
        this.occupancy = 0;  // Inicialmente no hay pasajeros
        this.passengers = new ArrayList<>();  // Inicializamos la lista de pasajeros
    }

    // Métodos para agregar pasajeros
    public boolean addPassenger(Passenger passenger) {
        // Verificar si el pasajero ya existe
        if (!passengers.contains(passenger)) {
            if (occupancy < capacity) {
                passengers.add(passenger);
                occupancy = passengers.size();  // Mantener consistencia
                return true;
            }
        }
        return false;
    }

    // Métodos getter y setter
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    // Método adicional para compatibilidad con el controlador
    public String getDestino() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(int occupancy) {
        this.occupancy = occupancy;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
        this.occupancy = passengers != null ? passengers.size() : 0;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "number=" + number +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", departureTime=" + departureTime +
                ", capacity=" + capacity +
                ", occupancy=" + occupancy +
                '}';
    }

    // Método para comprobar si el vuelo está lleno
    public boolean isFull() {
        return occupancy >= capacity;
    }

    // Método para eliminar un pasajero
    public boolean removePassenger(Passenger passenger) {
        boolean removed = passengers.remove(passenger);
        if (removed) {
            occupancy--;
        }
        return removed;
    }

    public Passenger getPassenger(int i) {
        if (i >= 0 && i < passengers.size()) {
            return passengers.get(i);
        } else {
            return null;
        }
    }
}