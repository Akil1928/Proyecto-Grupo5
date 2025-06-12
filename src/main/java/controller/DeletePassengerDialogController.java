package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class DeletePassengerDialogController {
    @FXML private TextField idField;
    private int idToDelete = -1;
    private boolean deleted = false;

    public int getIdToDelete() { return idToDelete; }
    public boolean isDeleted() { return deleted; }

    @FXML
    private void handleDelete() {
        try {
            idToDelete = Integer.parseInt(idField.getText().trim());
            deleted = true;
            ((Stage) idField.getScene().getWindow()).close();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Cédula inválida").showAndWait();
        }
    }
    @FXML
    private void handleCancel() {
        ((Stage) idField.getScene().getWindow()).close();
    }
}