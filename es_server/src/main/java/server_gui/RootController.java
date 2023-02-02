package server_gui;

import common.NodeHelpers;
import database.DBManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import server.ServerApp;
import server.ServerLogger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class RootController {
    @FXML
    private VBox checkboxes;
    @FXML
    private CheckBox initDB;
    @FXML
    private CheckBox seedSongs;
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
    private void initialize() {
        connectBtn.setOnAction(event -> {
            try {
                connect();
            } catch (SQLException | IOException e) {
                ServerLogger.error("Unable to initialize database: " + e);
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    private void connect() throws SQLException, IOException {
        String host = dbHost.getText().trim();
        String database = dbName.getText().trim();
        String user = dbUser.getText().trim();
        String password = dbPassword.getText().trim();

        boolean dbCreated = false;

        if (initDB.isSelected()) {
            String oldDBName = NodeHelpers.createTextInputDialog(
                    "Inserisci nome database",
                    "Inserisci il nome del vecchio database per crearne uno nuovo",
                    "Nome DB: ",
                    database);

            if (oldDBName != null) {
                dbCreated = DBManager.createDB(host, oldDBName, database, user, password);
            } else {
                return;
            }
        }

        Connection conn = new DBManager().openConnection(host, database, user, password);
        ServerApp.setConnection(conn);

        if (dbCreated) {
            DBManager.migrate();
            ServerLogger.debug("Migrations executed");
            DBManager.seedUsers();
            DBManager.seedEmotions();
            ServerLogger.debug("Seeds executed");
            ServerLogger.debug("Database initialized");
        }

        if (seedSongs.isSelected()) {
            DBManager.seedSongs();
            ServerLogger.debug("Songs catalog initialized");
        }

        loginBox.getChildren().remove(loginForm);
        loginBox.getChildren().remove(checkboxes);
        loginBox.getChildren().remove(connectBtn);
        loginBox.getScene().getWindow().setHeight(120);
        title.setText("Connesso al database");
        title.setTextFill(Color.GREEN);
    }

}
