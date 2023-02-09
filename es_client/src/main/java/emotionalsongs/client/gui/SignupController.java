package emotionalsongs.client.gui;

import emotionalsongs.client.ClientApp;
import emotionalsongs.client.ClientContext;
import emotionalsongs.common.NodeHelpers;
import emotionalsongs.common.PasswordEncrypter;
import emotionalsongs.common.StringHelpers;
import emotionalsongs.common.User;
import emotionalsongs.common.exceptions.EncryptionException;
import emotionalsongs.common.interfaces.UserDAO;
import emotionalsongs.exceptions.RMIStubException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Controller per FXML dela modale di login.
 * e azioni di visualizzazione e aggiunta ad una playlist per ogni canzone
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 */
public class SignupController {
    @FXML
    public Button confirmRegistrationBtn;
    @FXML
    public TextField firstNameText;
    @FXML
    public TextField lastNameText;
    @FXML
    public TextField cfText;
    @FXML
    public TextField addressText;
    @FXML
    public TextField usernameText;
    @FXML
    public TextField emailText;
    @FXML
    public PasswordField pwdText;
    @FXML
    public PasswordField pwdConfirmText;
    @FXML
    public Label errorLabel;

    /**
     * Metodo di inizializzazione chiamato alla creazione della modale.
     * Setta i listener per la validazione e fromattazione degli input e mostra
     * gli errori di validazione, uno per volta.
     *
     * @see ClientContext
     */
    public void initialize() {

        TextField[] requiredFields = {firstNameText, lastNameText, cfText, usernameText, emailText, pwdText, pwdConfirmText};

        for (TextField field : requiredFields) {
            field.textProperty().addListener((obs, oldValue, newValue) ->
            {

                Map<String, TextField> validationErrors = validateInputs();
//                if (newValue.isEmpty()) validationErrors.put("blank_required", field);

                if (!validationErrors.isEmpty()) {
                    errorLabel.setVisible(true);

                    if (validationErrors.containsValue(field)) {
                        if (!field.getStyleClass().contains("border-error")) {
                            field.getStyleClass().add("border-error");
                        }

                        for (Map.Entry<String, TextField> fieldError :
                                validationErrors.entrySet()) {

                            if (field == fieldError.getValue()) {
                                errorLabel.setText(fieldError.getKey());
                                break;
                            }
                        }
                    } else {
                        errorLabel.setText((String) validationErrors.keySet().toArray()[0]);
                        field.getStyleClass().removeIf(style -> style.equals("border-error"));
                    }

                } else {
                    field.getStyleClass().removeIf(style -> style.equals("border-error"));
                    errorLabel.setVisible(false);
                }

                confirmRegistrationBtn.setDisable(!validationErrors.isEmpty() || newValue.isEmpty());

            });
        }

        cfText.setTextFormatter(new TextFormatter<>(change -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));

        emailText.setTextFormatter(new TextFormatter<>(change -> {
            change.setText(change.getText().toLowerCase());
            return change;
        }));

    }

    /**
     * Valida gli input forniti secondo i criteri scelti
     *
     * @return eventuali errori di validazione
     */
    private Map<String, TextField> validateInputs() {

        Map<String, TextField> inputs = setValidationFields();
        Map<String, String> errorMessages = setValidationMessages();

        Map<String, TextField> errors = new LinkedHashMap<>();

        for (Map.Entry<String, TextField> entry :
                inputs.entrySet()) {
            TextField field = entry.getValue();
            String text = field.getText();
            String key = entry.getKey();

            if (text.isBlank()) {
                errors.put("Il campo '" + key + "' non può essere vuoto", field);
            } else {

                switch (key) {
                    case "Codice Fiscale" -> {
                        if (StringHelpers.invalidRegExMatch("^[A-Z]{6}[A-Z0-9]{2}[A-Z][A-Z0-9]{2}[A-Z][A-Z0-9]{3}[A-Z]$", text))
                            errors.put(errorMessages.get(key), field);
                    }
                    case "Email" -> {
                        if (StringHelpers.invalidRegExMatch("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", text))
                            errors.put(errorMessages.get(key), field);
                    }
                    case "Password" -> {
//                    At least one upper case English letter, (?=.*?[A-Z])
//                        At least one lower case English letter, (?=.*?[a-z])
//                        At least one digit, (?=.*?[0-9])
//                        At least one special character, (?=.*?[#?!@$%^&*-])
//                        Minimum eight in length .{8,} (with the anchors)
                        if (StringHelpers.invalidRegExMatch("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$", text))
                            errors.put(errorMessages.get(key), field);

                        String pwd = pwdText.getText().trim();
                        String pwdConfirm = pwdConfirmText.getText().trim();

                        if (!pwd.equals(pwdConfirm)) {
                            errors.put(errorMessages.get("Conferma Password"), pwdConfirmText);
                        }

                    }
                    default -> {
                    }
                }
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
        inputs.put("Nome", firstNameText);
        inputs.put("Cognome", lastNameText);
        inputs.put("Codice Fiscale", cfText);
        inputs.put("Username", usernameText);
        inputs.put("Email", emailText);
        inputs.put("Password", pwdText);
        inputs.put("Conferma Password", pwdConfirmText);

        return inputs;
    }

    /**
     * Definisce i messaggi di validazione da mostrare nel form
     *
     * @return una mappa di mesaggi di errore
     */
    private Map<String, String> setValidationMessages() {
        Map<String, String> errorMessages = new HashMap<>();
        errorMessages.put("Codice Fiscale", "Il codice fiscale non è valido");
        errorMessages.put("Email", "L'indirizzo email non è valido");
        errorMessages.put("Conferma Password", "Le password non coincidono");
        errorMessages.put("Password",
                """
                        La password deve essere lunga almeno 8 caratteri.
                        Deve contenere almeno una lettera maiuscola, una
                        minuscola, una cifra ed un simbolo tra #?!@$%^&*-
                        """);
        return errorMessages;
    }

    /**
     * Effettua la registrazione a DB dell'utente inserito.
     * Mostra un'alert se l'utente è già esistente.
     */
    @FXML
    private void performSignup() {
        UserDAO userDAO = ClientApp.getUserDAO();

        String firstName = firstNameText.getText().trim();
        String lastName = lastNameText.getText().trim();
        String cf = cfText.getText().trim();
        String address = addressText.getText().trim();
        String username = usernameText.getText().trim();
        String email = emailText.getText().trim();
        String pwd = pwdText.getText().trim();

        Stage owner = (Stage) confirmRegistrationBtn.getScene().getWindow();

        try {
            String hashedPwd = PasswordEncrypter.encryptPassword(pwd);
            boolean userAdded = userDAO.addUser(firstName, lastName, cf, address, username, email, hashedPwd);

            if (userAdded) {
                User user = userDAO.getUser(username, pwd);
                ClientContext context = ClientContext.getInstance();
                context.setUser(user);

                owner.close();
            } else {
                NodeHelpers.createAlert(owner,
                        Alert.AlertType.WARNING,
                        "Utente già esistente",
                        "Esiste già un utente corrispondente ai dati immessi",
                        "Controllare username, email e codice fiscale e riprovare",
                        true);
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new EncryptionException(owner, e);
        } catch (RemoteException e) {
            throw new RMIStubException(e);
        }

    }
}
