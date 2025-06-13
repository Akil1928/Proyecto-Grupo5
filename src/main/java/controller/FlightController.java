
package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import domain.Airport;
import domain.Flight;
import domain.Passenger;
import services.AirportService;
import services.FlightService;
import services.PassengerService;
import services.SimulationService;
import datastructure.list.ListException;
import datastructure.list.SinglyLinkedList;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class FlightController implements Initializable {

    @FXML
    private TableColumn<Flight, Integer> capacityColumn;

    @FXML
    private TextField capacityField;

    @FXML
    private TableColumn<Flight, LocalDateTime> departureTimeColumn;

    @FXML
    private DatePicker departureDatePicker;

    @FXML
    private ComboBox<String> departureHourCombo;

    @FXML
    private ComboBox<String> departureMinuteCombo;

    @FXML
    private TableColumn<Flight, String> destinationColumn;

    @FXML
    private ComboBox<Airport> destinationComboBox;

    @FXML
    private TextField flightNumberField;

    @FXML
    private TableView<Flight> flightTable;

    @FXML
    private TableColumn<Flight, Integer> numberColumn;

    @FXML
    private TableColumn<Flight, Integer> occupancyColumn;

    @FXML
    private TableColumn<Flight, String> originColumn;

    @FXML
    private ComboBox<Airport> originComboBox;

    @FXML
    private ListView<Passenger> passengerListView;

    @FXML
    private ComboBox<Passenger> passengerComboBox;

    private FlightService flightService = FlightService.getInstance();
    private PassengerService passengerService = PassengerService.getInstance();
    private AirportService airportService = AirportService.getInstance();
    private SimulationService simulationService = SimulationService.getInstance();

    private Flight selectedFlight;
    private ObservableList<Passenger> flightPassengers;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configurar las columnas de la tabla
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        originColumn.setCellValueFactory(new PropertyValueFactory<>("origin"));
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        departureTimeColumn.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        occupancyColumn.setCellValueFactory(new PropertyValueFactory<>("occupancy"));

        // Inicializar selector de fecha con la fecha actual
        departureDatePicker.setValue(LocalDate.now());

        // Inicializar opciones de horas (0-23)
        for (int i = 0; i < 24; i++) {
            departureHourCombo.getItems().add(String.format("%02d", i));
        }
        departureHourCombo.setValue("12"); // Valor por defecto

        // Inicializar opciones de minutos (0-59)
        for (int i = 0; i < 60; i++) {
            departureMinuteCombo.getItems().add(String.format("%02d", i));
        }
        departureMinuteCombo.setValue("00"); // Valor por defecto

        // Cargar aeropuertos en los ComboBox
        loadAirports();

        // Cargar pasajeros en el ComboBox
        loadPassengers();

        // Configurar listener para la selección de vuelos
        flightTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedFlight = newSelection;
            updatePassengerListView();
        });

        // Cargar datos iniciales (si los hay)
        updateFlightTable();
    }

    private void loadAirports() {
        try {
            // Obtener aeropuertos activos
            SinglyLinkedList<Airport> airportList = airportService.listAirports("active");

            if (!airportList.isEmpty()) {
                originComboBox.getItems().clear();
                destinationComboBox.getItems().clear();

                // Configurar cómo se muestran los aeropuertos
                StringConverter<Airport> airportConverter = new StringConverter<>() {
                    @Override
                    public String toString(Airport airport) {
                        if (airport == null) return "";
                        return airport.getCode() + " - " + airport.getName() + " (" + airport.getCountry() + ")";
                    }

                    @Override
                    public Airport fromString(String string) {
                        return null; // No necesitamos esta dirección
                    }
                };

                originComboBox.setConverter(airportConverter);
                destinationComboBox.setConverter(airportConverter);

                // Añadir cada aeropuerto a los ComboBox
                for (int i = 1; i <= airportList.size(); i++) {
                    Airport airport = (Airport) airportList.getNode(i).data;
                    originComboBox.getItems().add(airport);
                    destinationComboBox.getItems().add(airport);
                }

                // Seleccionar el primer aeropuerto por defecto
                if (!originComboBox.getItems().isEmpty()) {
                    originComboBox.getSelectionModel().selectFirst();
                    destinationComboBox.getSelectionModel().selectFirst();
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Aeropuertos no disponibles",
                        "No hay aeropuertos activos en el sistema. Por favor, cree algunos aeropuertos primero.");
            }
        } catch (ListException e) {
            showAlert(Alert.AlertType.ERROR, "Error al cargar aeropuertos",
                    "No se pudieron cargar los aeropuertos: " + e.getMessage());
        }
    }

    private void loadPassengers() {
        List<Passenger> passengers = passengerService.getAllPassengers();
        if (passengers != null && !passengers.isEmpty()) {
            passengerComboBox.getItems().clear();
            passengerComboBox.getItems().addAll(passengers);

            // Configurar cómo se muestran los pasajeros
            StringConverter<Passenger> passengerConverter = new StringConverter<>() {
                @Override
                public String toString(Passenger passenger) {
                    if (passenger == null) return "";
                    return passenger.getId() + " - " + passenger.getName() + " (" + passenger.getNationality() + ")";
                }

                @Override
                public Passenger fromString(String string) {
                    return null; // No necesitamos esta dirección
                }
            };

            passengerComboBox.setConverter(passengerConverter);
            passengerComboBox.getSelectionModel().selectFirst();
        } else {
            showAlert(Alert.AlertType.WARNING, "Pasajeros no disponibles",
                    "No hay pasajeros registrados en el sistema. Por favor, registre algunos pasajeros primero.");
        }
    }

    // Método para crear un vuelo
    @FXML
    void handleCreateFlight(ActionEvent event) {
        try {
            // Validar que todos los campos estén llenos
            if (flightNumberField.getText().isEmpty() ||
                    originComboBox.getValue() == null ||
                    destinationComboBox.getValue() == null ||
                    departureDatePicker.getValue() == null ||
                    departureHourCombo.getValue() == null ||
                    departureMinuteCombo.getValue() == null ||
                    capacityField.getText().isEmpty()) {

                showAlert(Alert.AlertType.ERROR, "Error al crear vuelo",
                        "Todos los campos son obligatorios");
                return;
            }

            // Validar que origen y destino sean diferentes
            if (originComboBox.getValue().equals(destinationComboBox.getValue())) {
                showAlert(Alert.AlertType.ERROR, "Error al crear vuelo",
                        "El origen y el destino no pueden ser el mismo aeropuerto");
                return;
            }

            int flightNumber = Integer.parseInt(flightNumberField.getText());
            String origin = originComboBox.getValue().getCode();
            String destination = destinationComboBox.getValue().getCode();
            int capacity = Integer.parseInt(capacityField.getText());

            // Obtener fecha y hora seleccionadas
            LocalDate selectedDate = departureDatePicker.getValue();
            int hour = Integer.parseInt(departureHourCombo.getValue());
            int minute = Integer.parseInt(departureMinuteCombo.getValue());

            // Crear LocalDateTime combinando fecha y hora
            LocalDateTime departureDateTime = LocalDateTime.of(selectedDate, LocalTime.of(hour, minute, 0));

            // Convertir a String en el formato esperado por el servicio
            String departureTime = departureDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

            // Crear vuelo
            Flight flight = flightService.createFlight(flightNumber, origin, destination, departureTime, capacity);

            // Mostrar confirmación
            showAlert(Alert.AlertType.INFORMATION, "Vuelo creado",
                    "El vuelo número " + flightNumber + " ha sido creado exitosamente:\n" +
                            "Origen: " + originComboBox.getValue().getName() + " (" + origin + ")\n" +
                            "Destino: " + destinationComboBox.getValue().getName() + " (" + destination + ")\n" +
                            "Salida: " + departureDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

            // Limpiar campos
            clearFields();

            // Actualizar tabla
            updateFlightTable();

            // Imprimir información del vuelo en consola para debug
            System.out.println("Vuelo creado: " + flight);
            System.out.println("Total de vuelos activos: " + flightService.getActiveFlights().size());

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error de formato",
                    "El número de vuelo y la capacidad deben ser valores numéricos");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error al crear el vuelo", e.getMessage());
            e.printStackTrace(); // Para depuración
        }
    }

    // Método para agregar un pasajero al vuelo seleccionado
    @FXML
    void handleAddPassenger(ActionEvent event) {
        if (selectedFlight == null) {
            showAlert(Alert.AlertType.WARNING, "Selección requerida",
                    "Debe seleccionar un vuelo para agregar pasajeros");
            return;
        }

        Passenger selectedPassenger = passengerComboBox.getValue();
        if (selectedPassenger == null) {
            showAlert(Alert.AlertType.WARNING, "Selección requerida",
                    "Debe seleccionar un pasajero para agregar al vuelo");
            return;
        }

        boolean added = flightService.addPassengerToFlight(selectedFlight.getNumber(), selectedPassenger);

        if (added) {
            showAlert(Alert.AlertType.INFORMATION, "Pasajero agregado",
                    "El pasajero " + selectedPassenger.getName() + " ha sido agregado al vuelo " + selectedFlight.getNumber());

            // Actualizar historial de vuelos del pasajero
            try {
                selectedPassenger.getFlightHistory().add(selectedFlight);
            } catch (Exception e) {
                System.err.println("Error al actualizar historial de vuelos: " + e.getMessage());
            }

            // Actualizar la vista
            updateFlightTable();
            updatePassengerListView();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error al agregar pasajero",
                    "No se pudo agregar el pasajero al vuelo. Posiblemente el vuelo está lleno.");
        }
    }

    // Método para mostrar los vuelos activos
    @FXML
    void handleDisplayActiveFlights(ActionEvent event) {
        flightTable.getItems().clear();
        List<Flight> activeFlights = flightService.getActiveFlights();
        flightTable.getItems().addAll(activeFlights);

        if (flightTable.getItems().isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Vuelos activos",
                    "No hay vuelos activos para mostrar");
        }
    }

    // Método para mostrar los vuelos completados
    @FXML
    void handleDisplayCompletedFlights(ActionEvent event) {
        flightTable.getItems().clear();
        List<Flight> completedFlights = flightService.getCompletedFlights();
        flightTable.getItems().addAll(completedFlights);

        if (flightTable.getItems().isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Vuelos completados",
                    "No hay vuelos completados para mostrar");
        }
    }

    // Método para simular un vuelo
    @FXML
    void handleSimulateFlight(ActionEvent event) {
        if (selectedFlight == null) {
            showAlert(Alert.AlertType.WARNING, "Selección requerida",
                    "Debe seleccionar un vuelo para simular");
            return;
        }

        // Comprobar si el vuelo tiene pasajeros
        if (selectedFlight.getPassengers().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Vuelo sin pasajeros",
                    "El vuelo seleccionado no tiene pasajeros. Agregue pasajeros antes de simular.");
            return;
        }

        // Crear ventana de simulación
        try {
            // Obtener el reporte de simulación
            String simulationReport = simulationService.simulateFlight(selectedFlight.getNumber());

            VBox simulationBox = new VBox(10);
            simulationBox.setPadding(new javafx.geometry.Insets(20));

            // Título
            Label titleLabel = new Label("Simulación de Vuelo " + selectedFlight.getNumber());
            titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

            // Área de texto para mostrar el reporte
            TextArea reportArea = new TextArea(simulationReport);
            reportArea.setPrefHeight(500);
            reportArea.setEditable(false);
            reportArea.setWrapText(true);

            // Botón para cerrar
            Button closeButton = new Button("Cerrar");
            closeButton.setOnAction(e -> {
                // Cerrar la ventana
                ((Stage) closeButton.getScene().getWindow()).close();

                // Actualizar vista
                updateFlightTable();
                selectedFlight = null;
                passengerListView.getItems().clear();
            });

            // Agregar todos los elementos al contenedor
            simulationBox.getChildren().addAll(
                    titleLabel,
                    reportArea,
                    closeButton
            );

            // Mostrar la ventana
            Stage simulationStage = new Stage();
            simulationStage.setTitle("Simulación de Vuelo");
            simulationStage.initModality(Modality.APPLICATION_MODAL);
            simulationStage.setScene(new Scene(simulationBox, 500, 600));
            simulationStage.showAndWait();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error en simulación",
                    "Error al simular el vuelo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para volver al dashboard
    @FXML
    void regresarOnAction(ActionEvent event) {
        try {
            Parent dashboard = FXMLLoader.load(getClass().getResource("/dashboard.fxml"));
            BorderPane root = (BorderPane) ((Button)event.getSource()).getScene().getRoot();
            root.setCenter(dashboard);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar el dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para actualizar la tabla de vuelos
    public void updateFlightTable() {
        flightTable.getItems().clear(); // Limpiar la tabla
        flightTable.getItems().addAll(flightService.getActiveFlights()); // Agregar vuelos activos
    }

    // Método para actualizar la lista de pasajeros del vuelo seleccionado
    private void updatePassengerListView() {
        flightPassengers = FXCollections.observableArrayList();

        if (selectedFlight != null && selectedFlight.getPassengers() != null) {
            flightPassengers.addAll(selectedFlight.getPassengers());
        }

        passengerListView.setItems(flightPassengers);
    }

    // Método para limpiar campos después de crear un vuelo
    private void clearFields() {
        flightNumberField.clear();
        // No limpiamos los ComboBox de origen/destino, mantenemos la selección
        departureDatePicker.setValue(LocalDate.now());
        departureHourCombo.setValue("12");
        departureMinuteCombo.setValue("00");
        capacityField.clear();
    }

    // Método auxiliar para mostrar alertas
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}