package domain;

import datastructure.list.SinglyLinkedList;
import datastructure.queue.LinkedQueue;
import domain.Passenger;

public class Airport {
    private String code;
    private String name;
    private String country;
    private String status; // "active" o "inactive"
    private SinglyLinkedList<Flight> departuresBoard;

    // 1. Cola de espera de pasajeros
    private LinkedQueue boardingQueue;

    public Airport() {
        this.departuresBoard = new SinglyLinkedList<>();
        // Inicializar la cola de espera
        this.boardingQueue = new LinkedQueue();
    }

    public Airport(String code, String name, String country, String status) {
        this.code = code;
        this.name = name;
        this.country = country;
        this.status = status;
        this.departuresBoard = new SinglyLinkedList<>();
        // Inicializar la cola de espera
        this.boardingQueue = new LinkedQueue();
    }

    // Getters y setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SinglyLinkedList<Flight> getDeparturesBoard() {
        return departuresBoard;
    }

    public void setDeparturesBoard(SinglyLinkedList<Flight> departuresBoard) {
        this.departuresBoard = departuresBoard;
    }

    // Getter corregido para la cola de espera de pasajeros
    public LinkedQueue getBoardingQueue() {
        return boardingQueue;
    }

    @Override
    public String toString() {
        return "Airport{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}