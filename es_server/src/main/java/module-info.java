module org.canos.es_server {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires org.apache.logging.log4j;
    requires java.sql;
    requires org.canos.es_common;


    opens org.canos.es_server to javafx.fxml;
    exports org.canos.es_server;
}