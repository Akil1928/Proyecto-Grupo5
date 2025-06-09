package domain;

import datastructure.list.SinglyLinkedList;

public class Airport {
    private String code;
    private String name;
    private String country;
    private String status; // "active" o "inactive"
    private SinglyLinkedList<Flight> departuresBoard;

    public Airport() {
        this.departuresBoard = new SinglyLinkedList<>();
    }

    public Airport(String code, String name, String country, String status) {
        this.code = code;
        this.name = name;
        this.country = country;
        this.status = status;
        this.departuresBoard = new SinglyLinkedList<>();
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