package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.ReportService;
import javafx.concurrent.Task;

import java.io.File;

public class ReportController {

    @FXML private CheckBox chkAirports;
    @FXML private CheckBox chkRoutes;
    @FXML private CheckBox chkPassengers;
    @FXML private CheckBox chkOccupancy;
    @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleGenerateReport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte PDF");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Documentos PDF", "*.pdf"));

        fileChooser.setInitialFileName("reporte_estadistico_" + System.currentTimeMillis() + ".pdf");

        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            // Mostrar progreso
            progressBar.setVisible(true);
            statusLabel.setText("Generando reporte...");

            // Crear tarea en segundo plano
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    ReportService reportService = new ReportService();
                    reportService.generateCustomReport(
                            file.getAbsolutePath(),
                            chkAirports.isSelected(),
                            chkRoutes.isSelected(),
                            chkPassengers.isSelected(),
                            chkOccupancy.isSelected()
                    );
                    return null;
                }
            };

            // Manejar éxito
            task.setOnSucceeded(e -> {
                progressBar.setVisible(false);
                statusLabel.setText("Reporte generado exitosamente!");

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Reporte Generado");
                alert.setHeaderText(null);
                alert.setContentText("El reporte se ha guardado en:\n" + file.getAbsolutePath());

                // Agregar botón para abrir archivo
                ButtonType openButton = new ButtonType("Abrir Archivo", ButtonBar.ButtonData.OK_DONE);
                alert.getButtonTypes().setAll(openButton, ButtonType.CLOSE);

                alert.showAndWait().ifPresent(response -> {
                    if (response == openButton) {
                        openPDFFile(file);
                    }
                });
            });

            // Manejar errores
            task.setOnFailed(e -> {
                progressBar.setVisible(false);
                statusLabel.setText("Error generando reporte");

                Throwable ex = task.getException();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("No se pudo generar el reporte");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
                ex.printStackTrace();
            });

            // Ejecutar tarea
            new Thread(task).start();
        }
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }

    private void openPDFFile(File file) {
        try {
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(file);
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Abrir PDF");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo abrir el archivo automáticamente. Por favor ábralo manualmente.");
            alert.showAndWait();
        }
    }
}