package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;
import domain.Flight;
import domain.Passenger;
import services.FlightService;
import services.PersonService;
import datastructure.list.SinglyLinkedList;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MyFlightsController implements Initializable {

    @FXML private ComboBox<Passenger> passengerComboBox;
    @FXML private TableView<Flight> myFlightsTable;
    @FXML private TableColumn<Flight, Integer> flightNumberColumn;
    @FXML private TableColumn<Flight, String> originColumn;
    @FXML private TableColumn<Flight, String> destinationColumn;
    @FXML private TableColumn<Flight, LocalDateTime> departureTimeColumn;
    @FXML private TableColumn<Flight, String> statusColumn;
    @FXML private Label passengerInfoLabel;
    @FXML private Label flightCountLabel;
    @FXML private TextArea flightHistoryTextArea;
    @FXML private Button viewDetailsButton;
    @FXML private Button cancelFlightButton;

    private FlightService flightService = FlightService.getInstance();
    private PersonService personService = PersonService.getInstance();

    private Passenger selectedPassenger;
    private Flight selectedFlight;
    private ObservableList<Flight> passengerFlights = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadPassengers();
        setupEventListeners();

        // Deshabilitar botones inicialmente
        viewDetailsButton.setDisable(true);
        cancelFlightButton.setDisable(true);
    }

    private void setupTableColumns() {
        flightNumberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        originColumn.setCellValueFactory(new PropertyValueFactory<>("origin"));
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        departureTimeColumn.setCellValueFactory(new PropertyValueFactory<>("departureTime"));

        // Columna de estado
        statusColumn.setCellValueFactory(cellData -> {
            Flight flight = cellData.getValue();
            LocalDateTime now = LocalDateTime.now();
            String status;

            if (flight.getDepartureTime().isBefore(now)) {
                status = "COMPLETADO";
            } else if (flight.getDepartureTime().minusHours(2).isBefore(now)) {
                status = "EN EMBARQUE";
            } else {
                status = "PROGRAMADO";
            }

            return new javafx.beans.property.SimpleStringProperty(status);
        });

        // Configurar formato de fecha
        departureTimeColumn.setCellFactory(column -> new TableCell<Flight, LocalDateTime>() {
            private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });

        myFlightsTable.setItems(passengerFlights);
    }

    private void loadPassengers() {
        try {
            SinglyLinkedList<Passenger> passengers = personService.getAllPassengers();

            if (passengers != null && !passengers.isEmpty()) {
                passengerComboBox.getItems().clear();

                for (int i = 0; i < passengers.size(); i++) {
                    Passenger passenger = passengers.get(i);
                    passengerComboBox.getItems().add(passenger);
                }

                StringConverter<Passenger> converter = new StringConverter<Passenger>() {
                    @Override
                    public String toString(Passenger passenger) {
                        return passenger == null ? "" : passenger.getName() + " (" + passenger.getId() + ")";
                    }

                    @Override
                    public Passenger fromString(String string) {
                        return null;
                    }
                };

                passengerComboBox.setConverter(converter);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudieron cargar los pasajeros: " + e.getMessage());
        }
    }

    private void setupEventListeners() {
        // Listener para selección de pasajero
        passengerComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedPassenger = newSelection;
            if (selectedPassenger != null) {
                loadPassengerFlights();
                updatePassengerInfo();
            } else {
                clearPassengerData();
            }
        });

        // Listener para selección de vuelo
        myFlightsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedFlight = newSelection;
            updateButtonStates();
        });
    }

    private void loadPassengerFlights() {
        passengerFlights.clear();

        if (selectedPassenger == null) return;

        // Buscar vuelos donde aparece el pasajero
        List<Flight> allFlights = flightService.getAllFlights();

        for (Flight flight : allFlights) {
            if (flight.getPassengers().contains(selectedPassenger)) {
                passengerFlights.add(flight);
            }
        }

        // Actualizar contador
        flightCountLabel.setText("Vuelos encontrados: " + passengerFlights.size());

        // Generar historial detallado
        generateFlightHistory();
    }

    private void updatePassengerInfo() {
        if (selectedPassenger != null) {
            StringBuilder info = new StringBuilder();
            info.append("Nombre: ").append(selectedPassenger.getName()).append("\n");
            info.append("ID: ").append(selectedPassenger.getId()).append("\n");
            info.append("Nacionalidad: ").append(selectedPassenger.getNationality()).append("\n");

            // Quitamos los métodos que no existen en la clase Passenger
            passengerInfoLabel.setText(info.toString());
        }
    }

    private void generateFlightHistory() {
        StringBuilder history = new StringBuilder();
        history.append("HISTORIAL DE VUELOS - ").append(selectedPassenger.getName()).append("\n");
        history.append("=".repeat(50)).append("\n\n");

        if (passengerFlights.isEmpty()) {
            history.append("No hay vuelos registrados para este pasajero.");
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime now = LocalDateTime.now();

            // Separar vuelos por estado
            List<Flight> completed = new ArrayList<>();
            List<Flight> upcoming = new ArrayList<>();

            for (Flight flight : passengerFlights) {
                if (flight.getDepartureTime().isBefore(now)) {
                    completed.add(flight);
                } else {
                    upcoming.add(flight);
                }
            }

            // Vuelos completados
            if (!completed.isEmpty()) {
                history.append("VUELOS COMPLETADOS (").append(completed.size()).append("):\n");
                history.append("-".repeat(30)).append("\n");

                for (Flight flight : completed) {
                    history.append("✈️ Vuelo ").append(flight.getNumber()).append("\n");
                    history.append("   Ruta: ").append(flight.getOrigin()).append(" → ").append(flight.getDestination()).append("\n");
                    history.append("   Fecha: ").append(flight.getDepartureTime().format(formatter)).append("\n");
                    history.append("   Estado: COMPLETADO\n\n");
                }
            }

            // Vuelos próximos
            if (!upcoming.isEmpty()) {
                history.append("VUELOS PRÓXIMOS (").append(upcoming.size()).append("):\n");
                history.append("-".repeat(30)).append("\n");

                for (Flight flight : upcoming) {
                    String status = flight.getDepartureTime().minusHours(2).isBefore(now) ? "EN EMBARQUE" : "PROGRAMADO";

                    history.append("🛫 Vuelo ").append(flight.getNumber()).append("\n");
                    history.append("   Ruta: ").append(flight.getOrigin()).append(" → ").append(flight.getDestination()).append("\n");
                    history.append("   Fecha: ").append(flight.getDepartureTime().format(formatter)).append("\n");
                    history.append("   Estado: ").append(status).append("\n\n");
                }
            }

            // Estadísticas
            history.append("ESTADÍSTICAS:\n");
            history.append("-".repeat(20)).append("\n");
            history.append("Total de vuelos: ").append(passengerFlights.size()).append("\n");
            history.append("Vuelos completados: ").append(completed.size()).append("\n");
            history.append("Vuelos próximos: ").append(upcoming.size()).append("\n");
        }

        flightHistoryTextArea.setText(history.toString());
    }

    private void updateButtonStates() {
        boolean flightSelected = selectedFlight != null;
        viewDetailsButton.setDisable(!flightSelected);

        // Solo permitir cancelar vuelos futuros
        boolean canCancel = flightSelected && selectedFlight.getDepartureTime().isAfter(LocalDateTime.now().plusHours(24));
        cancelFlightButton.setDisable(!canCancel);
    }

    private void clearPassengerData() {
        passengerFlights.clear();
        passengerInfoLabel.setText("Seleccione un pasajero para ver su información");
        flightCountLabel.setText("Vuelos encontrados: 0");
        flightHistoryTextArea.clear();
        selectedFlight = null;
        updateButtonStates();
    }

    @FXML
    private void handleViewDetails(ActionEvent event) {
        if (selectedFlight == null) return;

        try {
            // Crear ventana de detalles
            StringBuilder details = new StringBuilder();
            details.append("DETALLES DEL VUELO ").append(selectedFlight.getNumber()).append("\n");
            details.append("=".repeat(40)).append("\n\n");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            details.append("Información del Vuelo:\n");
            details.append("- Número: ").append(selectedFlight.getNumber()).append("\n");
            details.append("- Origen: ").append(selectedFlight.getOrigin()).append("\n");
            details.append("- Destino: ").append(selectedFlight.getDestination()).append("\n");
            details.append("- Fecha/Hora: ").append(selectedFlight.getDepartureTime().format(formatter)).append("\n");
            details.append("- Capacidad: ").append(selectedFlight.getCapacity()).append(" pasajeros\n");
            details.append("- Ocupación: ").append(selectedFlight.getOccupancy()).append(" pasajeros\n");
            details.append("- Asientos disponibles: ").append(selectedFlight.getCapacity() - selectedFlight.getOccupancy()).append("\n\n");

            LocalDateTime now = LocalDateTime.now();
            String status;
            if (selectedFlight.getDepartureTime().isBefore(now)) {
                status = "COMPLETADO";
            } else if (selectedFlight.getDepartureTime().minusHours(2).isBefore(now)) {
                status = "EN EMBARQUE";
            } else {
                status = "PROGRAMADO";
            }
            details.append("Estado: ").append(status).append("\n\n");

            details.append("Pasajeros en este vuelo:\n");
            details.append("-".repeat(25)).append("\n");

            List<Passenger> passengers = selectedFlight.getPassengers();
            if (passengers.isEmpty()) {
                details.append("No hay pasajeros registrados.\n");
            } else {
                for (int i = 0; i < passengers.size(); i++) {
                    Passenger p = passengers.get(i);
                    details.append((i + 1)).append(". ").append(p.getName()).append(" (").append(p.getId()).append(")\n");
                }
            }

            // Mostrar en ventana emergente
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Detalles del Vuelo");
            alert.setHeaderText("Vuelo " + selectedFlight.getNumber());

            TextArea textArea = new TextArea(details.toString());
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefRowCount(20);
            textArea.setPrefColumnCount(50);

            alert.getDialogPane().setContent(textArea);
            alert.showAndWait();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error al mostrar detalles: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelFlight(ActionEvent event) {
        if (selectedFlight == null || selectedPassenger == null) return;

        // Confirmar cancelación
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Cancelación");
        confirmAlert.setHeaderText("¿Cancelar reserva?");
        confirmAlert.setContentText("¿Está seguro que desea cancelar la reserva del pasajero " +
                selectedPassenger.getName() + " en el vuelo " + selectedFlight.getNumber() + "?");

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                // Necesitas implementar este método en FlightService
                // Probablemente deberías usar:
                // boolean removed = selectedFlight.removePassenger(selectedPassenger);
                // flightService.updateFlight(selectedFlight);
                boolean removed = flightService.removePassengerFromFlight(selectedFlight, selectedPassenger);

                if (removed) {
                    showAlert(Alert.AlertType.INFORMATION, "Reserva Cancelada",
                            "La reserva ha sido cancelada exitosamente.");

                    // Actualizar la lista de vuelos del pasajero
                    loadPassengerFlights();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error",
                            "No se pudo cancelar la reserva.");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Error al cancelar la reserva: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            // Cargar la vista del portal de usuario
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserPortal.fxml"));
            Parent root = loader.load();

            // Obtener la ventana actual y cambiar la escena
            BorderPane borderPane = (BorderPane) passengerComboBox.getScene().getRoot();
            borderPane.setCenter(root);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "No se pudo regresar al portal de usuario: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}