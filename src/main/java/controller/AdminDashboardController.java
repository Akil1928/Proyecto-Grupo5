package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import security.UserManager;

import java.io.IOException;
import java.util.Objects;

public class AdminDashboardController {

    @FXML
    private BorderPane mainPane;

    private UserManager userManager;

    @FXML
    public void initialize() {
        userManager = UserManager.getInstance();
        if (!userManager.isCurrentUserAdmin()) {
            System.err.println("Error: Acceso no autorizado al panel de administrador");
        }
    }

    @FXML
    public void handleManageAirports(ActionEvent event) {
        try {
            Parent airportView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/airport.fxml")));
            mainPane.setCenter(airportView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleGenerateReports(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reportView.fxml"));
            Parent reportView = loader.load();

            Stage reportStage = new Stage();
            reportStage.setTitle("Generador de Reportes");
            reportStage.setScene(new Scene(reportView, 500, 400));

            // Pasar la referencia del stage al controlador
            ReportController controller = loader.getController();
            controller.setStage(reportStage);

            reportStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "No se pudo abrir el generador de reportes", e.getMessage());
        }
    }

    // Método para mostrar alertas de error
    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void accederPasajeroOnAction(ActionEvent event) {
        // Nota: El método ahora coincide con el onAction del botón en el FXML: "#accederPasajeroOnAction"
        try {
            Parent passengerView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/passenger.fxml")));
            mainPane.setCenter(passengerView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            userManager.logout();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/login.fxml")));
            Stage stage = (Stage) mainPane.getScene().getWindow();
            Scene scene = new Scene(root, 400, 300);
            stage.setTitle("Sistema de Gestión de Aeropuertos - Login");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void accederVuelosOnAction(ActionEvent event) {
        try {
            Parent passengerView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/flight.fxml")));
            mainPane.setCenter(passengerView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Este método permite que AirportController y PassengerManagementController lo llamen para regresar
    public static void recargarDashboard(ActionEvent event) {
        try {
            Parent dashboard = FXMLLoader.load(AdminDashboardController.class.getResource("/admin_dashboard.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(dashboard, 1000, 700));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void rutasOnAction(ActionEvent event) {
        try {
            Parent routeView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/routes.fxml")));
            mainPane.setCenter(routeView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}