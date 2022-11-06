package server_gui;

import database.DBManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import server.EsServer;
import server.ServerLogger;

import java.sql.Connection;

public class EsServerController {

    @FXML
    private Label title;
    @FXML
    private TextField dbHost;
    @FXML
    private TextField dbName;
    @FXML
    private TextField dbUser;
    @FXML
    private PasswordField dbPassword;
    @FXML
    private Pane mainPane;
    @FXML
    private GridPane loginForm;
    @FXML
    private Button connectBtn;

    @FXML
    public void initialize() {

        connectBtn.setOnAction(event -> {
            String host = dbHost.getText();
            String database = dbName.getText();
            String user = dbUser.getText();
            String password = dbPassword.getText();
            DBManager dbManager = new DBManager();
            Connection conn = dbManager.openConnection(host, database, user, password);
            if (conn != null) {
                EsServer.setConnection(conn);
                ServerLogger.debug("Connection set");
                DBManager.migrate();
                ServerLogger.debug("Migrations executed");
//                DBManager.seed();
//                ServerLogger.debug("Seeds executed");
                mainPane.getScene().getWindow().setHeight(120);
                mainPane.getChildren().remove(loginForm);
                mainPane.getChildren().remove(connectBtn);
                title.setText("Connesso al database");
                title.setTextFill(Color.GREEN);
            }
        });
    }

    //    @FXML
//    protected void onConnect() {
//        String host = dbUser.getText();
//        String password = dbPassword.getText();
//        ServerLogger.info(MessageFormat.format("Host: {0}, Password: {1}", host, password));
//    }
}