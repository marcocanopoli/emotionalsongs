module es_server {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires org.apache.logging.log4j;
    requires java.sql;
    requires es_common;


    opens server to javafx.fxml;
    exports server;
    opens server_gui to javafx.fxml;
    exports server_gui;
}