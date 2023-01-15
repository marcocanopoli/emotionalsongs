package client_gui;

import client.ClientApp;
import client.ClientLogger;
import common.interfaces.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.rmi.RemoteException;

public class LoginController {

    @FXML
    public TextField username;
    @FXML
    public PasswordField pwd;
    @FXML
    public Button confirmLoginBtn;

    public void initialize() {

//        if (ClientApp.user != null) {
//            username.setDisable(true);
//            pwd.setDisable(true);
//            confirmLoginBtn.setText("Logout");
//        }

        confirmLoginBtn.setOnAction(event -> {

            UserDAO userDAO = ClientApp.getUserDAO();

            try {
                ClientApp.user = userDAO.getUser(username.getText(), pwd.getText());

                ClientLogger.debug("LoggedUser = " + (ClientApp.user != null ? String.valueOf(ClientApp.user) : "null"));

                ((Stage) confirmLoginBtn.getScene().getWindow()).close();

                ClientApp.initLayout("rootLayout");

            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }

        });
    }

}