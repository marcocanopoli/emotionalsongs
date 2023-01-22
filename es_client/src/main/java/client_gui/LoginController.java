package client_gui;

import client.ClientApp;
import client.ClientContext;
import client.ClientLogger;
import common.User;
import common.interfaces.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.RemoteException;

public class LoginController {

    @FXML
    public TextField username;
    @FXML
    public PasswordField pwd;
    @FXML
    public Button confirmLoginBtn;

    public void initialize() {

//        if (ClientContext.getUser() != null) {
//            username.setDisable(true);
//            pwd.setDisable(true);
//            confirmLoginBtn.setText("Logout");
//        }

        confirmLoginBtn.setOnAction(event -> {

            UserDAO userDAO = ClientApp.getUserDAO();

            try {
                User user = userDAO.getUser(username.getText(), pwd.getText());
                ClientContext context = ClientContext.getInstance();
                context.setUser(user);

                ClientLogger.debug("LoggedUser = " + (user != null ? String.valueOf(user) : "null"));

                ((Stage) confirmLoginBtn.getScene().getWindow()).close();

                ClientApp.initLayout("rootLayout");

            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }

        });
    }


    @FXML
    private void sendData(MouseEvent event, User user) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();

        try {
            Parent root = FXMLLoader.load(LoginController.class.getResource("/client_gui/loginView.fxml"));
            ClientContext context = ClientContext.getInstance();
            context.setUser(user);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println(String.format("Error: %s", e.getMessage()));
        }
    }

}