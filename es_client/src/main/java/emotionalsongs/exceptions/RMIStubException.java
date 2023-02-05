package emotionalsongs.exceptions;

import emotionalsongs.client.ClientLogger;
import emotionalsongs.common.NodeHelpers;
import emotionalsongs.common.interfaces.EmotionDAO;
import emotionalsongs.common.interfaces.PlaylistDAO;
import emotionalsongs.common.interfaces.SongDAO;
import emotionalsongs.common.interfaces.UserDAO;
import javafx.scene.control.Alert;

import java.util.Arrays;

/**
 * Eccezione riguardante l'accesso agli stub RMI.
 * Mostra un'alert e logga l'errore
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 * @see EmotionDAO
 * @see PlaylistDAO
 * @see SongDAO
 * @see UserDAO
 */
public class RMIStubException extends RuntimeException {

    public RMIStubException(Throwable err) {
        ClientLogger.error("Bind dello stub non presente: " + Arrays.toString(err.getStackTrace()));

        NodeHelpers.createAlert(
                Alert.AlertType.ERROR,
                "RMI stub not found",
                "Si è verificato un errore di comunicazione con il server",
                err.getMessage(),
                true);
    }

}
