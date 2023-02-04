package client_gui;

import client.ClientApp;
import client.ClientContext;
import common.NodeHelpers;
import common.StringHelpers;
import common.User;
import common.interfaces.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

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
     * @see client.ClientContext
     */
    public void initialize() {

        TextField[] requiredFields = {firstNameText, lastNameText, cfText, usernameText, emailText, pwdText, pwdConfirmText};

        for (TextField field : requiredFields) {
            field.textProperty().addListener((obs, oldValue, newValue) ->
            {

                Map<String, TextField> validationErrors = validateInputs();
                if (newValue.isEmpty()) validationErrors.put("blank_required", field);

                if (!validationErrors.isEmpty()) {
//                    ClientLogger.debug(validationErrors.toString());
//                    Map.Entry<String, TextField> firstEntry = validationErrors.entrySet().iterator().next();
//                    String error = firstEntry.getKey();
//                    TextField invalidField = firstEntry.getValue();

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
                        field.getStyleClass().removeIf(style -> style.equals("border-error"));
                    }

                } else {
                    field.getStyleClass().removeIf(style -> style.equals("border-error"));
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


//        cfText.setTextFormatter(new TextFormatter<>(change ->
//        {
//            if (change.getControlNewText().matches("([1-9][0-9]*)?")) {
//                return change;
//            } else {
//                return null;
//            }
//        }));

    }

    /**
     * Valida gli input forniti secondo i criteri scelti
     *
     * @return eventuali errori di validazione
     */
    private Map<String, TextField> validateInputs() {

        Map<String, TextField> inputs = new LinkedHashMap<>();
        inputs.put("first_name", firstNameText);
        inputs.put("last_name", lastNameText);
        inputs.put("cf", cfText);
        inputs.put("username", usernameText);
        inputs.put("email", emailText);
        inputs.put("password", pwdText);
        inputs.put("password_confirm", pwdConfirmText);

        Map<String, TextField> errors = new LinkedHashMap<>();

        for (Map.Entry<String, TextField> entry :
                inputs.entrySet()) {
            TextField field = entry.getValue();
            String text = field.getText();
            String key = entry.getKey();

            if (text.isBlank()) {
                errors.put("blank_" + key, field);
            } else {

                switch (key) {
                    case "cf" -> {
                        if (StringHelpers.invalidRegExMatch("^[A-Z]{6}[A-Z0-9]{2}[A-Z][A-Z0-9]{2}[A-Z][A-Z0-9]{3}[A-Z]$", text))
                            errors.put(key, field);
                    }
                    case "email" -> {
                        if (StringHelpers.invalidRegExMatch("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", text))
                            errors.put(key, field);
                    }
                    case "password" -> {
//                    At least one upper case English letter, (?=.*?[A-Z])
//                        At least one lower case English letter, (?=.*?[a-z])
//                        At least one digit, (?=.*?[0-9])
//                        At least one special character, (?=.*?[#?!@$%^&*-])
//                        Minimum eight in length .{8,} (with the anchors)
                        if (StringHelpers.invalidRegExMatch("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$", text))
                            errors.put(key, field);

                        String pwd = pwdText.getText().trim();
                        String pwdConfirm = pwdConfirmText.getText().trim();

                        if (!pwd.equals(pwdConfirm)) {
                            errors.put(key + "_confirm", pwdConfirmText);
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
//        String pwdConfirm = pwdConfirmText.getText().trim();

//            if (
//                    !firstName.isEmpty() &&
//                            !lastName.isEmpty() &&
//                            !cf.isEmpty() &&
//                            !address.isEmpty() &&
//                            !username.isEmpty() &&
//                            !email.isEmpty() &&
//                            !pwd.isEmpty() &&
//                            !pwdConfirm.isEmpty() &&
//                            pwd.trim().equals(pwdConfirm.trim())
//            )
//            {
        boolean userAdded = userDAO.addUser(firstName, lastName, cf, address, username, email, pwd);

        if (userAdded) {
            User user = userDAO.getUser(username, pwd);
            ClientContext context = ClientContext.getInstance();
            context.setUser(user);

            ((Stage) confirmRegistrationBtn.getScene().getWindow()).close();
        } else {
            NodeHelpers.createAlert(
                    Alert.AlertType.WARNING,
                    "Utente già esistente",
                    "Esiste già un utente corrispondente ai dati immessi",
                    "Controllare username, emai e codice fiscale e riprovare",
                    true);
        }
//            }
    }
}
