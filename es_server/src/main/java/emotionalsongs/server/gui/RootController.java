package emotionalsongs.server.gui;

import emotionalsongs.common.NodeHelpers;
import emotionalsongs.database.DBManager;
import emotionalsongs.server.ServerApp;
import emotionalsongs.server.ServerLogger;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Controller per FXML del layout di base dell'applicazione.
 * Mostra un form compilabile con le credenziali di accesso al database e
 * la scelta di esecuzioni aggiuntive quali inizializzazione del DB e seed del dataset di base.
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 */
public class RootController {
    @FXML
    private VBox checkboxes;
    @FXML
    private CheckBox initDB;
    @FXML
    private CheckBox deleteDB;
    @FXML
    private CheckBox seedSongs;
    @FXML
    private Label title;
    @FXML
    private TextField dbHost;
    @FXML
    private TextField dbPort;
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

    /**
     * Metodo di inizializzazione chiamato alla creazione del layout.
     * Setta il listener sulle checkbox per evitare l'eliminazione di un db senza
     * ricrearne uno nuovo
     */
    public void initialize() {

        deleteDB.disableProperty().bind(initDB.selectedProperty().not());
        connectBtn.disableProperty().bind(
                dbHost.textProperty().isEmpty().or(
                        dbPort.textProperty().isEmpty()).or(
                        dbName.textProperty().isEmpty()).or(
                        dbUser.textProperty().isEmpty()).or(
                        dbPassword.textProperty().isEmpty())
        );
    }

    /**
     * Stabiilisce una connessione con il db e ne segue l'inizializzazione e il seed dei dati
     */
    @FXML
    private void connect() {
        String host = dbHost.getText().trim();
        String port = dbPort.getText().trim();
        String database = dbName.getText().trim();
        String user = dbUser.getText().trim();
        String password = dbPassword.getText().trim();

        boolean dbCreated = false;
        boolean canceled = false;

        if (initDB.isSelected()) {
            if (deleteDB.isSelected()) {
                String oldDBName = NodeHelpers.createTextInputDialog(
                        "Inserisci nome database",
                        "Inserisci il nome del vecchio database"
                        ,
                        "Nome DB: ", database);

                if (oldDBName == null) {
                    canceled = true;
                } else {
                    oldDBName = oldDBName.isBlank() ? "emotionalsongs" : oldDBName;
                    dbCreated = DBManager.createDB(host, port, oldDBName, database, user, password);
                }
            } else {
                dbCreated = DBManager.createDB(host, port, null, database, user, password);
            }

        }

        Connection conn = new DBManager().openConnection(host, port, database, user, password);
        ServerApp.setConnection(conn);

        if (dbCreated) {
            DBManager.migrate();
            ServerLogger.debug("Migrations executed");
            DBManager.seedUsers();
            DBManager.seedEmotions();
            ServerLogger.debug("Seeds executed");
            ServerLogger.debug("Database initialized");
        }

        if (seedSongs.isSelected() && !canceled) {
            DBManager.seedSongs();
            ServerLogger.debug("Songs catalog initialized");
        }

//        DBManager.seedTestSongEmotions();

        boolean ready = (
                (initDB.isSelected() && dbCreated) ||
                        (!initDB.isSelected() && !dbCreated)) &&
                ServerApp.getConnection() != null;

        if (ready) {
            ServerLogger.info("Connected to database");
            loginBox.getChildren().remove(loginForm);
            loginBox.getChildren().remove(checkboxes);
            loginBox.getChildren().remove(connectBtn);
            loginBox.getScene().getWindow().setHeight(120);
            title.setText("Connesso al database");
            title.setTextFill(Color.GREEN);
        } else {
            try {
                conn.close();
            } catch (SQLException e) {
                ServerLogger.error("An error occurred while trying to close connection: " + e);
                ServerApp.shutdown();
            }
        }

    }

}
