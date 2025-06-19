package controller;

import datastructure.list.ListException;
import datastructure.list.SinglyLinkedList;
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
import services.PersonService;

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
        System.out.println("PassengerManagementController: Cargando pasajeros...");

        PersonService personService = PersonService.getInstance();
        SinglyLinkedList<Passenger> allPassengers = personService.getAllPassengers();

        observablePassengers = FXCollections.observableArrayList();
        for (int i = 0; i < allPassengers.size(); i++) {
            try {
                observablePassengers.add(allPassengers.get(i));
            } catch (ListException e) {
                throw new RuntimeException(e);
            }
        }

        tablePassengers.setItems(observablePassengers);
        System.out.println("PassengerManagementController: " + observablePassengers.size() + " pasajeros cargados");
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

                PersonService.getInstance().addPassenger(newP);

                // Refrescar tabla
                loadPassengersToTable();

                System.out.println("PassengerManagementController: Pasajero guardado automáticamente - " + newP.getName());

                // Mostrar confirmación
                new Alert(Alert.AlertType.INFORMATION,
                        "Pasajero '" + newP.getName() + "' agregado y guardado exitosamente!").showAndWait();
            }
        } catch (IOException e) {
            System.err.println("PassengerManagementController: Error al agregar pasajero - " + e.getMessage());
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

                    PersonService.getInstance().removePassenger(toDelete.getId());

                    // Refrescar tabla
                    loadPassengersToTable();

                    System.out.println("PassengerManagementController: Pasajero eliminado automáticamente - " + toDelete.getName());

                    // Mostrar confirmación
                    new Alert(Alert.AlertType.INFORMATION,
                            "Pasajero '" + toDelete.getName() + "' eliminado y guardado exitosamente!").showAndWait();
                } else {
                    new Alert(Alert.AlertType.WARNING, "No se encontró pasajero con esa cédula.").showAndWait();
                }
            }
        } catch (IOException e) {
            System.err.println("PassengerManagementController: Error al eliminar pasajero - " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void historialOnAction() {
        // Aquí va tu lógica de historial (si la implementas)
        System.out.println("PassengerManagementController: Función historial pendiente de implementar");
    }

    @FXML
    public void guardarCambiosOnAction(ActionEvent event) {
        System.out.println("PassengerManagementController: Guardado manual solicitado");
        PersonService.getInstance().savePassengers();
        new Alert(Alert.AlertType.INFORMATION,
                "¡" + observablePassengers.size() + " pasajeros guardados correctamente!").showAndWait();
    }

    @FXML
    public void regresarOnAction(ActionEvent event) {
        try {
            System.out.println("PassengerManagementController: Regresando al dashboard");
            Parent dashboard = FXMLLoader.load(getClass().getResource("/dashboard.fxml"));
            BorderPane root = (BorderPane) ((Button)event.getSource()).getScene().getRoot();
            root.setCenter(dashboard);
        } catch (IOException e) {
            System.err.println("PassengerManagementController: Error al regresar - " + e.getMessage());
            e.printStackTrace();
        }
    }

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