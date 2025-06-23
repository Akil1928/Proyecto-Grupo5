module ucr.lab.proyectogrupo5 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires itextpdf;

    opens ucr.lab.app to javafx.fxml;
    exports ucr.lab.app;

    opens controller to javafx.fxml;
    exports controller;

    opens security to javafx.fxml;
    exports security;

    opens domain to javafx.base, com.google.gson;
    exports domain;

    opens datastructure.circular to javafx.fxml;
    exports datastructure.circular;

    opens services to javafx.fxml;
    exports services;

    opens persistence to com.google.gson;
}