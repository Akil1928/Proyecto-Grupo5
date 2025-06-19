package services;

import domain.Airport;
import datastructure.list.SinglyLinkedList;
import datastructure.list.ListException;

public class AirportService {
    private static AirportService instance;
    private SinglyLinkedList<Airport> airports;

    // Constructor privado para patrón Singleton
    private AirportService() {
        this.airports = new SinglyLinkedList<>();
        System.out.println("AirportService: Constructor ejecutado");
        loadInitialAirports();
    }

    // Método para obtener la instancia única
    public static synchronized AirportService getInstance() {
        if (instance == null) {
            System.out.println("AirportService: Creando nueva instancia");
            instance = new AirportService();
        } else {
            System.out.println("AirportService: Usando instancia existente");
        }
        return instance;
    }

    /**
     * Crear un nuevo aeropuerto
     * @param airport El aeropuerto a crear
     * @return true si se creó correctamente, false si ya existe un aeropuerto con el mismo código
     */
    public boolean createAirport(Airport airport) {
        try {
            // Verificar si ya existe un aeropuerto con el mismo código
            if (!airports.isEmpty()) {
                try {
                    for (int i = 1; i <= airports.size(); i++) {
                        Airport existingAirport = (Airport) airports.getNode(i).data;
                        if (existingAirport.getCode().equals(airport.getCode())) {
                            return false; // Ya existe un aeropuerto con este código
                        }
                    }
                } catch (ListException e) {
                    System.err.println("Error verificando aeropuerto existente: " + e.getMessage());
                }
            }

            // Si llegamos aquí, es seguro añadir el aeropuerto
            airports.add(airport);
            System.out.println("Aeropuerto añadido: " + airport.getCode());
            return true;
        } catch (Exception e) {
            System.err.println("Error creando aeropuerto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualizar los datos de un aeropuerto existente
     * @param code Código del aeropuerto a actualizar
     * @param name Nuevo nombre (o null para mantener el actual)
     * @param country Nuevo país (o null para mantener el actual)
     * @param status Nuevo estado (o null para mantener el actual)
     * @return true si se actualizó correctamente, false si no se encontró el aeropuerto
     */
    public boolean updateAirport(String code, String name, String country, String status) {
        try {
            System.out.println("AirportService.updateAirport: Intentando actualizar aeropuerto con código " + code);

            if (airports.isEmpty()) {
                System.out.println("AirportService.updateAirport: La lista está vacía");
                return false;
            }

            int size = airports.size();
            System.out.println("AirportService.updateAirport: Tamaño de la lista: " + size);

            for (int i = 1; i <= size; i++) {
                Airport airport = (Airport) airports.getNode(i).data;
                if (airport.getCode().equals(code)) {
                    System.out.println("AirportService.updateAirport: Aeropuerto encontrado, actualizando...");

                    // Actualizar los campos si no son null
                    if (name != null && !name.isEmpty()) {
                        airport.setName(name);
                    }
                    if (country != null && !country.isEmpty()) {
                        airport.setCountry(country);
                    }
                    if (status != null && !status.isEmpty()) {
                        airport.setStatus(status);
                    }

                    // Actualizar el nodo en la lista
                    airports.getNode(i).data = airport;
                    System.out.println("AirportService.updateAirport: Aeropuerto actualizado con éxito");
                    return true;
                }
            }

            System.out.println("AirportService.updateAirport: No se encontró el aeropuerto con código " + code);
            return false;
        } catch (ListException e) {
            System.err.println("AirportService.updateAirport: Error de lista: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("AirportService.updateAirport: Error general: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Eliminar un aeropuerto
     * @param code Código del aeropuerto a eliminar
     * @return true si se eliminó correctamente, false si no se encontró el aeropuerto
     */
    public boolean deleteAirport(String code) {
        try {
            System.out.println("AirportService.deleteAirport: Intentando eliminar aeropuerto con código " + code);

            if (airports.isEmpty()) {
                System.out.println("AirportService.deleteAirport: La lista está vacía");
                return false;
            }

            int size = airports.size();
            System.out.println("AirportService.deleteAirport: Tamaño de la lista antes de eliminar: " + size);

            for (int i = 1; i <= size; i++) {
                Airport airport = (Airport) airports.getNode(i).data;
                if (airport.getCode().equals(code)) {
                    System.out.println("AirportService.deleteAirport: Aeropuerto encontrado, eliminando...");

                    // Crear una nueva lista temporal
                    SinglyLinkedList<Airport> tempList = new SinglyLinkedList<>();

                    // Copiar todos los aeropuertos excepto el que se va a eliminar
                    for (int j = 1; j <= size; j++) {
                        if (j != i) {
                            tempList.add((Airport) airports.getNode(j).data);
                        }
                    }

                    // Reemplazar la lista original
                    airports = tempList;

                    System.out.println("AirportService.deleteAirport: Aeropuerto eliminado con éxito");
                    System.out.println("AirportService.deleteAirport: Tamaño de la lista después de eliminar: " + airports.size());
                    return true;
                }
            }

            System.out.println("AirportService.deleteAirport: No se encontró el aeropuerto con código " + code);
            return false;
        } catch (ListException e) {
            System.err.println("AirportService.deleteAirport: Error de lista: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("AirportService.deleteAirport: Error general: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Activar un aeropuerto
     * @param code Código del aeropuerto a activar
     * @return true si se activó correctamente, false si no se encontró el aeropuerto
     */
    public boolean activateAirport(String code) {
        System.out.println("AirportService.activateAirport: Cambiando estado a 'active' para " + code);
        return updateAirport(code, null, null, "active");
    }

    /**
     * Desactivar un aeropuerto
     * @param code Código del aeropuerto a desactivar
     * @return true si se desactivó correctamente, false si no se encontró el aeropuerto
     */
    public boolean deactivateAirport(String code) {
        System.out.println("AirportService.deactivateAirport: Cambiando estado a 'inactive' para " + code);
        return updateAirport(code, null, null, "inactive");
    }

    /**
     * Listar todos los aeropuertos
     * @param filter "active" para solo activos, "inactive" para solo inactivos, cualquier otro valor para todos
     * @return Lista de aeropuertos según el filtro
     */
    public SinglyLinkedList<Airport> listAirports(String filter) {
        SinglyLinkedList<Airport> filteredList = new SinglyLinkedList<>();

        if (airports.isEmpty()) {
            System.out.println("AirportService.listAirports: La lista está vacía");
            // Volver a cargar aeropuertos iniciales si la lista está vacía
            loadInitialAirports();

            if (airports.isEmpty()) {
                System.out.println("AirportService.listAirports: La lista sigue vacía después de intentar cargarla");
                return filteredList;
            }
        }

        int size = airports.size();
        System.out.println("AirportService.listAirports: Tamaño de la lista: " + size);

        for (int i = 1; i <= size; i++) {
            try {
                Airport airport = (Airport) airports.getNode(i).data;

                // Aplicar filtro
                if (filter == null || filter.isEmpty()) {
                    filteredList.add(airport); // Listar todos
                } else if (filter.equals("active") && airport.getStatus().equals("active")) {
                    filteredList.add(airport); // Solo activos
                } else if (filter.equals("inactive") && airport.getStatus().equals("inactive")) {
                    filteredList.add(airport); // Solo inactivos
                }
            } catch (Exception e) {
                System.err.println("Error procesando aeropuerto " + i + ": " + e.getMessage());
            }
        }

        System.out.println("AirportService.listAirports: Tamaño de la lista filtrada: " + filteredList.size());

        return filteredList;
    }

    /**
     * Cargar aeropuertos iniciales
     */
    public void loadInitialAirports() {
        System.out.println("AirportService.loadInitialAirports: Iniciando carga de aeropuertos");

        // Añadir aeropuertos directamente a la lista para evitar el problema del método add
        try {
            airports.add(new Airport("SJO", "Aeropuerto Internacional Juan Santamaría", "Costa Rica", "active"));
            airports.add(new Airport("LIR", "Aeropuerto Internacional Daniel Oduber", "Costa Rica", "active"));
            airports.add(new Airport("MIA", "Aeropuerto Internacional de Miami", "Estados Unidos", "active"));
            airports.add(new Airport("LAX", "Aeropuerto Internacional de Los Ángeles", "Estados Unidos", "active"));
            airports.add(new Airport("JFK", "Aeropuerto Internacional John F. Kennedy", "Estados Unidos", "active"));
            airports.add(new Airport("MEX", "Aeropuerto Internacional Benito Juárez", "México", "active"));
            airports.add(new Airport("MAD", "Aeropuerto Adolfo Suárez Madrid-Barajas", "España", "active"));
            airports.add(new Airport("FCO", "Aeropuerto Leonardo da Vinci-Fiumicino", "Italia", "active"));
            airports.add(new Airport("CDG", "Aeropuerto Charles de Gaulle", "Francia", "active"));
            airports.add(new Airport("LHR", "Aeropuerto de Londres-Heathrow", "Reino Unido", "inactive"));

            System.out.println("AirportService.loadInitialAirports: Aeropuertos cargados");

            int size = airports.size();
            System.out.println("AirportService.loadInitialAirports: Tamaño final: " + size);
        } catch (Exception e) {
            System.err.println("Error cargando aeropuertos iniciales: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método de diagnóstico para imprimir todos los aeropuertos
     */
    public void printAllAirports() {
        System.out.println("=== IMPRIMIENDO TODOS LOS AEROPUERTOS ===");
        try {
            if (airports.isEmpty()) {
                System.out.println("La lista está vacía. Intentando cargar aeropuertos...");
                loadInitialAirports();
                if (airports.isEmpty()) {
                    System.out.println("La lista sigue vacía después de intentar cargarla.");
                    return;
                }
            }

            int size = airports.size();
            System.out.println("Tamaño de la lista: " + size);

            for (int i = 1; i <= size; i++) {
                try {
                    Airport airport = (Airport) airports.getNode(i).data;
                    System.out.println(i + ": " + airport.getCode() + " - " + airport.getName() + " - " + airport.getStatus());
                } catch (Exception e) {
                    System.err.println("Error al acceder al aeropuerto " + i + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error imprimiendo aeropuertos: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("======================================");
    }
    // Agrega este método a tu clase AirportService

    /**
     * Guarda los aeropuertos en un archivo
     * @return true si se guardó exitosamente, false en caso contrario
     */
    public boolean saveAirports() {
        try {
            System.out.println("AirportService.saveAirports: Iniciando guardado de aeropuertos");

            // Verificar si hay aeropuertos para guardar
            if (airports.isEmpty()) {
                System.out.println("AirportService.saveAirports: Lista vacía, guardando archivo vacío");
            }
            // Crear el contenido JSON manualmente
            StringBuilder json = new StringBuilder();
            json.append("[\n");

            boolean first = true;
            int size = airports.size();
            System.out.println("AirportService.saveAirports: Guardando " + size + " aeropuertos");

            for (int i = 1; i <= size; i++) {
                try {
                    Airport airport = (Airport) airports.getNode(i).data;

                    if (!first) {
                        json.append(",\n");
                    }
                    first = false;

                    json.append("  {\n");
                    json.append("    \"code\": \"").append(airport.getCode()).append("\",\n");
                    json.append("    \"name\": \"").append(airport.getName()).append("\",\n");
                    json.append("    \"country\": \"").append(airport.getCountry()).append("\",\n");
                    json.append("    \"status\": \"").append(airport.getStatus()).append("\"\n");
                    json.append("  }");

                } catch (ListException e) {
                    System.err.println("AirportService.saveAirports: Error accediendo al aeropuerto en índice " + i + ": " + e.getMessage());
                    continue;
                }
            }

            json.append("\n]");
            // Escribir al archivo
            java.io.File file = new java.io.File("data/airports.json");
            file.getParentFile().mkdirs(); // Crear directorio si no existe

            try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
                writer.write(json.toString());
            }

            System.out.println("AirportService.saveAirports: Aeropuertos guardados exitosamente en: " + file.getAbsolutePath());
            return true;

        } catch (Exception e) {
            System.err.println("AirportService.saveAirports: Error guardando aeropuertos: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Carga aeropuertos desde archivo al iniciar el servicio
     */
    public void loadAirports() {
        try {
            java.io.File file = new java.io.File("data/airports.json");
            if (!file.exists()) {
                System.out.println("AirportService.loadAirports: Archivo no existe, usando aeropuertos iniciales");
                return;
            }

            // Leer el archivo
            StringBuilder content = new StringBuilder();
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }

            String jsonContent = content.toString().trim();
            if (jsonContent.isEmpty() || jsonContent.equals("[]")) {
                System.out.println("AirportService.loadAirports: Archivo vacío, usando aeropuertos iniciales");
                return;
            }

            // Limpiar la lista actual
            airports = new SinglyLinkedList<>();

            // Parsear JSON manualmente
            String[] airportBlocks = jsonContent.split("\\},\\s*\\{");

            for (String block : airportBlocks) {
                try {
                    // Limpiar el bloque
                    block = block.replace("[", "").replace("]", "")
                            .replace("{", "").replace("}", "").trim();

                    if (block.isEmpty()) continue;

                    // Extraer campos
                    String code = extractJsonValue(block, "code");
                    String name = extractJsonValue(block, "name");
                    String country = extractJsonValue(block, "country");
                    String status = extractJsonValue(block, "status");

                    // Crear aeropuerto usando tu constructor
                    Airport airport = new Airport(code, name, country, status);

                    // Agregar a la lista
                    airports.add(airport);

                } catch (Exception e) {
                    System.err.println("AirportService.loadAirports: Error procesando aeropuerto: " + e.getMessage());
                }
            }

            System.out.println("AirportService.loadAirports: " + airports.size() + " aeropuertos cargados desde archivo");

        } catch (Exception e) {
            System.err.println("AirportService.loadAirports: Error cargando aeropuertos: " + e.getMessage());
            e.printStackTrace();
            // Si hay error, cargar aeropuertos iniciales
            loadInitialAirports();
        }
    }

    /**
     * Método auxiliar para extraer valores del JSON
     */
    private String extractJsonValue(String jsonBlock, String key) {
        try {
            String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]+)\"";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(jsonBlock);
            if (m.find()) {
                return m.group(1);
            }
        } catch (Exception e) {
            System.err.println("Error extrayendo valor JSON para " + key + ": " + e.getMessage());
        }
        return "";
    }
}