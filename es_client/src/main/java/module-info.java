module es_client {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
    requires java.rmi;
    requires java.sql;
    requires java.desktop;
    requires es_common;


    opens client to javafx.fxml;
    exports client;
    exports client_gui;
    opens client_gui to javafx.fxml;
}