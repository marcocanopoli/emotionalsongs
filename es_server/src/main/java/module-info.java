module es_server {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires org.apache.logging.log4j;
    requires java.sql;
    requires es_common;


    opens emotionalsongs.server to javafx.fxml;
    exports emotionalsongs.server;
    exports emotionalsongs.server.gui;
    opens emotionalsongs.server.gui to javafx.fxml;
}