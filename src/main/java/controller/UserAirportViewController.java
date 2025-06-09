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
import security.UserManager;

public class UserAirportViewController {

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
    private ComboBox<String> filterComboBox;

    private AirportService airportService;
    private UserManager userManager;
    private ObservableList<Airport> airportData;

    @FXML
    public void initialize() {
        System.out.println("UserAirportViewController: Initializing...");

        // Inicializar managers
        airportService = AirportService.getInstance();
        userManager = UserManager.getInstance();

        // Configurar las columnas de la tabla
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Configurar el ComboBox de filtros
        filterComboBox.setItems(FXCollections.observableArrayList("Todos", "Activos", "Inactivos"));
        filterComboBox.getSelectionModel().selectFirst();

        // Cargar datos iniciales (por defecto mostrar aeropuertos activos)
        loadAirportData("active");

        // Configurar el filtro
        filterComboBox.setOnAction(event -> {
            String selectedFilter = filterComboBox.getValue();
            System.out.println("Filter selected: " + selectedFilter);

            String filter = null;
            if ("Todos".equals(selectedFilter)) {
                filter = null;
            } else if ("Activos".equals(selectedFilter)) {
                filter = "active";
            } else if ("Inactivos".equals(selectedFilter)) {
                filter = "inactive";
            }

            loadAirportData(filter);
        });

        System.out.println("UserAirportViewController: Initialization complete");
    }

    private void loadAirportData(String filter) {
        try {
            System.out.println("Loading airport data with filter: " + filter);

            // Convertir la lista a ObservableList para la tabla
            airportData = FXCollections.observableArrayList();

            datastructure.list.SinglyLinkedList<Airport> airports = airportService.listAirports(filter);

            if (!airports.isEmpty()) {
                for (int i = 1; i <= airports.size(); i++) {
                    airportData.add((Airport)airports.getNode(i).data);
                }
                System.out.println("Loaded " + airportData.size() + " airports");
            } else {
                System.out.println("No airports found with filter: " + filter);
            }

            airportTable.setItems(airportData);
        } catch (ListException e) {
            System.err.println("Error loading airports: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Error al cargar aeropuertos", e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Error inesperado", e.getMessage());
        }
    }

    @FXML
    private void handleViewFlights(ActionEvent event) {
        System.out.println("View Flights button clicked");
        Airport selectedAirport = airportTable.getSelectionModel().getSelectedItem();
        if (selectedAirport != null) {
            showAlert("Información", "Ver vuelos",
                    "Esta funcionalidad está en desarrollo. Se mostrarían los vuelos desde "
                            + selectedAirport.getName());
        } else {
            showAlert("Advertencia", "Selección vacía",
                    "Por favor, seleccione un aeropuerto primero.");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        System.out.println("Back button clicked");
        // Esta función no hace nada porque la vista se muestra dentro del BorderPane principal
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}