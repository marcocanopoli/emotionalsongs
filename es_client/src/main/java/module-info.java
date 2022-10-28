module es_client {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
    requires es_common;
    requires java.rmi;


    opens es_client to javafx.fxml;
    exports es_client;
}