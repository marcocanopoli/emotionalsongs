module es_common {
    requires org.apache.logging.log4j;
    requires java.rmi;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;

    exports emotionalsongs.common;
    exports emotionalsongs.common.interfaces;
    exports emotionalsongs.common.exceptions;
}