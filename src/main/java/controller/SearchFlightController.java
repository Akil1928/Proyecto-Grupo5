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
import domain.Airport;
import domain.Flight;
import domain.Passenger;
import services.AirportService;
import services.FlightService;
import services.PersonService;
import datastructure.list.ListException;
import datastructure.list.SinglyLinkedList;
import datastructure.queue.QueueException;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SearchFlightController implements Initializable {

    @FXML private ComboBox<Airport> originComboBox;
    @FXML private ComboBox<Airport> destinationComboBox;
    @FXML private DatePicker departureDatePicker;
    @FXML private TableView<Flight> flightSearchTable;
    @FXML private TableColumn<Flight, Integer> flightNumberColumn;
    @FXML private TableColumn<Flight, String> originColumn;
    @FXML private TableColumn<Flight, String> destinationColumn;
    @FXML private TableColumn<Flight, LocalDateTime> departureTimeColumn;
    @FXML private TableColumn<Flight, Integer> availableSeatsColumn;
    @FXML private TableColumn<Flight, String> statusColumn;
    @FXML private Button searchButton;
    @FXML private Button purchaseButton;
    @FXML private ComboBox<Passenger> passengerComboBox;
    @FXML private Label searchResultsLabel;

    private FlightService flightService = FlightService.getInstance();
    private AirportService airportService = AirportService.getInstance();
    private PersonService personService = PersonService.getInstance();

    private Flight selectedFlight;
    private ObservableList<Flight> searchResults = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadAirports();
        loadPassengers();
        setupEventListeners();

        // Configurar fecha por defecto
        departureDatePicker.setValue(LocalDate.now());

        // Deshabilitar botón de compra inicialmente
        purchaseButton.setDisable(true);
    }

    private void setupTableColumns() {
        flightNumberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        originColumn.setCellValueFactory(new PropertyValueFactory<>("origin"));
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        departureTimeColumn.setCellValueFactory(new PropertyValueFactory<>("departureTime"));

        // Columna de asientos disponibles
        availableSeatsColumn.setCellValueFactory(cellData -> {
            Flight flight = cellData.getValue();
            int available = flight.getCapacity() - flight.getOccupancy();
            return new javafx.beans.property.SimpleIntegerProperty(available).asObject();
        });

        // Columna de estado
        statusColumn.setCellValueFactory(cellData -> {
            Flight flight = cellData.getValue();
            String status = flight.isFull() ? "LLENO" : "DISPONIBLE";
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

        flightSearchTable.setItems(searchResults);
    }

    private void loadAirports() {
        try {
            SinglyLinkedList<Airport> airports = airportService.listAirports("active");

            if (!airports.isEmpty()) {
                originComboBox.getItems().clear();
                destinationComboBox.getItems().clear();

                StringConverter<Airport> converter = new StringConverter<Airport>() {
                    @Override
                    public String toString(Airport airport) {
                        return airport == null ? "" : airport.getCode() + " - " + airport.getName();
                    }

                    @Override
                    public Airport fromString(String string) {
                        return null;
                    }
                };

                originComboBox.setConverter(converter);
                destinationComboBox.setConverter(converter);

                for (int i = 1; i <= airports.size(); i++) {
                    Airport airport = (Airport) airports.getNode(i).data;
                    originComboBox.getItems().add(airport);
                    destinationComboBox.getItems().add(airport);
                }
            }
        } catch (ListException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudieron cargar los aeropuertos: " + e.getMessage());
        }
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
        // Listener para selección de vuelo
        flightSearchTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedFlight = newSelection;
            purchaseButton.setDisable(selectedFlight == null || passengerComboBox.getValue() == null);
        });

        // Listener para selección de pasajero
        passengerComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            purchaseButton.setDisable(selectedFlight == null || newSelection == null);
        });
    }

    @FXML
    private void handleSearchFlights(ActionEvent event) {
        if (!validateSearchInputs()) {
            return;
        }

        Airport origin = originComboBox.getValue();
        Airport destination = destinationComboBox.getValue();
        LocalDate searchDate = departureDatePicker.getValue();

        // Buscar vuelos directos
        List<Flight> directFlights = searchDirectFlights(origin.getCode(), destination.getCode(), searchDate);

        // Buscar vuelos con conexiones (opcional)
        List<Flight> connectingFlights = searchConnectingFlights(origin.getCode(), destination.getCode(), searchDate);

        // Combinar resultados
        searchResults.clear();
        searchResults.addAll(directFlights);
        searchResults.addAll(connectingFlights);

        // Actualizar etiqueta de resultados
        updateSearchResultsLabel();

        if (searchResults.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Sin resultados",
                    "No se encontraron vuelos para la ruta y fecha seleccionadas.");
        }
    }

    private boolean validateSearchInputs() {
        if (originComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validación", "Seleccione un aeropuerto de origen");
            return false;
        }

        if (destinationComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validación", "Seleccione un aeropuerto de destino");
            return false;
        }

        if (originComboBox.getValue().equals(destinationComboBox.getValue())) {
            showAlert(Alert.AlertType.WARNING, "Validación", "El origen y destino no pueden ser iguales");
            return false;
        }

        if (departureDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validación", "Seleccione una fecha de salida");
            return false;
        }

        if (departureDatePicker.getValue().isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.WARNING, "Validación", "La fecha de salida no puede ser anterior a hoy");
            return false;
        }

        return true;
    }

    private List<Flight> searchDirectFlights(String origin, String destination, LocalDate date) {
        List<Flight> results = new ArrayList<>();
        List<Flight> allFlights = flightService.getAllFlights();

        for (Flight flight : allFlights) {
            if (flight.getOrigin().equals(origin) &&
                    flight.getDestination().equals(destination) &&
                    flight.getDepartureTime().toLocalDate().equals(date) &&
                    !flight.isFull()) {
                results.add(flight);
            }
        }

        return results;
    }

    private List<Flight> searchConnectingFlights(String origin, String destination, LocalDate date) {
        List<Flight> results = new ArrayList<>();
        List<Flight> allFlights = flightService.getAllFlights();

        // Buscar vuelos que salgan del origen en la fecha indicada
        for (Flight firstFlight : allFlights) {
            if (firstFlight.getOrigin().equals(origin) &&
                    firstFlight.getDepartureTime().toLocalDate().equals(date) &&
                    !firstFlight.isFull()) {

                // Buscar vuelos de conexión que salgan del destino del primer vuelo
                String intermediateAirport = firstFlight.getDestination();

                for (Flight secondFlight : allFlights) {
                    if (secondFlight.getOrigin().equals(intermediateAirport) &&
                            secondFlight.getDestination().equals(destination) &&
                            secondFlight.getDepartureTime().isAfter(firstFlight.getDepartureTime().plusHours(2)) &&
                            secondFlight.getDepartureTime().toLocalDate().equals(date) &&
                            !secondFlight.isFull()) {

                        // Agregar ambos vuelos (se podrían crear objetos de conexión especiales)
                        if (!results.contains(firstFlight)) {
                            results.add(firstFlight);
                        }
                        if (!results.contains(secondFlight)) {
                            results.add(secondFlight);
                        }
                    }
                }
            }
        }

        return results;
    }

    private void updateSearchResultsLabel() {
        int totalResults = searchResults.size();
        int availableSeats = searchResults.stream()
                .mapToInt(flight -> flight.getCapacity() - flight.getOccupancy())
                .sum();

        searchResultsLabel.setText(String.format("Resultados: %d vuelos encontrados, %d asientos disponibles",
                totalResults, availableSeats));
    }

    @FXML
    private void handlePurchaseTicket(ActionEvent event) {
        if (selectedFlight == null || passengerComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Selección requerida",
                    "Seleccione un vuelo y un pasajero");
            return;
        }

        Passenger passenger = passengerComboBox.getValue();

        try {
            boolean success = flightService.purchaseTicket(selectedFlight.getNumber(), passenger);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Compra exitosa",
                        "Boleto comprado exitosamente para " + passenger.getName() +
                                " en el vuelo " + selectedFlight.getNumber());

                // Actualizar la tabla
                refreshSearchResults();
            } else {
                String queueStatus = flightService.getQueueStatus(selectedFlight.getNumber());
                showAlert(Alert.AlertType.INFORMATION, "En lista de espera",
                        "El vuelo está lleno. El pasajero ha sido agregado a la lista de espera.\n" + queueStatus);
            }

        } catch (QueueException e) {
            showAlert(Alert.AlertType.ERROR, "Error en la compra",
                    "Error al procesar la compra: " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Error inesperado: " + e.getMessage());
        }
    }

    private void refreshSearchResults() {
        // Recargar los datos de los vuelos en la tabla
        for (Flight flight : searchResults) {
            Flight updatedFlight = flightService.findByNumber(flight.getNumber());
            if (updatedFlight != null) {
                int index = searchResults.indexOf(flight);
                searchResults.set(index, updatedFlight);
            }
        }

        flightSearchTable.refresh();
        updateSearchResultsLabel();
    }

    @FXML
    private void handleClearSearch(ActionEvent event) {
        originComboBox.getSelectionModel().clearSelection();
        destinationComboBox.getSelectionModel().clearSelection();
        departureDatePicker.setValue(LocalDate.now());
        searchResults.clear();
        searchResultsLabel.setText("Resultados: 0 vuelos encontrados");
        selectedFlight = null;
        purchaseButton.setDisable(true);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            // Cambiar a userDashboard.fxml
            Parent dashboard = FXMLLoader.load(getClass().getResource("/userDashboard.fxml"));
            BorderPane root = (BorderPane) ((Button) event.getSource()).getScene().getRoot();
            root.setCenter(dashboard);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo regresar al dashboard: " + e.getMessage());
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