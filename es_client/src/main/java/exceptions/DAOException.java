package exceptions;

import client.ClientLogger;
import common.NodeHelpers;
import javafx.scene.control.Alert;

import java.util.Arrays;

/**
 * Eccezione riguardante l'accesso agli stub RMI.
 * Mostra un'alert e logga l'errore
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 */
public class DAOException extends RuntimeException {

    public DAOException(Throwable err) {
        ClientLogger.error("DAOException: " + Arrays.toString(err.getStackTrace()));

        NodeHelpers.createAlert(
                Alert.AlertType.ERROR,
                "DAO exception",
                "Si è verificato un errore di comunicazione con il server",
                err.getMessage(),
                true);
    }

}
