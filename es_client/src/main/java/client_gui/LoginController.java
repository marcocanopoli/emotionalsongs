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
        ClientContext context = ClientContext.getInstance();

//        if (ClientContext.getUser() != null) {
//            username.setDisable(true);
//            pwd.setDisable(true);
//            confirmLoginBtn.setText("Logout");
//        }

        confirmLoginBtn.setOnAction(event -> {

            UserDAO userDAO = ClientApp.getUserDAO();

            try {
                User user = userDAO.getUser(username.getText(), pwd.getText());
                context.setUser(user);

                ClientLogger.debug("LoggedUser = " + (user != null ? String.valueOf(user) : "null"));

                ((Stage) confirmLoginBtn.getScene().getWindow()).close();

            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }

        });
    }

}