package client_gui;

import client.ClientApp;
import client.ClientContext;
import client.ClientLogger;
import common.User;
import common.interfaces.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public void initialize() {

        TextField[] fieldsToValidate = {firstNameText, lastNameText, cfText, usernameText, emailText, pwdText, pwdConfirmText};

        for (TextField field : fieldsToValidate) {
            field.textProperty().addListener((obs, oldValue, newValue) ->
            {

                List<String> validationErrors = validateInputs();
                if (newValue.isEmpty()) validationErrors.add("blank_required");

                if (!validationErrors.isEmpty()) ClientLogger.error(validationErrors.toString());

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


        confirmRegistrationBtn.setOnAction(event -> {
            UserDAO userDAO = ClientApp.getUserDAO();

            String firstName = firstNameText.getText().trim();
            String lastName = lastNameText.getText().trim();
            String cf = cfText.getText().trim();
            String address = addressText.getText().trim();
            String username = usernameText.getText().trim();
            String email = emailText.getText().trim();
            String pwd = pwdText.getText().trim();
            String pwdConfirm = pwdConfirmText.getText().trim();

            try {
                if (
                        !firstName.isEmpty() &&
                                !lastName.isEmpty() &&
                                !cf.isEmpty() &&
                                !address.isEmpty() &&
                                !username.isEmpty() &&
                                !email.isEmpty() &&
                                !pwd.isEmpty() &&
                                !pwdConfirm.isEmpty() &&
                                pwd.trim().equals(pwdConfirm.trim())
                ) {
                    boolean userAdded = userDAO.addUser(firstName, lastName, cf, address, username, email, pwd);

                    if (userAdded) {
                        User user = userDAO.getUser(username, pwd);
                        ClientContext context = ClientContext.getInstance();
                        context.setUser(user);

                        ((Stage) confirmRegistrationBtn.getScene().getWindow()).close();
                    }
                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }

        });
    }

    boolean invalidRegExMatch(String regEx, String text) {
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(text);

        return !m.find();
    }

    private List<String> validateInputs() {

        Map<String, TextField> inputs = new HashMap<>();
        inputs.put("first_name", firstNameText);
        inputs.put("cf", cfText);
        inputs.put("last_name", lastNameText);
        inputs.put("username", usernameText);
        inputs.put("email", emailText);
        inputs.put("password", pwdText);
        inputs.put("password_confirm", pwdConfirmText);

        List<String> errors = new ArrayList<>();

        for (Map.Entry<String, TextField> entry :
                inputs.entrySet()) {
            String text = entry.getValue().getText();
            String key = entry.getKey();

            if (text.isBlank()) {
                errors.add("blank_" + key);
            } else {

                switch (key) {
                    case "cf" -> {
                        if (invalidRegExMatch("^[A-Z]{6}[A-Z0-9]{2}[A-Z][A-Z0-9]{2}[A-Z][A-Z0-9]{3}[A-Z]$", text))
                            errors.add(key);
                    }
                    case "email" -> {
                        if (invalidRegExMatch("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", text))
                            errors.add(key);
                    }
                    case "password" -> {
//                    At least one upper case English letter, (?=.*?[A-Z])
//                        At least one lower case English letter, (?=.*?[a-z])
//                        At least one digit, (?=.*?[0-9])
//                        At least one special character, (?=.*?[#?!@$%^&*-])
//                        Minimum eight in length .{8,} (with the anchors)
                        if (invalidRegExMatch("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$", text))
                            errors.add(key);

                        String pwd = pwdText.getText().trim();
                        String pwdConfirm = pwdConfirmText.getText().trim();

                        if (!pwd.equals(pwdConfirm)) {
                            errors.add(key + "_confirm");
                        }

                    }
                    default -> {
                    }
                }
            }
        }

        return errors;
    }
}
