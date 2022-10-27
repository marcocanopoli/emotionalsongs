module es_server {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires org.apache.logging.log4j;
    requires java.sql;
    requires es_common;


    opens es_server to javafx.fxml;
    exports es_server;
    opens gui to javafx.fxml;
    exports gui;
}