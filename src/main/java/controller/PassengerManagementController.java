package controller;

import datastructure.tree.AVLTree;
import datastructure.tree.BTreeNode;
import domain.Passenger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import persistence.PassengerDataLoader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PassengerManagementController {

    @FXML private TableView<Passenger> tablePassengers;
    @FXML private TableColumn<Passenger, Integer> colId;
    @FXML private TableColumn<Passenger, String> colName;
    @FXML private TableColumn<Passenger, String> colNationality;

    private AVLTree passengerTree;
    private ObservableList<Passenger> observablePassengers;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getId()).asObject());
        colName.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getName()));
        colNationality.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getNationality()));
        loadPassengersToTable();
    }

    private void loadPassengersToTable() {
        passengerTree = PassengerDataLoader.loadPassengers();
        observablePassengers = FXCollections.observableArrayList();
        List<Object> inOrder = new ArrayList<>();
        passengerTree.inOrderList(inOrder);
        for (Object obj : inOrder) {
            observablePassengers.add((Passenger) obj);
        }
        tablePassengers.setItems(observablePassengers);
    }

    @FXML
    private void handleAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/addPassenger.fxml"));
            Parent root = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Agregar Pasajero");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            AddPassengerDialogController controller = loader.getController();
            dialogStage.showAndWait();
            if (controller.isSaved()) {
                Passenger newP = controller.getPassenger();
                passengerTree.add(newP);
                observablePassengers.add(newP);
                tablePassengers.refresh();
                // PassengerDataLoader.savePassengers(passengerTree); // Solo guarda si quieres guardar inmediato
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/deletePassenger.fxml"));
            Parent root = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Eliminar Pasajero");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            DeletePassengerDialogController controller = loader.getController();
            dialogStage.showAndWait();
            if (controller.isDeleted()) {
                int id = controller.getIdToDelete();
                Passenger toDelete = null;
                for (Passenger p : observablePassengers) {
                    if (p.getId() == id) {
                        toDelete = p;
                        break;
                    }
                }
                if (toDelete != null) {
                    passengerTree.remove(toDelete);
                    observablePassengers.remove(toDelete);
                    tablePassengers.refresh();
                    // PassengerDataLoader.savePassengers(passengerTree); // Solo guarda si quieres guardar inmediato
                } else {
                    new Alert(Alert.AlertType.WARNING, "No se encontró pasajero con esa cédula.").showAndWait();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void historialOnAction() {
        // Aquí va tu lógica de historial (si la implementas)
    }

    @FXML
    public void guardarCambiosOnAction(ActionEvent event) {
        PassengerDataLoader.savePassengers(passengerTree);
        new Alert(Alert.AlertType.INFORMATION, "¡Cambios guardados correctamente!").showAndWait();
    }

    @FXML
    public void regresarOnAction(ActionEvent event) {
        try {
            Parent dashboard = FXMLLoader.load(getClass().getResource("/dashboard.fxml"));
            BorderPane root = (BorderPane) ((Button)event.getSource()).getScene().getRoot();
            root.setCenter(dashboard);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Si necesitas buscar pasajero por id en el árbol:
    private Passenger findPassengerById(int id) {
        return findPassengerById(passengerTree.getRoot(), id);
    }
    private Passenger findPassengerById(BTreeNode node, int id) {
        if (node == null) return null;
        Passenger p = (Passenger) node.data;
        if (p.getId() == id) return p;
        Passenger found = findPassengerById(node.left, id);
        if (found != null) return found;
        return findPassengerById(node.right, id);
    }
}