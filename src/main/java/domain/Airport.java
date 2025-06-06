package domain;

import datastructure.SinglyLinkedList;

public class Airport {
    private int code;
    private String name, country;
    private boolean active;
    private SinglyLinkedList<Flight> departureBoard;
}
