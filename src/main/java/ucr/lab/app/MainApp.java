package ucr.lab.app; // This is the package of MainApp

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects; // Make sure this import is present

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Load login.fxml from the root of resources
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/login.fxml")));

        Scene scene = new Scene(root, 800, 600); // Adjust width and height as needed

        // Apply the CSS file from the root of resources
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/stylesheet.css")).toExternalForm());

        stage.setTitle("Sistema de Gestión de Aeropuertos - Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}