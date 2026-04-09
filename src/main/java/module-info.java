module com.gestiontransporte.sistemadegestiondetransporte {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.google.gson;
    requires java.desktop;
    requires com.brunomnsilva.smartgraph;

    opens com.gestiontransporte.sistemadegestiondetransporte to javafx.fxml;
    opens com.gestiontransporte.sistemadegestiondetransporte.ui to javafx.fxml;
    exports com.gestiontransporte.sistemadegestiondetransporte;

    opens com.gestiontransporte.sistemadegestiondetransporte.persistencia to com.google.gson;
    opens com.gestiontransporte.sistemadegestiondetransporte.modelo to com.google.gson;
    exports com.gestiontransporte.sistemadegestiondetransporte.modelo;
    exports com.gestiontransporte.sistemadegestiondetransporte.persistencia;
}