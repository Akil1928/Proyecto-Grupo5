module ucr.lab.proyectogrupo5 {
    requires javafx.controls;
    requires javafx.fxml;


    opens ucr.lab.app to javafx.fxml;
    exports ucr.lab.app;
    exports controller;
    opens controller to javafx.fxml;
}