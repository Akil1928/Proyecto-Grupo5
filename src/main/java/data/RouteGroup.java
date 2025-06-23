package data;

import java.util.List;

public class RouteGroup {
    private String origin;
    private List<String> destinations;
    private List<Integer> distances;

    public RouteGroup(String origin) {
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
}
