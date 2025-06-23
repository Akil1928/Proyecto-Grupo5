package persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import domain.RouteGroup;
import domain.Route;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RouteManager {
    private static final String ROUTES_FILE = "data/routes.json";

    // ✅ Cargar rutas desde archivo en formato RouteGroup
    public static List<Route> loadRoutesFromGroups() {
        List<Route> routes = new ArrayList<>();
        File file = new File(ROUTES_FILE);

        if (!file.exists()) {
            System.out.println("⚠ Archivo routes.json no encontrado en: " + file.getAbsolutePath());
            return routes;
        }

        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<RouteGroup>>() {}.getType();
            List<RouteGroup> groups = new Gson().fromJson(reader, listType);

            for (RouteGroup group : groups) {
                String origin = group.getOrigin();
                List<String> destinations = group.getDestinations();
                List<Integer> distances = group.getDistances();

                for (int i = 0; i < destinations.size(); i++) {
                    routes.add(new Route(origin, destinations.get(i), distances.get(i)));
                }
            }

        } catch (Exception e) {
            System.err.println("Error al cargar rutas desde JSON: " + e.getMessage());
            e.printStackTrace();
        }

        return routes;
    }

    // ✅ Guardar rutas agrupadas por origen en formato legible
    public static void saveRoutesAsGroups(List<Route> routes) {
        try {
            File file = new File(ROUTES_FILE);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs(); // crear carpeta /data
            }

            List<RouteGroup> groups = groupRoutesByOrigin(routes);

            try (Writer writer = new FileWriter(file)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(groups, writer);
                System.out.println("✓ Rutas guardadas correctamente en: " + file.getAbsolutePath());
            }

        } catch (Exception e) {
            System.err.println("Error al guardar rutas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Agrupa rutas en objetos RouteGroup por origen
    private static List<RouteGroup> groupRoutesByOrigin(List<Route> routes) {
        List<RouteGroup> result = new ArrayList<>();
        // Mapa temporal para agrupar por código de origen
        java.util.Map<String, RouteGroup> map = new java.util.HashMap<>();

        for (Route route : routes) {
            String origin = route.getOrigin();
            map.putIfAbsent(origin, new RouteGroup(origin, new ArrayList<>(), new ArrayList<>()));
            RouteGroup group = map.get(origin);
            group.getDestinations().add(route.getDestination());
            group.getDistances().add(route.getDistance());
        }

        result.addAll(map.values());
        return result;
    }
}
