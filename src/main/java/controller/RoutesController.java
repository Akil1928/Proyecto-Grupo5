package controller;

import domain.Airport;
import domain.Route;
import datastructure.list.SinglyLinkedList;
import datastructure.list.ListException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import services.AirportService;
import services.RouteService;

import java.io.IOException;
import java.net.URL;
import java.util.EventObject;
import java.util.ResourceBundle;

public class RoutesController implements Initializable {

    @FXML private TableView<Route> routesTable;
    @FXML private TableColumn<Route, String> originColumn;
    @FXML private TableColumn<Route, String> destinationColumn;
    @FXML private TableColumn<Route, Integer> distanceColumn;

    @FXML private ComboBox<Airport> originComboBox;
    @FXML private ComboBox<Airport> destinationComboBox;
    @FXML private TextField distanceField;

    @FXML private ComboBox<Airport> sourceAirportCombo;
    @FXML private ComboBox<Airport> targetAirportCombo;

    @FXML private Label routeResultLabel;
    @FXML private Label distanceResultLabel;
    @FXML private VBox resultsContainer;

    private final AirportService airportService = AirportService.getInstance();
    private final RouteService routeService = RouteService.getInstance();

    private Route selectedRoute;
    private EventObject event;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupColumns();
        loadAirports();
        loadRoutes();

        routesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            selectedRoute = newSel;
            if (newSel != null) {
                fillFieldsFromRoute(newSel);
            }
        });
    }

    private void setupColumns() {
        originColumn.setCellValueFactory(data -> data.getValue().originProperty());
        destinationColumn.setCellValueFactory(data -> data.getValue().destinationProperty());
        distanceColumn.setCellValueFactory(data -> data.getValue().distanceProperty().asObject());
    }

    private void loadAirports() {
        try {
            SinglyLinkedList<Airport> airportList = airportService.listAirports("active");

            ObservableList<Airport> airportObservableList = FXCollections.observableArrayList();
            for (int i = 1; i <= airportList.size(); i++) {
                airportObservableList.add((Airport) airportList.getNode(i).data);
            }

            setAirportComboBox(originComboBox, airportObservableList);
            setAirportComboBox(destinationComboBox, airportObservableList);
            setAirportComboBox(sourceAirportCombo, airportObservableList);
            setAirportComboBox(targetAirportCombo, airportObservableList);

        } catch (ListException e) {
            showAlert(Alert.AlertType.ERROR, "Error al cargar aeropuertos", e.getMessage());
        }
    }

    private void setAirportComboBox(ComboBox<Airport> comboBox, ObservableList<Airport> list) {
        comboBox.setItems(list);
        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Airport airport) {
                if (airport == null) return "";
                return airport.getCode() + " - " + airport.getName();
            }

            @Override
            public Airport fromString(String s) {
                return null;
            }
        });
    }

    private void loadRoutes() {
        routesTable.getItems().clear();
        routesTable.getItems().addAll(routeService.getRoutes());
    }

    private void fillFieldsFromRoute(Route route) {
        originComboBox.getSelectionModel().select(findAirportByCode(route.getOrigin()));
        destinationComboBox.getSelectionModel().select(findAirportByCode(route.getDestination()));
        distanceField.setText(String.valueOf(route.getDistance()));
    }

    private Airport findAirportByCode(String code) {
        for (Airport airport : originComboBox.getItems()) {
            if (airport.getCode().equals(code)) return airport;
        }
        return null;
    }

    @FXML
    private void handleAddRoute() {
        Airport origin = originComboBox.getValue();
        Airport destination = destinationComboBox.getValue();
        String distanceStr = distanceField.getText();

        if (origin == null || destination == null || distanceStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos obligatorios", "Complete todos los campos para crear o modificar una ruta.");
            return;
        }

        if (origin.equals(destination)) {
            showAlert(Alert.AlertType.ERROR, "Error", "El origen y destino no pueden ser iguales.");
            return;
        }

        try {
            int distance = Integer.parseInt(distanceStr);

            Route newRoute = new Route(origin.getCode(), destination.getCode(), distance);
            routeService.addOrUpdateRoute(newRoute);
            loadRoutes();
            clearFields();

            showAlert(Alert.AlertType.INFORMATION, "Ruta guardada", "La ruta fue guardada correctamente.");

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Distancia inválida", "Ingrese una distancia numérica válida.");
        }
    }

    @FXML
    private void handleRemoveRoute() {
        if (selectedRoute != null) {
            routeService.removeRoute(selectedRoute);
            loadRoutes();
            clearFields();
            selectedRoute = null;
        } else {
            showAlert(Alert.AlertType.WARNING, "Ruta no seleccionada", "Seleccione una ruta para eliminar.");
        }
    }

    @FXML
    private void handleClearFields() {
        clearFields();
    }

    private void clearFields() {
        originComboBox.getSelectionModel().clearSelection();
        destinationComboBox.getSelectionModel().clearSelection();
        distanceField.clear();
        routesTable.getSelectionModel().clearSelection();
        selectedRoute = null;
    }

    @FXML
    private void handleSaveChanges() {
        routeService.saveRoutes();
        showAlert(Alert.AlertType.INFORMATION, "Cambios guardados", "Todas las rutas han sido guardadas en data/routes.json");
    }

    @FXML
    private void handleFindRoute() {
        Airport source = sourceAirportCombo.getValue();
        Airport target = targetAirportCombo.getValue();

        if (source == null || target == null) {
            showAlert(Alert.AlertType.WARNING, "Seleccione ambos aeropuertos", "Debe seleccionar un aeropuerto de origen y destino.");
            return;
        }

        var result = routeService.findShortestRoute(source.getCode(), target.getCode());
        if (result != null) {
            routeResultLabel.setText(result.getPathAsString());
            distanceResultLabel.setText(result.getTotalDistance() + " km");
            resultsContainer.setVisible(true);
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Ruta no encontrada", "No se encontró ninguna ruta entre los aeropuertos seleccionados.");
        }
    }

    @FXML
    private void handleAnalyzeConnectivity() {
        showAlert(Alert.AlertType.INFORMATION, "Conectividad", "Análisis de conectividad aún no implementado.");
    }

    @FXML
    private void regresarOnAction() {
        try {
            Parent dashboard = FXMLLoader.load(getClass().getResource("/dashboard.fxml"));
            BorderPane root = (BorderPane) ((Button)event.getSource()).getScene().getRoot();
            root.setCenter(dashboard);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
