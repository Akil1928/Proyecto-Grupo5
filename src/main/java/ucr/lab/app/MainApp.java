package ucr.lab.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.FlightService;
import services.AirportService;
import services.PersonService;

import java.util.Objects;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Load login.fxml from the root of resources
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/login.fxml")));

        Scene scene = new Scene(root, 800, 600); // Adjust width and height as needed


        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/stylesheet.css")).toExternalForm());

        stage.setTitle("Sistema de Gestión de Aeropuertos - Login");
        stage.setScene(scene);

        //guardado automático
        setupAutoSave(stage);

        stage.show();
    }

    private void setupAutoSave(Stage primaryStage) {
        // Guardado cuando se cierra la ventana
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Cerrando aplicación - Guardando datos...");
            saveAllApplicationData();
            Platform.exit();
            System.exit(0);
        });

        // Guardado al cerrar la aplicación inesperadamente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown hook ejecutado - Guardando datos...");
            saveAllApplicationData();
        }));
    }

    /**
     * Método para guardar todos los datos de la aplicación
     */
    private void saveAllApplicationData() {
        System.out.println("Guardando todos los datos de la aplicación...");

        try {
            // Guardar vuelos
            try {
                FlightService flightService = FlightService.getInstance();
                if (flightService != null) {
                    // Verificar si el método saveFlights existe
                    try {
                        flightService.getClass().getMethod("saveFlights");
                        // Si existe, llamarlo usando reflexión
                        Boolean result = (Boolean) flightService.getClass().getMethod("saveFlights").invoke(flightService);
                        if (result != null && result) {
                            System.out.println("✓ Vuelos guardados exitosamente");
                        }
                    } catch (NoSuchMethodException e) {
                        System.out.println("Método saveFlights no encontrado en FlightService");
                    }
                }
            } catch (Exception e) {
                System.out.println("No se pudo guardar vuelos: " + e.getMessage());
            }
                // Guardar pasajeros
            try {
                PersonService personService = PersonService.getInstance();
                if (personService != null) {
                    try {
                        personService.savePassengers();
                        System.out.println("✓ Pasajeros guardados exitosamente");
                    } catch (Exception e) {
                        System.out.println("Error al guardar pasajeros: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                System.out.println("No se pudo obtener instancia de PersonService: " + e.getMessage());
            }
            // Guardar aeropuertos
            try {
                AirportService airportService = AirportService.getInstance();
                if (airportService != null) {
                    // Verificar si el método saveAirports existe
                    try {
                        airportService.getClass().getMethod("saveAirports");
                        // Si existe, llamarlo usando reflexión
                        Boolean result = (Boolean) airportService.getClass().getMethod("saveAirports").invoke(airportService);
                        if (result != null && result) {
                            System.out.println("✓ Aeropuertos guardados exitosamente");
                        }
                    } catch (NoSuchMethodException e) {
                        System.out.println("Método saveAirports no encontrado en AirportService");
                    }
                }
            } catch (Exception e) {
                System.out.println("No se pudo guardar aeropuertos: " + e.getMessage());
            }

            System.out.println("Proceso de guardado completado");

        } catch (Exception e) {
            System.err.println("Error general al guardar datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}