package controller;

import domain.Passenger;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddPassengerDialogController {
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField nationalityField;

    private Passenger newPassenger;
    private boolean saved = false;

    public Passenger getPassenger() { return newPassenger; }
    public boolean isSaved() { return saved; }

    @FXML
    private void handleSave() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            String name = nameField.getText().trim();
            String nationality = nationalityField.getText().trim();
            if (name.isEmpty() || nationality.isEmpty()) {
                throw new IllegalArgumentException("Nombre y nacionalidad son obligatorios");
            }
            newPassenger = new Passenger(id, name, nationality, null);
            saved = true;
            ((Stage) idField.getScene().getWindow()).close();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Datos inválidos: " + e.getMessage()).showAndWait();
        }
    }
    @FXML
    private void handleCancel() {
        ((Stage) idField.getScene().getWindow()).close();
    }
}
