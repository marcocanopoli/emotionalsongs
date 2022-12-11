package client_gui;

import client.EsClientMain;
import common.interfaces.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.rmi.RemoteException;

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

        confirmRegistrationBtn.setOnAction(event -> {
            UserService userService = EsClientMain.getUserService();

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
                                !pwdConfirm.isEmpty()
//                        pwd.equals(pwdConfirm)
                ) {
                    boolean userAdded = userService.addUser(firstName, lastName, cf, address, username, email, pwd);

                    if (userAdded) {
                        ((Stage) confirmRegistrationBtn.getScene().getWindow()).close();
                    }
                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }

        });
    }
}