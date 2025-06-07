package domain;

public class Destination {
    private Airport airport;
    private double distance;

    public Destination() {
    }

    public Destination(Airport airport, double distance) {
        this.airport = airport;
        this.distance = distance;
    }

    public Airport getAirport() {
        return airport;
    }

    public void setAirport(Airport airport) {
        this.airport = airport;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "Destination{" +
                "airport=" + airport +
                ", distance=" + distance +
                '}';
    }
}
