package org.canos.es_server;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class EsServerController {
    @FXML
    private TextField dbHost;
    @FXML
    private PasswordField dbPassword;
    @FXML
    private Button connectBtn;

    @FXML
    public void initialize() {
        connectBtn.setOnAction(event -> {
            String host = dbHost.getText();
            String password = dbPassword.getText();
            EsServer.setConnection(host, password);
            dbHost.clear();
            dbPassword.clear();
        });
    }

    //    @FXML
//    protected void onConnect() {
//        String host = dbHost.getText();
//        String password = dbPassword.getText();
//        ServerLogger.info(MessageFormat.format("Host: {0}, Password: {1}", host, password));
//    }
}