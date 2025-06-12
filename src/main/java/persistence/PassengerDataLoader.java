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
        AVLTree tree = new AVLTree();
        File file = new File(PASSENGERS_FILE);
        if (!file.exists()) return tree;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line.trim());
            }
            String json = jsonBuilder.toString();
            if (json.startsWith("[")) json = json.substring(1);
            if (json.endsWith("]")) json = json.substring(0, json.length() - 1);
            String[] objects = json.split("\\},\\s*\\{");
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
                    if (key.equals("id")) id = Integer.parseInt(value);
                    else if (key.equals("name")) name = value;
                    else if (key.equals("nationality")) nationality = value;
                }
                if (id != -1 && !name.isEmpty() && !nationality.isEmpty()) {
                    tree.add(new Passenger(id, name, nationality, null));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading passengers: " + e.getMessage());
        }
        return tree;
    }

    // Guarda el árbol AVL de pasajeros en un archivo JSON
    public static void savePassengers(AVLTree tree) {
        List<Object> inOrder = new ArrayList<>();
        tree.inOrderList(inOrder);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PASSENGERS_FILE))) {
            writer.write("[\n");
            int size = inOrder.size();
            for (int i = 0; i < size; i++) {
                Passenger p = (Passenger) inOrder.get(i);
                writer.write("  { \"id\": " + p.getId()
                        + ", \"name\": \"" + p.getName() + "\""
                        + ", \"nationality\": \"" + p.getNationality() + "\" }");
                if (i < size - 1) writer.write(",");
                writer.write("\n");
            }
            writer.write("]\n");
        } catch (IOException e) {
            System.err.println("Error saving passengers: " + e.getMessage());
        }
    }
}