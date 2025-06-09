package controller;

import domain.Airport;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import services.AirportService;
import datastructure.list.ListException;

public class AirportController {

    @FXML
    private TableView<Airport> airportTable;

    @FXML
    private TableColumn<Airport, String> codeColumn;

    @FXML
    private TableColumn<Airport, String> nameColumn;

    @FXML
    private TableColumn<Airport, String> countryColumn;

    @FXML
    private TableColumn<Airport, String> statusColumn;

    @FXML
    private TextField codeField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField countryField;

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private Button createButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button clearButton;

    private AirportService airportService;
    private ObservableList<Airport> airportData;

    @FXML
    public void initialize() {
        // Inicializar el gestor de aeropuertos
        System.out.println("AirportController.initialize: Iniciando...");
        airportService = AirportService.getInstance();

        // Asegurarnos de que hay aeropuertos cargados
        airportService.printAllAirports();

        // Configurar las columnas de la tabla
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Configurar el ComboBox de estados
        statusComboBox.setItems(FXCollections.observableArrayList("active", "inactive"));
        statusComboBox.getSelectionModel().selectFirst();

        // Configurar el ComboBox de filtros
        filterComboBox.setItems(FXCollections.observableArrayList("All", "Active", "Inactive"));
        filterComboBox.getSelectionModel().selectFirst();

        // Configurar la selección de la tabla
        airportTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                codeField.setText(newSelection.getCode());
                nameField.setText(newSelection.getName());
                countryField.setText(newSelection.getCountry());
                statusComboBox.setValue(newSelection.getStatus());

                codeField.setDisable(true);
                createButton.setDisable(true);
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            }
        });

        // Cargar datos iniciales
        System.out.println("AirportController.initialize: Cargando datos iniciales...");
        try {
            loadAirportData("All");
        } catch (Exception e) {
            System.err.println("Error cargando datos iniciales: " + e.getMessage());
            e.printStackTrace();

            // Intentar nuevamente después de un breve retraso
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    javafx.application.Platform.runLater(() -> {
                        try {
                            loadAirportData("All");
                        } catch (Exception ex) {
                            System.err.println("Error en segundo intento de carga: " + ex.getMessage());
                        }
                    });
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).start();
        }

        // Configurar el filtro
        filterComboBox.setOnAction(event -> {
            String filter = filterComboBox.getValue();
            if ("All".equals(filter)) {
                loadAirportData(null);
            } else if ("Active".equals(filter)) {
                loadAirportData("active");
            } else if ("Inactive".equals(filter)) {
                loadAirportData("inactive");
            }
        });

        System.out.println("AirportController.initialize: Inicialización completada");
    }

    private void loadAirportData(String filter) {
        try {
            // Convertir la lista a ObservableList para la tabla
            airportData = FXCollections.observableArrayList();

            datastructure.list.SinglyLinkedList<Airport> airports = airportService.listAirports(filter);

            if (!airports.isEmpty()) {
                for (int i = 1; i <= airports.size(); i++) {
                    airportData.add((Airport)airports.getNode(i).data);
                }
            }

            airportTable.setItems(airportData);
        } catch (ListException e) {
            showAlert("Error", "Error loading airports: " + e.getMessage());
        }
    }


    @FXML
    private void handleCreateAirport(ActionEvent event) {
        System.out.println("AirportController.handleCreateAirport: Intentando crear aeropuerto...");

        try {
            String code = codeField.getText();
            String name = nameField.getText();
            String country = countryField.getText();
            String status = statusComboBox.getValue();

            if (code.isEmpty() || name.isEmpty() || country.isEmpty() || status == null) {
                showAlert("Error", "Por favor complete todos los campos");
                return;
            }

            System.out.println("Creando aeropuerto: " + code + " - " + name);
            Airport airport = new Airport(code, name, country, status);
            boolean success = airportService.createAirport(airport);

            if (success) {
                showAlert("Éxito", "Aeropuerto creado correctamente");
                clearForm();

                // Recargar los datos en la tabla
                String currentFilter = filterComboBox.getValue();
                if ("All".equals(currentFilter)) {
                    loadAirportData(null);
                } else if ("Active".equals(currentFilter)) {
                    loadAirportData("active");
                } else if ("Inactive".equals(currentFilter)) {
                    loadAirportData("inactive");
                }

                // Imprimir aeropuertos para diagnóstico
                airportService.printAllAirports();
            } else {
                showAlert("Error", "No se pudo crear el aeropuerto. El código podría ya existir.");
            }
        } catch (Exception e) {
            System.err.println("Error al crear aeropuerto: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Error al crear aeropuerto: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateAirport(ActionEvent event) {
        try {
            String code = codeField.getText();
            String name = nameField.getText();
            String country = countryField.getText();
            String status = statusComboBox.getValue();

            if (code.isEmpty()) {
                showAlert("Error", "Please select an airport to update");
                return;
            }

            boolean success = airportService.updateAirport(code, name, country, status);

            if (success) {
                showAlert("Success", "Airport updated successfully");
                clearForm();
                loadAirportData(filterComboBox.getValue().equals("All") ? null : filterComboBox.getValue().toLowerCase());
            } else {
                showAlert("Error", "Failed to update airport");
            }
        } catch (Exception e) {
            showAlert("Error", "Error updating airport: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteAirport(ActionEvent event) {
        try {
            String code = codeField.getText();

            if (code.isEmpty()) {
                showAlert("Error", "Please select an airport to delete");
                return;
            }

            boolean success = airportService.deleteAirport(code);

            if (success) {
                showAlert("Success", "Airport deleted successfully");
                clearForm();
                loadAirportData(filterComboBox.getValue().equals("All") ? null : filterComboBox.getValue().toLowerCase());
            } else {
                showAlert("Error", "Failed to delete airport");
            }
        } catch (Exception e) {
            showAlert("Error", "Error deleting airport: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearForm(ActionEvent event) {
        clearForm();
    }

    @FXML
    private void handleActivateAirport(ActionEvent event) {
        try {
            String code = codeField.getText();

            if (code.isEmpty()) {
                showAlert("Error", "Please select an airport to activate");
                return;
            }

            boolean success = airportService.activateAirport(code);

            if (success) {
                showAlert("Success", "Airport activated successfully");
                clearForm();
                loadAirportData(filterComboBox.getValue().equals("All") ? null : filterComboBox.getValue().toLowerCase());
            } else {
                showAlert("Error", "Failed to activate airport");
            }
        } catch (Exception e) {
            showAlert("Error", "Error activating airport: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeactivateAirport(ActionEvent event) {
        try {
            String code = codeField.getText();

            if (code.isEmpty()) {
                showAlert("Error", "Please select an airport to deactivate");
                return;
            }

            boolean success = airportService.deactivateAirport(code);

            if (success) {
                showAlert("Success", "Airport deactivated successfully");
                clearForm();
                loadAirportData(filterComboBox.getValue().equals("All") ? null : filterComboBox.getValue().toLowerCase());
            } else {
                showAlert("Error", "Failed to deactivate airport");
            }
        } catch (Exception e) {
            showAlert("Error", "Error deactivating airport: " + e.getMessage());
        }
    }

    private void clearForm() {
        codeField.clear();
        nameField.clear();
        countryField.clear();
        statusComboBox.getSelectionModel().selectFirst();

        codeField.setDisable(false);
        createButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);

        airportTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}