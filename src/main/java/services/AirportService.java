package services;

import datastructure.circular.DoublyLinkedList;
import domain.Airport;
import datastructure.list.SinglyLinkedList;
import datastructure.list.ListException;

public class AirportService {
    private static AirportService instance;
    private SinglyLinkedList<Airport> airports;

    // Constructor privado para patrón Singleton
    private AirportService() {
        this.airports = new SinglyLinkedList<>();
        loadInitialAirports(); // Cargar aeropuertos iniciales automáticamente al crear la instancia
    }

    // Método para obtener la instancia única
    public static AirportService getInstance() {
        if (instance == null) {
            instance = new AirportService();
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
            for (int i = 1; i <= airports.size(); i++) {  // Nota: Los índices empiezan en 1
                Airport existingAirport = (Airport) airports.getNode(i).data;
                if (existingAirport.getCode().equals(airport.getCode())) {
                    return false; // Ya existe un aeropuerto con este código
                }
            }

            airports.add(airport);
            return true;
        } catch (ListException e) {
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
            for (int i = 1; i <= airports.size(); i++) {  // Nota: Los índices empiezan en 1
                Airport airport = (Airport) airports.getNode(i).data;
                if (airport.getCode().equals(code)) {
                    if (name != null) {
                        airport.setName(name);
                    }
                    if (country != null) {
                        airport.setCountry(country);
                    }
                    if (status != null) {
                        airport.setStatus(status);
                    }

                    // Actualizar el nodo en la lista
                    airports.getNode(i).data = airport;
                    return true;
                }
            }
            return false;
        } catch (ListException e) {
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
            for (int i = 1; i <= airports.size(); i++) {  // Nota: Los índices empiezan en 1
                Airport airport = (Airport) airports.getNode(i).data;
                if (airport.getCode().equals(code)) {
                    airports.remove(airport);
                    return true;
                }
            }
            return false;
        } catch (ListException e) {
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
        return updateAirport(code, null, null, "active");
    }

    /**
     * Desactivar un aeropuerto
     * @param code Código del aeropuerto a desactivar
     * @return true si se desactivó correctamente, false si no se encontró el aeropuerto
     */
    public boolean deactivateAirport(String code) {
        return updateAirport(code, null, null, "inactive");
    }

    /**
     * Obtener un aeropuerto por su código
     * @param code Código del aeropuerto
     * @return El aeropuerto o null si no se encontró
     */
    public Airport getAirport(String code) {
        try {
            for (int i = 1; i <= airports.size(); i++) {  // Nota: Los índices empiezan en 1
                Airport airport = (Airport) airports.getNode(i).data;
                if (airport.getCode().equals(code)) {
                    return airport;
                }
            }
            return null;
        } catch (ListException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Listar todos los aeropuertos
     * @param filter "active" para solo activos, "inactive" para solo inactivos, cualquier otro valor para todos
     * @return Lista de aeropuertos según el filtro
     */
    public SinglyLinkedList<Airport> listAirports(String filter) {
        SinglyLinkedList<Airport> filteredList = new SinglyLinkedList<>();

        try {
            for (int i = 1; i <= airports.size(); i++) {  // Nota: Los índices empiezan en 1
                Airport airport = (Airport) airports.getNode(i).data;

                // Aplicar filtro
                if (filter == null || filter.isEmpty()) {
                    filteredList.add(airport); // Listar todos
                } else if (filter.equals("active") && airport.getStatus().equals("active")) {
                    filteredList.add(airport); // Solo activos
                } else if (filter.equals("inactive") && airport.getStatus().equals("inactive")) {
                    filteredList.add(airport); // Solo inactivos
                }
            }
        } catch (ListException e) {
            e.printStackTrace();
            // Manejar excepción
        }

        return filteredList;
    }

    /**
     * Cargar aeropuertos iniciales desde un archivo
     */
    public void loadInitialAirports() {
        // Aeropuertos de ejemplo
        createAirport(new Airport("SJO", "Aeropuerto Internacional Juan Santamaría", "Costa Rica", "active"));
        createAirport(new Airport("LIR", "Aeropuerto Internacional Daniel Oduber", "Costa Rica", "active"));
        createAirport(new Airport("MIA", "Aeropuerto Internacional de Miami", "Estados Unidos", "active"));
        createAirport(new Airport("LAX", "Aeropuerto Internacional de Los Ángeles", "Estados Unidos", "active"));
        createAirport(new Airport("JFK", "Aeropuerto Internacional John F. Kennedy", "Estados Unidos", "active"));
        createAirport(new Airport("MEX", "Aeropuerto Internacional Benito Juárez", "México", "active"));
        createAirport(new Airport("MAD", "Aeropuerto Adolfo Suárez Madrid-Barajas", "España", "active"));
        createAirport(new Airport("FCO", "Aeropuerto Leonardo da Vinci-Fiumicino", "Italia", "active"));
        createAirport(new Airport("CDG", "Aeropuerto Charles de Gaulle", "Francia", "active"));
        createAirport(new Airport("LHR", "Aeropuerto de Londres-Heathrow", "Reino Unido", "inactive"));
    }
}