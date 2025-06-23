package domain;

import datastructure.list.SinglyLinkedList;

import java.util.Objects;

public class Passenger {
    private int id;
    private String name, nationality;
    private SinglyLinkedList<Flight> flightHistory;

    public Passenger() {
        // Asegurarse de que flightHistory no sea nulo al crear un pasajero
        this.flightHistory = new SinglyLinkedList<>();
    }

    public Passenger(int id, String name, String nationality, SinglyLinkedList<Flight> flightHistory) {
        this.id = id;
        this.name = name;
        this.nationality = nationality;
        this.flightHistory = flightHistory != null ? flightHistory : new SinglyLinkedList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public SinglyLinkedList<Flight> getFlightHistory() { return flightHistory; }
    public void setFlightHistory(SinglyLinkedList<Flight> flightHistory) { this.flightHistory = flightHistory; }

    @Override
    public String toString() {
        return id + " - " + name + " (" + nationality + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passenger passenger = (Passenger) o;
        return id == passenger.id;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
