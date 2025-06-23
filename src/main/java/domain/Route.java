package domain;

import javafx.beans.property.*;

public class Route {
    private final StringProperty origin;
    private final StringProperty destination;
    private final IntegerProperty distance;

    public Route(String origin, String destination, int distance) {
        this.origin = new SimpleStringProperty(origin);
        this.destination = new SimpleStringProperty(destination);
        this.distance = new SimpleIntegerProperty(distance);
    }

    public String getOrigin() { return origin.get(); }
    public void setOrigin(String value) { origin.set(value); }
    public StringProperty originProperty() { return origin; }

    public String getDestination() { return destination.get(); }
    public void setDestination(String value) { destination.set(value); }
    public StringProperty destinationProperty() { return destination; }

    public int getDistance() { return distance.get(); }
    public void setDistance(int value) { distance.set(value); }
    public IntegerProperty distanceProperty() { return distance; }
}
