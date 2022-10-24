module com.example.es_server {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.es_server to javafx.fxml;
    exports com.example.es_server;
}