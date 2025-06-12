package domain;

import datastructure.list.SinglyLinkedList;

public class Passenger {
    private int id;
    private String name, nationality;
    private SinglyLinkedList<Flight> flightHistory;

    public Passenger() {}

    public Passenger(int id, String name, String nationality, SinglyLinkedList<Flight> flightHistory) {
        this.id = id;
        this.name = name;
        this.nationality = nationality;
        this.flightHistory = flightHistory;
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

    // Para búsqueda en AVLTree por id
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Passenger)) return false;
        Passenger p = (Passenger) obj;
        return this.id == p.id;
    }
}