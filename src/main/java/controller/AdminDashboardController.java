package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import security.UserManager;

import java.io.IOException;
import java.util.Objects;

public class AdminDashboardController {

    private UserManager userManager;

    @FXML
    public void initialize() {
        userManager = UserManager.getInstance();

        // Verificar que el usuario es administrador
        if (!userManager.isCurrentUserAdmin()) {
            // Redirigir o mostrar error
            System.err.println("Error: Acceso no autorizado al panel de administrador");
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            // Cerrar sesión
            userManager.logout();

            // Redirigir a la pantalla de login
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/login.fxml")));

            // Obtener el stage actual desde el evento
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root, 400, 300);
            stage.setTitle("Sistema de Gestión de Aeropuertos - Login");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}