module es_common {
    requires org.apache.logging.log4j;
    requires java.rmi;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;

    exports common;
    exports common.interfaces;
}