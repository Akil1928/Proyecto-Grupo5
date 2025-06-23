package domain;

import java.util.List;

public class RouteGroup {
    private String origin;
    private List<String> destinations;
    private List<Integer> distances;

    public RouteGroup(String origin, List<String> destinations, List<Integer> distances) {
        this.origin = origin;
        this.destinations = destinations;
        this.distances = distances;
    }

    public String getOrigin() {
        return origin;
    }

    public List<String> getDestinations() {
        return destinations;
    }

    public List<Integer> getDistances() {
        return distances;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setDestinations(List<String> destinations) {
        this.destinations = destinations;
    }

    public void setDistances(List<Integer> distances) {
        this.distances = distances;
    }
}
