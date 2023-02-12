package emotionalsongs.client.gui;

import emotionalsongs.client.ClientApp;
import emotionalsongs.client.ClientContext;
import emotionalsongs.common.NodeHelpers;
import emotionalsongs.common.interfaces.SongDAO;
import emotionalsongs.exceptions.RMIStubException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Controller per FXML dela modale di login.
 * e azioni di visualizzazione e aggiunta ad una playlist per ogni canzone
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 */
public class NewSongController {

    @FXML
    private Button confirmRegistrationBtn;
    @FXML
    private TextField titleText;
    @FXML
    private TextField authorText;
    @FXML
    private TextField albumText;
    @FXML
    private TextField yearText;
    @FXML
    private TextField genreText;
    @FXML
    private TextField durationText;

    /**
     * Metodo di inizializzazione chiamato alla creazione della modale.
     * Setta i listener per la validazione e fromattazione degli input.
     *
     * @see ClientContext
     */
    public void initialize() {

        TextField[] requiredFields = {titleText, authorText};

        for (TextField field : requiredFields) {
            field.textProperty().addListener((obs, oldValue, newValue) ->
            {

                Map<String, TextField> validationErrors = validateInputs();

                if (!validationErrors.isEmpty()) {
                    if (validationErrors.containsValue(field)) {
                        if (!field.getStyleClass().contains("border-error")) {
                            field.getStyleClass().add("border-error");
                        }

                    } else {

                        field.getStyleClass().removeIf(style -> style.equals("border-error"));
                    }

                } else {
                    field.getStyleClass().removeIf(style -> style.equals("border-error"));
                }

                confirmRegistrationBtn.setDisable(!validationErrors.isEmpty() || newValue.isEmpty());

            });
        }

        yearText.setTextFormatter(new TextFormatter<>(change ->
        {
            if (change.getControlNewText().matches("[0-9]*") &&
                    change.getControlNewText().length() <= 4) {
                return change;
            } else {
                return null;
            }
        }));

        durationText.setTextFormatter(new TextFormatter<>(change ->
        {
            if (change.getControlNewText().matches("[0-9]*") &&
                    change.getControlNewText().length() <= 5) {
                return change;
            } else {
                return null;
            }
        }));

    }

    /**
     * Valida gli input forniti secondo i criteri scelti
     *
     * @return eventuali errori di validazione
     */
    private Map<String, TextField> validateInputs() {

        Map<String, TextField> inputs = setValidationFields();

        Map<String, TextField> errors = new LinkedHashMap<>();

        for (Map.Entry<String, TextField> entry :
                inputs.entrySet()) {
            TextField field = entry.getValue();
            String text = field.getText();
            String key = entry.getKey();

            if (text.isBlank()) {
                errors.put("Il campo '" + key + "' non può essere vuoto", field);
            }
        }

        return errors;
    }

    /**
     * Definisce gli input da validare
     *
     * @return una mappa di input
     */
    private Map<String, TextField> setValidationFields() {
        Map<String, TextField> inputs = new LinkedHashMap<>();
        inputs.put("Titolo", titleText);
        inputs.put("Autore", authorText);

        return inputs;
    }

    /**
     * Effettua la registrazione a DB della canzone inserita.
     */
    @FXML
    private void performSongRegistration() {
        SongDAO songDAO = ClientApp.getSongDAO();

        String title = titleText.getText().trim();
        String author = authorText.getText().trim();
        String album = albumText.getText().trim();
        int year = Integer.parseInt(yearText.getText());
        String genre = genreText.getText().trim();
        int duration = Integer.parseInt(durationText.getText());

        Stage owner = (Stage) confirmRegistrationBtn.getScene().getWindow();

        try {
            boolean songAdded = songDAO.addSong(title, author, album, year, genre, duration);

            if (songAdded) {
                NodeHelpers.createAlert(owner,
                        Alert.AlertType.INFORMATION,
                        "Canzone aggiunta al catalogo",
                        "La canzone è stata aggiunta al catalogo!",
                        null,
                        true);
                owner.close();
            }
        } catch (RemoteException e) {
            throw new RMIStubException(e);
        }

    }
}
