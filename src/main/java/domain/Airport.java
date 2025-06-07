package domain;

import datastructure.list.SinglyLinkedList;

public class Airport {
    private int code;
    private String name, country;
    private boolean active;
    private SinglyLinkedList<Flight> departureBoard;

    public Airport() {

    }

    public Airport(int code, String name, String country, boolean active, SinglyLinkedList<Flight> departureBoard) {
        this.code = code;
        this.name = name;
        this.country = country;
        this.active = active;
        this.departureBoard = departureBoard;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public SinglyLinkedList<Flight> getDepartureBoard() {
        return departureBoard;
    }

    public void setDepartureBoard(SinglyLinkedList<Flight> departureBoard) {
        this.departureBoard = departureBoard;
    }

    @Override
    public String toString() {
        return "Airport{" +
                "code=" + code +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", active=" + active +
                ", departureBoard=" + departureBoard +
                '}';
    }
}
