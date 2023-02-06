package emotionalsongs.exceptions;

import emotionalsongs.client.ClientLogger;
import emotionalsongs.common.NodeHelpers;
import javafx.scene.control.Alert;

import java.util.Arrays;

/**
 * Eccezione riguardante l'accesso al registry RMI.
 * Mostra un'alert e logga l'errore
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 */
public class RMIRegistryNotFoundException extends RuntimeException {

    public RMIRegistryNotFoundException(Throwable err) {
        ClientLogger.error("Registo RMI non trovato: " + Arrays.toString(err.getStackTrace()));

        NodeHelpers.createAlert(null,
                Alert.AlertType.ERROR,
                "RMI registry not found",
                "Si è verificato un errore di comunicazione con il registro del server: ",
                err.getMessage(),
                true);
    }

}
