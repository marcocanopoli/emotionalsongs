package client_gui;

import client.EsClientMain;
import common.interfaces.UserDAO;
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
            UserDAO userDAO = EsClientMain.getUserDAO();
//            try {
//                userService.addUser();
//            } catch (RemoteException e) {
//                throw new RuntimeException(e);
//            }

        });
    }
}