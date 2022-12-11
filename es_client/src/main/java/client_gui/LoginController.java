package client_gui;

import client.EsClientMain;
import common.interfaces.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    public TextField username;
    @FXML
    public PasswordField pwd;
    @FXML
    public Button confirmLoginBtn;

    public void initialize() {

        confirmLoginBtn.setOnAction(event -> {
            UserService userService = EsClientMain.getUserService();
//            try {
//                userService.addUser();
//            } catch (RemoteException e) {
//                throw new RuntimeException(e);
//            }

        });
    }
}