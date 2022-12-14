module es_client {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
    requires es_common;
    requires java.rmi;
    requires java.sql;
    requires es_server;
    requires java.desktop;


    opens client to javafx.fxml;
    exports client;
    exports client_gui;
    opens client_gui to javafx.fxml;
}