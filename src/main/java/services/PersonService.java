package services;

import datastructure.list.ListException;
import datastructure.list.SinglyLinkedList;
import domain.Passenger;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PersonService {
    private static PersonService instance;
    private SinglyLinkedList<Passenger> passengers;
    private static final String DATA_FILE = "data/passengers.json";

    private PersonService() {
        this.passengers = new SinglyLinkedList<>();
        System.out.println("PersonService: Constructor ejecutado");

        // Cargar datos existentes
        loadPassengers();

        // Si no hay datos, cargar datos iniciales
        if (passengers.isEmpty()) {
            System.out.println("PersonService: No hay datos guardados, cargando pasajeros iniciales");
            loadInitialPassengers();
        }
    }

    public static PersonService getInstance() {
        if (instance == null) {
            instance = new PersonService();
        }
        return instance;
    }

    // Cargar pasajeros iniciales
    private void loadInitialPassengers() {
        System.out.println("PersonService: Cargando pasajeros iniciales");
        passengers.add(new Passenger(101, "Carlos Vargas", "Costa Rica", null));
        passengers.add(new Passenger(102, "Ana Jiménez", "Costa Rica", null));
        passengers.add(new Passenger(103, "John Smith", "USA", null));
        System.out.println("PersonService: " + passengers.size() + " pasajeros iniciales cargados");
    }

    // Obtener todos los pasajeros
    public SinglyLinkedList<Passenger> getAllPassengers() {
        return passengers;
    }

    // Agregar nuevo pasajero
    public boolean addPassenger(Passenger passenger) {
        if (passenger == null) return false;

        // Verificar que no exista ya
        if (findPassengerById(passenger.getId()) != null) {
            System.out.println("PersonService: Pasajero con ID " + passenger.getId() + " ya existe");
            return false;
        }

        passengers.add(passenger);
        System.out.println("PersonService: Pasajero agregado - " + passenger.toString());

        // Guardar automáticamente
        savePassengers();
        return true;
    }

    // Buscar pasajero por ID
    public Passenger findPassengerById(int id) {
        for (int i = 0; i < passengers.size(); i++) {
            Passenger p = null;
            try {
                p = passengers.get(i);
            } catch (ListException e) {
                throw new RuntimeException(e);
            }
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    // Eliminar pasajero
    public boolean removePassenger(int id) {
        Passenger toRemove = findPassengerById(id);
        if (toRemove == null) return false;

        try {
            passengers.remove(toRemove);
        } catch (ListException e) {
            throw new RuntimeException(e);
        }
        System.out.println("PersonService: Pasajero eliminado - " + toRemove.toString());

        // Guardar automáticamente
        savePassengers();
        return true;
    }

    // Cargar desde archivo
    private void loadPassengers() {
        try {
            File file = new File(DATA_FILE);
            if (!file.exists()) {
                System.out.println("PersonService.loadPassengers: Archivo no existe - " + DATA_FILE);
                return;
            }

            StringBuilder json = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line.trim());
                }
            }

            String jsonContent = json.toString();
            if (jsonContent.isEmpty() || jsonContent.equals("[]")) {
                System.out.println("PersonService.loadPassengers: Archivo vacío");
                return;
            }

            // Parsear JSON manualmente
            jsonContent = jsonContent.trim();
            if (jsonContent.startsWith("[")) jsonContent = jsonContent.substring(1);
            if (jsonContent.endsWith("]")) jsonContent = jsonContent.substring(0, jsonContent.length() - 1);

            String[] objects = jsonContent.split("\\},\\s*\\{");
            int loadedCount = 0;

            for (String obj : objects) {
                String clean = obj.replace("{", "").replace("}", "").trim();
                if (clean.isEmpty()) continue;

                int id = extractIntValue(clean, "id");
                String name = extractStringValue(clean, "name");
                String nationality = extractStringValue(clean, "nationality");

                if (id != -1 && !name.isEmpty() && !nationality.isEmpty()) {
                    Passenger passenger = new Passenger(id, name, nationality, null);
                    passengers.add(passenger);
                    loadedCount++;
                    System.out.println("PersonService.loadPassengers: Cargado - " + passenger.toString());
                }
            }

            System.out.println("PersonService.loadPassengers: " + loadedCount + " pasajeros cargados desde archivo");

        } catch (Exception e) {
            System.err.println("PersonService.loadPassengers: Error - " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Guardar a archivo
    public boolean savePassengers() {
        try {
            System.out.println("PersonService.savePassengers: Guardando " + passengers.size() + " pasajeros");

            // Crear el contenido JSON manualmente
            StringBuilder json = new StringBuilder();
            json.append("[\n");

            for (int i = 0; i < passengers.size(); i++) {
                Passenger p = passengers.get(i);
                json.append("  { \"id\": ").append(p.getId())
                        .append(", \"name\": \"").append(p.getName().replace("\"", "\\\"")).append("\"")
                        .append(", \"nationality\": \"").append(p.getNationality().replace("\"", "\\\"")).append("\"")
                        .append(", \"flightHistory\": [] }");

                if (i < passengers.size() - 1) {
                    json.append(",");
                }
                json.append("\n");
            }

            json.append("]\n");

            // Escribir al archivo
            File file = new File(DATA_FILE);
            file.getParentFile().mkdirs(); // Crear directorio si no existe

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(json.toString());
            }

            System.out.println("PersonService.savePassengers: Guardado exitoso en " + DATA_FILE);
            return true;

        } catch (Exception e) {
            System.err.println("PersonService.savePassengers: Error - " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Métodos auxiliares para parsear JSON
    private String extractStringValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]+)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : "";
    }

    private int extractIntValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*(\\d+)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group(1));
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    // Convertir a lista para compatibilidad con otros sistemas
    public List<Passenger> getPassengersList() {
        List<Passenger> list = new ArrayList<>();
        for (int i = 0; i < passengers.size(); i++) {
            try {
                list.add(passengers.get(i));
            } catch (ListException e) {
                throw new RuntimeException(e);
            }
        }
        return list;
    }
}
