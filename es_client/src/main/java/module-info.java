module org.canos.es_client {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
    requires org.canos.es_common;
    requires java.rmi;


    opens org.canos.es_client to javafx.fxml;
    exports org.canos.es_client;
}