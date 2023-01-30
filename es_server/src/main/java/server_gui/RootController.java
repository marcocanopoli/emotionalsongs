package server_gui;

import database.DBManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import server.ServerApp;
import server.ServerLogger;

import java.sql.Connection;
import java.sql.SQLException;

public class RootController {

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
    private VBox loginBox;
    @FXML
    private GridPane loginForm;
    @FXML
    private Button connectBtn;

    @FXML
    public void initialize() {


    }


    @FXML
    protected void onConnect() {
        connectBtn.setOnAction(event -> {
            String host = dbHost.getText();
            String database = dbName.getText();
            String user = dbUser.getText();
            String password = dbPassword.getText();
            DBManager dbManager = new DBManager();
            Connection conn = dbManager.openConnection(host, database, user, password);
            
            if (conn != null) {
                ServerApp.setConnection(conn);
                ServerLogger.debug("Connection set");
                DBManager.migrate();
                ServerLogger.debug("Migrations executed");
                try {
                    DBManager.seed();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                ServerLogger.debug("Seeds executed");
                loginBox.getScene().getWindow().setHeight(120);
                loginBox.getChildren().remove(loginForm);
                loginBox.getChildren().remove(connectBtn);
                title.setText("Connesso al database");
                title.setTextFill(Color.GREEN);
            }
        });
    }
}