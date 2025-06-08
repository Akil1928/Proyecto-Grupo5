package controller;

import domain.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import security.UserManager;

import java.io.IOException;
import java.util.Objects;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    private UserManager userManager;

    @FXML
    public void initialize() {
        userManager = UserManager.getInstance();
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        System.out.println("Botón de login presionado"); // Log para depuración

        String username = usernameField.getText();
        String password = passwordField.getText();

        System.out.println("Usuario: " + username); // Log para depuración
        System.out.println("Contraseña: " + password); // Log para depuración

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Campos vacíos", "Por favor ingrese usuario y contraseña.");
            return;
        }

        User authenticatedUser = userManager.authenticate(username, password);
        System.out.println("Usuario autenticado: " + (authenticatedUser != null ? authenticatedUser.getName() : "null")); // Log para depuración

        if (authenticatedUser != null) {
            try {
                // Cargar la pantalla principal según el rol del usuario
                String fxmlFile = userManager.isCurrentUserAdmin() ?
                        "/dashboard.fxml" :
                        "/userdashboard.fxml";

                System.out.println("Intentando cargar: " + fxmlFile); // Log para depuración

                // Usamos getClass().getResource() para cargar desde el classpath
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlFile)));

                // Obtener la ventana actual
                Stage stage = (Stage) loginButton.getScene().getWindow();

                // Configurar la nueva escena
                Scene scene = new Scene(root);
                stage.setScene(scene);

                // Actualizar el título de la ventana según el rol
                stage.setTitle(userManager.isCurrentUserAdmin() ?
                        "Panel de Administrador" : "Panel de Usuario");

                stage.setResizable(true);
                stage.show();

                System.out.println("Vista cargada con éxito"); // Log para depuración
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error al cargar la vista: " + e.getMessage()); // Log para depuración
                showAlert("Error", "Error de navegación",
                        "No se pudo cargar la siguiente pantalla: " + e.getMessage());
            }
        } else {
            showAlert("Error de Autenticación", "Credenciales inválidas",
                    "Usuario o contraseña incorrectos.");
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}