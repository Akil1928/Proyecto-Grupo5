package controller;

import domain.Airport;
import domain.Destination;
import domain.Route;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
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
import services.AirportService;
import services.RouteService;
import datastructure.list.ListException;
import datastructure.list.SinglyLinkedList;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class RouteController implements Initializable {

    @FXML
    private ComboBox<Airport> originComboBox;

    @FXML
    private ComboBox<Airport> destinationComboBox;

    @FXML
    private TextField distanceField;

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button clearButton;

    @FXML
    private TableView<RouteEntry> routeTable;

    @FXML
    private TableColumn<RouteEntry, String> originColumn;

    @FXML
    private TableColumn<RouteEntry, String> destinationColumn;

    @FXML
    private TableColumn<RouteEntry, Double> distanceColumn;

    @FXML
    private ComboBox<Airport> originPathCombo;

    @FXML
    private ComboBox<Airport> destinationPathCombo;

    @FXML
    private Label pathResultLabel;

    private RouteService routeService;
    private AirportService airportService;
    private ObservableList<RouteEntry> routeEntries;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicializar servicios
        routeService = RouteService.getInstance();
        airportService = AirportService.getInstance();

        // Inicializar colecciones observables
        routeEntries = FXCollections.observableArrayList();

        // Configurar tabla
        originColumn.setCellValueFactory(data -> data.getValue().originProperty());
        destinationColumn.setCellValueFactory(data -> data.getValue().destinationProperty());
        distanceColumn.setCellValueFactory(data -> data.getValue().distanceProperty().asObject());
        routeTable.setItems(routeEntries);

        // Cargar datos iniciales
        loadAirports();
        loadRoutes();

        // Configurar listener para la selección en la tabla
        routeTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Al seleccionar una ruta, cargar sus datos en los campos
                Airport origin = findAirportByCode(newValue.getOrigin());
                Airport destination = findAirportByCode(newValue.getDestination());

                originComboBox.setValue(origin);
                destinationComboBox.setValue(destination);
                distanceField.setText(String.valueOf(newValue.getDistance()));

                // Habilitar botones de modificar y eliminar
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            } else {
                clearForm();
            }
        });
    }

    /**
     * Carga los aeropuertos en los combobox
     */
    private void loadAirports() {
        try {
            SinglyLinkedList<Airport> airports = airportService.listAirports("active");
            ObservableList<Airport> airportList = FXCollections.observableArrayList();

            for (int i = 1; i <= airports.size(); i++) {
                airportList.add(airports.get(i));
            }

            originComboBox.setItems(airportList);
            destinationComboBox.setItems(airportList);
            originPathCombo.setItems(airportList);
            destinationPathCombo.setItems(airportList);

            // Configurar cómo se muestran los aeropuertos en los combobox
            originComboBox.setButtonCell(new AirportListCell());
            originComboBox.setCellFactory(param -> new AirportListCell());
            destinationComboBox.setButtonCell(new AirportListCell());
            destinationComboBox.setCellFactory(param -> new AirportListCell());
            originPathCombo.setButtonCell(new AirportListCell());
            originPathCombo.setCellFactory(param -> new AirportListCell());
            destinationPathCombo.setButtonCell(new AirportListCell());
            destinationPathCombo.setCellFactory(param -> new AirportListCell());
        } catch (ListException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error al cargar aeropuertos: " + e.getMessage());
        }
    }

    /**
     * Carga las rutas en la tabla
     */
    private void loadRoutes() {
        routeEntries.clear();
        SinglyLinkedList<Route> routes = routeService.getAllRoutes();

        try {
            for (int i = 1; i <= routes.size(); i++) {
                Route route = routes.get(i);
                SinglyLinkedList<Destination> destinations = route.getDestinationList();

                for (int j = 1; j <= destinations.size(); j++) {
                    Destination dest = destinations.get(j);
                    routeEntries.add(new RouteEntry(
                            route.getOrigin().getCode(),
                            dest.getAirport().getCode(),
                            dest.getDistance()
                    ));
                }
            }
        } catch (ListException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error al cargar rutas: " + e.getMessage());
        }
    }

    /**
     * Maneja la acción de agregar una nueva ruta
     */
    @FXML
    void handleAddRoute(ActionEvent event) {
        Airport origin = originComboBox.getValue();
        Airport destination = destinationComboBox.getValue();
        String distanceStr = distanceField.getText();

        if (origin == null || destination == null || distanceStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Datos Incompletos",
                    "Debe seleccionar aeropuertos de origen y destino, y especificar la distancia.");
            return;
        }

        if (origin.equals(destination)) {
            showAlert(Alert.AlertType.WARNING, "Error",
                    "El aeropuerto de origen y destino no pueden ser el mismo.");
            return;
        }

        try {
            double distance = Double.parseDouble(distanceStr);
            if (distance <= 0) {
                showAlert(Alert.AlertType.WARNING, "Dato Inválido",
                        "La distancia debe ser un número positivo.");
                return;
            }

            boolean success = routeService.addRoute(origin, destination, distance);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Éxito", "Ruta agregada correctamente.");
                loadRoutes();
                clearForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "No se pudo agregar la ruta.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "La distancia debe ser un número válido.");
        }
    }

    /**
     * Maneja la acción de modificar una ruta existente
     */
    @FXML
    void handleUpdateRoute(ActionEvent event) {
        Airport origin = originComboBox.getValue();
        Airport destination = destinationComboBox.getValue();
        String distanceStr = distanceField.getText();

        if (origin == null || destination == null || distanceStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Datos Incompletos",
                    "Debe seleccionar aeropuertos de origen y destino, y especificar la distancia.");
            return;
        }

        try {
            double distance = Double.parseDouble(distanceStr);
            if (distance <= 0) {
                showAlert(Alert.AlertType.WARNING, "Dato Inválido",
                        "La distancia debe ser un número positivo.");
                return;
            }

            boolean success = routeService.updateRouteDistance(origin, destination, distance);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Éxito", "Ruta modificada correctamente.");
                loadRoutes();
                clearForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "No se pudo modificar la ruta. Verifique que la ruta exista.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "La distancia debe ser un número válido.");
        }
    }

    /**
     * Maneja la acción de eliminar una ruta
     */
    @FXML
    void handleDeleteRoute(ActionEvent event) {
        RouteEntry selectedEntry = routeTable.getSelectionModel().getSelectedItem();
        if (selectedEntry == null) {
            showAlert(Alert.AlertType.WARNING, "Selección Requerida",
                    "Debe seleccionar una ruta para eliminar.");
            return;
        }

        // Confirmar eliminación
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Eliminación");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("¿Está seguro de eliminar la ruta de " +
                selectedEntry.getOrigin() + " a " + selectedEntry.getDestination() + "?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            boolean success = routeService.deleteDestination(
                    selectedEntry.getOrigin(),
                    selectedEntry.getDestination()
            );

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Éxito", "Ruta eliminada correctamente.");
                loadRoutes();
                clearForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "No se pudo eliminar la ruta.");
            }
        }
    }

    /**
     * Maneja la acción de limpiar el formulario
     */
    @FXML
    void handleClearForm(ActionEvent event) {
        clearForm();
    }

    /**
     * Maneja la acción de calcular la ruta más corta
     */
    @FXML
    void handleCalculatePath(ActionEvent event) {
        Airport origin = originPathCombo.getValue();
        Airport destination = destinationPathCombo.getValue();

        if (origin == null || destination == null) {
            showAlert(Alert.AlertType.WARNING, "Datos Incompletos",
                    "Debe seleccionar aeropuertos de origen y destino.");
            return;
        }

        if (origin.equals(destination)) {
            pathResultLabel.setText("El origen y destino son el mismo aeropuerto.");
            return;
        }

        List<Airport> shortestPath = routeService.findShortestPath(origin, destination);
        if (shortestPath == null || shortestPath.isEmpty()) {
            pathResultLabel.setText("No existe ruta entre estos aeropuertos.");
            return;
        }

        double totalDistance = routeService.getShortestPathDistance(origin, destination);

        StringBuilder resultText = new StringBuilder("Ruta: ");
        for (int i = 0; i < shortestPath.size(); i++) {
            resultText.append(shortestPath.get(i).getCode());
            if (i < shortestPath.size() - 1) {
                resultText.append(" → ");
            }
        }

        resultText.append("\nDistancia total: ").append(String.format("%.2f", totalDistance)).append(" km");
        pathResultLabel.setText(resultText.toString());
    }

    /**
     * Maneja la acción de regresar al dashboard
     */
    @FXML
    void regresarOnAction(ActionEvent event) {
        try {
            Parent dashboardView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/admin_dashboard.fxml")));
            BorderPane borderPane = (BorderPane) ((Button) event.getSource()).getScene().getRoot();
            borderPane.setCenter(dashboardView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo volver al dashboard.");
        }
    }

    /**
     * Limpia los campos del formulario
     */
    private void clearForm() {
        originComboBox.getSelectionModel().clearSelection();
        destinationComboBox.getSelectionModel().clearSelection();
        distanceField.clear();
        routeTable.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    /**
     * Busca un aeropuerto por su código
     */
    private Airport findAirportByCode(String code) {
        for (Airport airport : originComboBox.getItems()) {
            if (airport.getCode().equals(code)) {
                return airport;
            }
        }
        return null;
    }

    /**
     * Muestra una alerta
     */
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Clase para mostrar aeropuertos en combobox
     */
    private static class AirportListCell extends ListCell<Airport> {
        @Override
        protected void updateItem(Airport item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
            } else {
                setText(item.getCode() + " - " + item.getName());
            }
        }
    }

    /**
     * Clase para mostrar rutas en la tabla
     */
    public static class RouteEntry {
        private final SimpleStringProperty origin;
        private final SimpleStringProperty destination;
        private final SimpleDoubleProperty distance;

        public RouteEntry(String origin, String destination, double distance) {
            this.origin = new SimpleStringProperty(origin);
            this.destination = new SimpleStringProperty(destination);
            this.distance = new SimpleDoubleProperty(distance);
        }

        public String getOrigin() {
            return origin.get();
        }

        public SimpleStringProperty originProperty() {
            return origin;
        }

        public String getDestination() {
            return destination.get();
        }

        public SimpleStringProperty destinationProperty() {
            return destination;
        }

        public double getDistance() {
            return distance.get();
        }

        public SimpleDoubleProperty distanceProperty() {
            return distance;
        }
    }
}