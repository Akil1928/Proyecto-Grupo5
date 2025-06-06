package domain;

import datastructure.SinglyLinkedList;

public class Passenger {
    private int id;
    private String name, nationality;
    private SinglyLinkedList<Flight> flightHistory;
}
