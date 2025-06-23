package controller;

import domain.Airport;
import domain.Flight;
import domain.Passenger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;
import services.AirportService;
import services.FlightService;
import services.PersonService;
import services.QueueService;
import datastructure.list.ListException;
import datastructure.list.SinglyLinkedList;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class PurchaseController implements Initializable {

    @FXML private TableView<Flight> availableFlightsTable;
    @FXML private TableColumn<Flight, Integer> flightNumberColumn;
    @FXML private TableColumn<Flight, String> originColumn;
    @FXML private TableColumn<Flight, String> destinationColumn;
    @FXML private TableColumn<Flight, LocalDateTime> departureTimeColumn;
    @FXML private TableColumn<Flight, Integer> capacityColumn;
    @FXML private TableColumn<Flight, Integer> availableSeatsColumn;

    @FXML private ComboBox<Passenger> passengerComboBox;
    @FXML private Button purchaseButton;
    @FXML private Button processQueueButton;
    @FXML private Button viewQueueButton;
    @FXML private TextArea queueStatusArea;
    @FXML private Label statusLabel;

    private FlightService flightService;
    private PersonService personService;
    private AirportService airportService;
    private QueueService queueService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicializar servicios
        flightService = FlightService.getInstance();
        personService = PersonService.getInstance();
        airportService = AirportService.getInstance();
        queueService = QueueService.getInstance();

        // Configurar columnas de la tabla
        setupTableColumns();

        // Cargar datos iniciales
        loadPassengers();
        loadAvailableFlights();

        // Configurar listeners
        setupEventListeners();

        // Actualizar estado inicial
        updateStatus("Listo para comprar tiquetes");
    }

    private void setupTableColumns() {
        flightNumberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        originColumn.setCellValueFactory(new PropertyValueFactory<>("origin"));
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        departureTimeColumn.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));

        // Columna calculada para asientos disponibles
        availableSeatsColumn.setCellValueFactory(cellData -> {
            Flight flight = cellData.getValue();
            int available = flight.getCapacity() - flight.getPassengers().size();
            return new javafx.beans.property.SimpleIntegerProperty(available).asObject();
        });
    }

    private void loadPassengers() {
        try {
            SinglyLinkedList<Passenger> passengers = personService.getAllPassengers();
            passengerComboBox.getItems().clear();

            if (passengers != null && !passengers.isEmpty()) {
                for (int i = 0; i < passengers.size(); i++) {
                    Passenger passenger = passengers.get(i);
                    passengerComboBox.getItems().add(passenger);
                }

                // Configurar cómo se muestran los pasajeros
                passengerComboBox.setConverter(new StringConverter<Passenger>() {
                    @Override
                    public String toString(Passenger passenger) {
                        if (passenger == null) return "";
                        return passenger.getId() + " - " + passenger.getName();
                    }

                    @Override
                    public Passenger fromString(String string) {
                        return null;
                    }
                });

                if (!passengerComboBox.getItems().isEmpty()) {
                    passengerComboBox.getSelectionModel().selectFirst();
                }
            }
        } catch (Exception e) {
            showAlert("Error", "No se pudieron cargar los pasajeros: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void loadAvailableFlights() {
        try {
            List<Flight> activeFlights = flightService.getActiveFlights();
            availableFlightsTable.getItems().clear();
            availableFlightsTable.getItems().addAll(activeFlights);
        } catch (Exception e) {
            showAlert("Error", "No se pudieron cargar los vuelos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupEventListeners() {
        // Listener para selección de vuelo
        availableFlightsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateQueueStatus(newSelection);
            }
        });
    }

    @FXML
    private void handlePurchaseTicket(ActionEvent event) {
        Flight selectedFlight = availableFlightsTable.getSelectionModel().getSelectedItem();
        Passenger selectedPassenger = passengerComboBox.getSelectionModel().getSelectedItem();

        if (selectedFlight == null) {
            showAlert("Error", "Debe seleccionar un vuelo", Alert.AlertType.ERROR);
            return;
        }

        if (selectedPassenger == null) {
            showAlert("Error", "Debe seleccionar un pasajero", Alert.AlertType.ERROR);
            return;
        }

        try {
            // Verificar si el pasajero ya está en el vuelo
            if (isPassengerInFlight(selectedFlight, selectedPassenger)) {
                showAlert("Información", "El pasajero ya está registrado en este vuelo", Alert.AlertType.INFORMATION);
                return;
            }

            // Intentar comprar el tiquete
            boolean success = flightService.addPassengerToFlight(
                    selectedFlight.getNumber(),
                    selectedPassenger
            );

            if (success) {
                showAlert("Éxito",
                        "Tiquete comprado exitosamente!\n" +
                                "Pasajero: " + selectedPassenger.getName() + "\n" +
                                "Vuelo: " + selectedFlight.getNumber(),
                        Alert.AlertType.INFORMATION);

                updateStatus("Tiquete comprado para " + selectedPassenger.getName());
            } else {
                showAlert("Información",
                        "Vuelo completo. Pasajero agregado a la lista de espera.\n" +
                                "Pasajero: " + selectedPassenger.getName() + "\n" +
                                "Vuelo: " + selectedFlight.getNumber(),
                        Alert.AlertType.INFORMATION);

                updateStatus("Pasajero agregado a lista de espera");
            }

            // Refrescar datos
            refreshData();

        } catch (Exception e) {
            showAlert("Error", "Error al procesar la compra: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleProcessQueue(ActionEvent event) {
        Flight selectedFlight = availableFlightsTable.getSelectionModel().getSelectedItem();

        if (selectedFlight == null) {
            showAlert("Error", "Debe seleccionar un vuelo", Alert.AlertType.ERROR);
            return;
        }

        try {
            int processed = queueService.processQueue(selectedFlight);

            if (processed > 0) {
                showAlert("Éxito",
                        "Se procesaron " + processed + " pasajero(s) de la lista de espera",
                        Alert.AlertType.INFORMATION);

                updateStatus("Procesados " + processed + " pasajeros de la cola");
                refreshData();
            } else {
                showAlert("Información",
                        "No hay pasajeros en lista de espera o el vuelo está completo",
                        Alert.AlertType.INFORMATION);
            }

        } catch (Exception e) {
            showAlert("Error", "Error al procesar la cola: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleViewQueue(ActionEvent event) {
        Flight selectedFlight = availableFlightsTable.getSelectionModel().getSelectedItem();

        if (selectedFlight == null) {
            showAlert("Error", "Debe seleccionar un vuelo", Alert.AlertType.ERROR);
            return;
        }

        updateQueueStatus(selectedFlight);
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        refreshData();
        updateStatus("Datos actualizados");
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent dashboard = FXMLLoader.load(getClass().getResource("/userDashboard.fxml"));
            BorderPane root = (BorderPane) ((Button) event.getSource()).getScene().getRoot();
            root.setCenter(dashboard);
        } catch (IOException e) {
            showAlert("Error", "No se pudo regresar al dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateQueueStatus(Flight flight) {
        try {
            String queueInfo = queueService.getQueueStatus(flight);
            queueStatusArea.setText(queueInfo);
        } catch (Exception e) {
            queueStatusArea.setText("Error al obtener estado de la cola: " + e.getMessage());
        }
    }

    private boolean isPassengerInFlight(Flight flight, Passenger passenger) {
        try {
            List<Passenger> passengers = flight.getPassengers();
            return passengers.stream().anyMatch(p -> p.getId() == passenger.getId());
        } catch (Exception e) {
            System.err.println("Error verificando pasajero en vuelo: " + e.getMessage());
            return false;
        }
    }

    private void refreshData() {
        loadAvailableFlights();
        Flight selectedFlight = availableFlightsTable.getSelectionModel().getSelectedItem();
        if (selectedFlight != null) {
            updateQueueStatus(selectedFlight);
        }
    }

    private void updateStatus(String message) {
        if (statusLabel != null) {
            Platform.runLater(() -> {
                statusLabel.setText(message + " - " +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            });
        }
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}