package persistence;

import domain.Airport;
import datastructure.list.SinglyLinkedList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class AirportDataLoader {
    private static final String AIRPORTS_FILE = "data/airports.json";

    public static SinglyLinkedList<Airport> loadAirports() {
        SinglyLinkedList<Airport> list = new SinglyLinkedList<>();
        File file = new File(AIRPORTS_FILE);

        if (!file.exists()) {
            System.out.println("⚠ No se encontró el archivo de aeropuertos. Se retorna lista vacía.");
            return list;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line.trim());
            }

            String json = jsonBuilder.toString();
            if (json.isEmpty() || json.equals("[]")) return list;

            // Remover corchetes externos
            if (json.startsWith("[")) json = json.substring(1);
            if (json.endsWith("]")) json = json.substring(0, json.length() - 1);

            // Separar objetos
            String[] objects = json.split("\\},\\s*\\{");

            for (String obj : objects) {
                String clean = obj.replace("{", "").replace("}", "").trim();
                if (clean.isEmpty()) continue;

                String code = "", name = "", country = "", status = "active";
                String[] fields = clean.split(",");
                for (String field : fields) {
                    String[] keyValue = field.split(":", 2);
                    if (keyValue.length < 2) continue;
                    String key = keyValue[0].trim().replace("\"", "");
                    String value = keyValue[1].trim().replace("\"", "");

                    switch (key) {
                        case "code" -> code = value;
                        case "name" -> name = value;
                        case "country" -> country = value;
                        case "status" -> status = value;
                    }
                }

                if (!code.isEmpty() && !name.isEmpty()) {
                    list.add(new Airport(code, name, country, status));
                }
            }

        } catch (Exception e) {
            System.err.println("Error al cargar aeropuertos: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }
}
