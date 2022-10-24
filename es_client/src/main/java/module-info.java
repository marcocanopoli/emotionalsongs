module org.canos.es_client {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.canos.es_client to javafx.fxml;
    exports org.canos.es_client;
}