package persistence;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import domain.Airport;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class AirportDataLoader {
    private static final String AIRPORTS_FILE = "data/airports.json";

    /**
     * Carga todos los aeropuertos del JSON.
     * @return lista de objetos Airport
     * @throws IOException si ocurre un error de I/O
     */
    public static List<Airport> loadAll() throws IOException {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(AIRPORTS_FILE)) {
            Type listType = new TypeToken<List<Airport>>(){}.getType();
            return gson.fromJson(reader, listType);
        }
    }
}