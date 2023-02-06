package emotionalsongs.common.exceptions;

import emotionalsongs.common.NodeHelpers;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class EncryptionException extends RuntimeException {
    public EncryptionException(Stage owner, Throwable err) {
        NodeHelpers.createAlert(owner,
                Alert.AlertType.ERROR,
                "Errore di criptazione",
                "Si è verificato un un errore di codifica della password ",
                err.getMessage(),
                true);
    }
}
