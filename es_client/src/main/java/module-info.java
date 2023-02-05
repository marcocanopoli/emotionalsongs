module es_client {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
    requires java.rmi;
    requires java.sql;
    requires java.desktop;
    requires es_common;


    opens emotionalsongs.client to javafx.fxml;
    exports emotionalsongs.client;
    exports emotionalsongs.client.gui;
    opens emotionalsongs.client.gui to javafx.fxml;
    exports emotionalsongs.client.gui.components;
    opens emotionalsongs.client.gui.components to javafx.fxml;

}