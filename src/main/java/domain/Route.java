package domain;

import datastructure.list.SinglyLinkedList;

public class Route {
    private Airport origin;
    private SinglyLinkedList<Destination> destinationList;

    public Route() {

    }

    public Route(Airport origin, SinglyLinkedList<Destination> destinationList) {
        this.origin = origin;
        this.destinationList = destinationList;
    }

    public Airport getOrigin() {
        return origin;
    }

    public void setOrigin(Airport origin) {
        this.origin = origin;
    }

    public SinglyLinkedList<Destination> getDestinationList() {
        return destinationList;
    }

    public void setDestinationList(SinglyLinkedList<Destination> destinationList) {
        this.destinationList = destinationList;
    }

    @Override
    public String toString() {
        return "Route{" +
                "origin=" + origin +
                ", destinationList=" + destinationList +
                '}';
    }
}
