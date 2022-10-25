package org.canos.es_server;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.sql.Connection;

public class EsServerController {
    @FXML
    private TextField dbHost;
    @FXML
    private Pane mainPane;
    @FXML
    private AnchorPane connectionStatus;
    @FXML
    private AnchorPane loginForm;
    @FXML
    private PasswordField dbPassword;
    @FXML
    private Button connectBtn;

    @FXML
    public void initialize() {

        connectBtn.setOnAction(event -> {
            String host = dbHost.getText();
            String password = dbPassword.getText();
            Connection conn = EsServer.setConnection(host, password);
            if (conn != null) {
                mainPane.getChildren().remove(loginForm);
                connectionStatus.setVisible(true);
            } else {
                dbHost.clear();
                dbPassword.clear();
            }
        });
    }

    //    @FXML
//    protected void onConnect() {
//        String host = dbHost.getText();
//        String password = dbPassword.getText();
//        ServerLogger.info(MessageFormat.format("Host: {0}, Password: {1}", host, password));
//    }
}