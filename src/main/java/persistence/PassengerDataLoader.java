package persistence;

import datastructure.tree.AVLTree;
import domain.Passenger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PassengerDataLoader {
    private static final String PASSENGERS_FILE = "passengers.json";

    // Carga los pasajeros en un árbol AVL desde un archivo JSON
    public static AVLTree loadPassengers() {
        System.out.println("PassengerDataLoader.loadPassengers: Iniciando carga desde " + PASSENGERS_FILE);
        AVLTree tree = new AVLTree();
        File file = new File(PASSENGERS_FILE);

        if (!file.exists()) {
            System.out.println("PassengerDataLoader.loadPassengers: Archivo no existe, creando árbol vacío");
            return tree;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line.trim());
            }

            String json = jsonBuilder.toString();
            System.out.println("PassengerDataLoader.loadPassengers: JSON leído - " + json.length() + " caracteres");

            if (json.isEmpty() || json.equals("[]")) {
                System.out.println("PassengerDataLoader.loadPassengers: Archivo vacío");
                return tree;
            }

            // Procesar JSON
            if (json.startsWith("[")) json = json.substring(1);
            if (json.endsWith("]")) json = json.substring(0, json.length() - 1);

            String[] objects = json.split("\\},\\s*\\{");
            int loadedCount = 0;

            for (String obj : objects) {
                String clean = obj.replace("{", "").replace("}", "").trim();
                if (clean.isEmpty()) continue;

                String[] fields = clean.split(",");
                int id = -1;
                String name = "";
                String nationality = "";

                for (String field : fields) {
                    String[] keyValue = field.split(":", 2);
                    if (keyValue.length < 2) continue;

                    String key = keyValue[0].trim().replace("\"", "");
                    String value = keyValue[1].trim().replace("\"", "");

                    switch (key) {
                        case "id":
                            try {
                                id = Integer.parseInt(value);
                            } catch (NumberFormatException e) {
                                System.err.println("PassengerDataLoader.loadPassengers: Error parsing ID - " + value);
                            }
                            break;
                        case "name":
                            name = value;
                            break;
                        case "nationality":
                            nationality = value;
                            break;
                        // Ignorar flightHistory por ahora
                    }
                }

                if (id != -1 && !name.isEmpty() && !nationality.isEmpty()) {
                    Passenger passenger = new Passenger(id, name, nationality, null);
                    tree.add(passenger);
                    loadedCount++;
                    System.out.println("PassengerDataLoader.loadPassengers: Cargado - " + passenger.toString());
                } else {
                    System.err.println("PassengerDataLoader.loadPassengers: Datos incompletos - ID:" + id + ", Name:" + name + ", Nationality:" + nationality);
                }
            }

            System.out.println("PassengerDataLoader.loadPassengers: " + loadedCount + " pasajeros cargados exitosamente");

        } catch (IOException e) {
            System.err.println("PassengerDataLoader.loadPassengers: Error de E/O - " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("PassengerDataLoader.loadPassengers: Error inesperado - " + e.getMessage());
            e.printStackTrace();
        }

        return tree;
    }

    // Guarda el árbol AVL de pasajeros en un archivo JSON
    public static void savePassengers(AVLTree tree) {
        System.out.println("PassengerDataLoader.savePassengers: Iniciando guardado");

        if (tree == null) {
            System.err.println("PassengerDataLoader.savePassengers: Árbol es nulo");
            return;
        }

        List<Object> inOrder = new ArrayList<>();
        tree.inOrderList(inOrder);

        System.out.println("PassengerDataLoader.savePassengers: Guardando " + inOrder.size() + " pasajeros");

        try {
            // Crear directorio si no existe
            File file = new File(PASSENGERS_FILE);
            file.getParentFile(); // No necesita crear directorio si está en raíz

        } catch (Exception e) {
            // Continuar aunque falle la creación del directorio
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PASSENGERS_FILE))) {
            writer.write("[\n");
            int size = inOrder.size();

            for (int i = 0; i < size; i++) {
                Passenger p = (Passenger) inOrder.get(i);

                // Escribir JSON del pasajero
                writer.write("  { \"id\": " + p.getId()
                        + ", \"name\": \"" + p.getName().replace("\"", "\\\"") + "\""
                        + ", \"nationality\": \"" + p.getNationality().replace("\"", "\\\"") + "\""
                        + ", \"flightHistory\": [] }");

                if (i < size - 1) writer.write(",");
                writer.write("\n");

                System.out.println("PassengerDataLoader.savePassengers: Guardado - " + p.toString());
            }

            writer.write("]\n");
            System.out.println("PassengerDataLoader.savePassengers: Archivo guardado exitosamente en " + PASSENGERS_FILE);

        } catch (IOException e) {
            System.err.println("PassengerDataLoader.savePassengers: Error de E/O - " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("PassengerDataLoader.savePassengers: Error inesperado - " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método auxiliar para verificar si el archivo existe y su contenido
    public static void debugFileStatus() {
        File file = new File(PASSENGERS_FILE);
        System.out.println("=== DEBUG PassengerDataLoader ===");
        System.out.println("Archivo: " + file.getAbsolutePath());
        System.out.println("Existe: " + file.exists());
        if (file.exists()) {
            System.out.println("Tamaño: " + file.length() + " bytes");
            System.out.println("Puede leer: " + file.canRead());
            System.out.println("Puede escribir: " + file.canWrite());
        }
        System.out.println("=================================");
    }
}