package persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Passenger;
import datastructure.list.SinglyLinkedList;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class PassengerDataLoader {
    private static final String PASSENGERS_FILE = "passengers.json";

    // Cargar pasajeros desde JSON
    public static SinglyLinkedList<Passenger> loadPassengers() {
        SinglyLinkedList<Passenger> passengers = new SinglyLinkedList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File(PASSENGERS_FILE);
            // Verifica si el archivo existe antes de intentar leerlo
            if (!file.exists()) {
                return passengers;
            }
            // Carga la lista de pasajeros del archivo
            List<Passenger> list = mapper.readValue(file, new TypeReference<List<Passenger>>() {});
            for (Passenger p : list) {
                passengers.add(p);
            }
        } catch (Exception e) {
            System.err.println("Error loading passengers: " + e.getMessage());
        }
        return passengers;
    }

    // Guardar pasajeros en JSON
    public static void savePassengers(SinglyLinkedList<Passenger> passengers) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Passenger> list = new ArrayList<>();
            // Recorre desde 1 hasta size, asumiendo que tu SinglyLinkedList es 1-based
            for (int i = 1; i <= passengers.size(); i++) {
                list.add((Passenger) passengers.getNode(i).data);
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(PASSENGERS_FILE), list);
        } catch (Exception e) {
            System.err.println("Error saving passengers: " + e.getMessage());
        }
    }
}