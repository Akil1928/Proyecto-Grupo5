package controller;

import domain.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import security.UserManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class UserDashboardController {

    @FXML
    private BorderPane mainPane;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label lastUpdateLabel;

    @FXML
    private Label activeFlightsLabel;

    @FXML
    private Label onlineUsersLabel;

    private UserManager userManager;
    private Timer statusUpdateTimer;

    @FXML
    public void initialize() {
        System.out.println("UserDashboardController: Initializing...");
        userManager = UserManager.getInstance();

        // Configurar información del usuario
        setupUserInfo();

        // Inicializar estadísticas de la barra de estado
        initializeStatusBar();

        // Iniciar timer para actualizar estadísticas
        startStatusUpdater();

        System.out.println("UserDashboardController: Initialization complete");
    }

    private void setupUserInfo() {
        User currentUser = userManager.getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText("¡Bienvenido de vuelta, " + currentUser.getName() + "!");
            System.out.println("Current user: " + currentUser.getName() + " (Role: " + currentUser.getRole() + ")");
        } else {
            welcomeLabel.setText("¡Bienvenido al sistema!");
            System.out.println("WARNING: No current user found!");
        }
    }

    private void initializeStatusBar() {
        // Inicializar labels si existen en el FXML
        if (lastUpdateLabel != null) {
            updateLastUpdateTime();
        }
        if (activeFlightsLabel != null) {
            activeFlightsLabel.setText("1,247");
        }
        if (onlineUsersLabel != null) {
            onlineUsersLabel.setText("18,293");
        }
    }

    private void startStatusUpdater() {
        statusUpdateTimer = new Timer(true);
        statusUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                javafx.application.Platform.runLater(() -> {
                    updateLastUpdateTime();
                    // Aquí podrías actualizar otras estadísticas dinámicamente
                });
            }
        }, 0, 60000); // Actualizar cada minuto
    }

    private void updateLastUpdateTime() {
        if (lastUpdateLabel != null) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            lastUpdateLabel.setText("Actualizado: " + now.format(formatter));
        }
    }

    @FXML
    public void handleViewAirports(ActionEvent event) {
        System.out.println("Loading airport view for user...");
        loadView("/userAirportView.fxml", "vista de aeropuertos");
    }

    @FXML
    public void handleViewFlights(ActionEvent event) {
        System.out.println("View flights menu item clicked");
        loadView("/userFlightView.fxml", "vista de vuelos");
    }

    @FXML
    public void handleBuyTickets(ActionEvent event) {
        System.out.println("Buy tickets menu item clicked");
        loadView("/ticketPurchase.fxml", "compra de boletos");
    }

    @FXML
    public void handleMyProfile(ActionEvent event) {
        System.out.println("My profile menu item clicked");
        loadView("/userProfile.fxml", "perfil de usuario");
    }

    @FXML
    public void handleMyFlights(ActionEvent event) {
        System.out.println("My flights menu item clicked");
        loadView("/MyFlights.fxml", "mis vuelos");
    }

    @FXML
    public void handleMyTickets(ActionEvent event) {
        System.out.println("My tickets menu item clicked");
        loadView("/userTickets.fxml", "mis boletos");
    }

    @FXML
    public void handleAirportMap(ActionEvent event) {
        System.out.println("Airport map menu item clicked");
        loadView("/airportMap.fxml", "mapa de aeropuertos");
    }

    @FXML
    public void handleSchedules(ActionEvent event) {
        System.out.println("Schedules menu item clicked");
        loadView("/flightSchedules.fxml", "horarios de vuelos");
    }

    @FXML
    public void handleSettings(ActionEvent event) {
        System.out.println("Settings menu item clicked");
        loadView("/userSettings.fxml", "configuración");
    }

    /**
     * Método helper para cargar vistas de manera consistente
     */
    private void loadView(String fxmlPath, String viewName) {
        try {
            Parent view = FXMLLoader.load(Objects.requireNonNull(
                    getClass().getResource(fxmlPath)));
            mainPane.setCenter(view);
            System.out.println(viewName + " loaded successfully");
        } catch (IOException e) {
            System.err.println("Error loading " + viewName + ": " + e.getMessage());
            showErrorAlert("Error de Carga",
                    "No se pudo cargar " + viewName + ".",
                    "La funcionalidad estará disponible próximamente.");
        } catch (NullPointerException e) {
            System.err.println("FXML file not found: " + fxmlPath);
            showInfoAlert("Función en Desarrollo",
                    "La función '" + viewName + "' está en desarrollo.",
                    "Estará disponible en una próxima actualización.");
        } catch (Exception e) {
            System.err.println("Unexpected error loading " + viewName + ": " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Error Inesperado",
                    "Ocurrió un error inesperado.",
                    "Por favor, inténtelo de nuevo más tarde.");
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        System.out.println("Logout menu item clicked");
        try {
            // Detener timer de actualizaciones
            if (statusUpdateTimer != null) {
                statusUpdateTimer.cancel();
            }

            // Cerrar sesión
            userManager.logout();
            System.out.println("User logged out successfully");

            // Redirigir a la pantalla de login
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/login.fxml")));

            // Obtener el stage actual
            Stage stage = (Stage) mainPane.getScene().getWindow();

            Scene scene = new Scene(root, 500, 400); // Tamaño mejorado para login
            stage.setTitle("Sistema de Gestión de Aeropuertos - Iniciar Sesión");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error during logout: " + e.getMessage());
            showErrorAlert("Error de Logout",
                    "No se pudo cerrar sesión correctamente.",
                    "Por favor, cierre la aplicación manualmente.");
        }
    }

    /**
     * Muestra un alert de error al usuario
     */
    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Muestra un alert informativo al usuario
     */
    private void showInfoAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Cleanup cuando se cierra el controller
     */
    public void cleanup() {
        if (statusUpdateTimer != null) {
            statusUpdateTimer.cancel();
        }
    }
}