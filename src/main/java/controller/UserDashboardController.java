package controller;

import domain.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import security.UserManager;

import java.io.IOException;
import java.util.Objects;

public class UserDashboardController {

    @FXML
    private BorderPane mainPane;

    @FXML
    private Label welcomeLabel;

    private UserManager userManager;

    @FXML
    public void initialize() {
        System.out.println("UserDashboardController: Initializing...");
        userManager = UserManager.getInstance();

        User currentUser = userManager.getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText("Bienvenido, " + currentUser.getName());
            System.out.println("Current user: " + currentUser.getName() + " (Role: " + currentUser.getRole() + ")");
        } else {
            System.out.println("WARNING: No current user found!");
        }

        System.out.println("UserDashboardController: Initialization complete");
    }

    @FXML
    public void handleViewAirports(ActionEvent event) {
        System.out.println("Loading airport view for user...");
        try {
            // Cargar la vista de visualización de aeropuertos para usuario
            Parent airportView = FXMLLoader.load(Objects.requireNonNull(
                    getClass().getResource("/userAirportView.fxml")));

            mainPane.setCenter(airportView);
            System.out.println("Airport view loaded successfully");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading airport view: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    @FXML
    public void handleViewFlights(ActionEvent event) {
        System.out.println("View flights menu item clicked");
        // Implementación pendiente
    }

    @FXML
    public void handleBuyTickets(ActionEvent event) {
        System.out.println("Buy tickets menu item clicked");
        // Implementación pendiente
    }

    @FXML
    public void handleMyProfile(ActionEvent event) {
        System.out.println("My profile menu item clicked");
        // Implementación pendiente
    }

    @FXML
    public void handleMyFlights(ActionEvent event) {
        System.out.println("My flights menu item clicked");
        // Implementación pendiente
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        System.out.println("Logout menu item clicked");
        try {
            // Cerrar sesión
            userManager.logout();
            System.out.println("User logged out successfully");

            // Redirigir a la pantalla de login
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/login.fxml")));

            // Obtener el stage actual desde el evento
            Stage stage = (Stage) mainPane.getScene().getWindow();

            Scene scene = new Scene(root, 400, 300);
            stage.setTitle("Sistema de Gestión de Aeropuertos - Login");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error during logout: " + e.getMessage());
        }
    }
}