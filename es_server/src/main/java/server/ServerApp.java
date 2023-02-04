package server;

import common.NodeHelpers;
import database.EmotionDAOImpl;
import database.PlaylistDAOImpl;
import database.SongDAOImpl;
import database.UserDAOImpl;
import javafx.application.Application;
import javafx.stage.Stage;

import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;

/**
 * Classe principale che permette di avviare l'applicazione EmotionalSongs server.
 * Estende la classe Application, entry point di un'applicazione JavaFX
 * Include il riferimento ai file FXML utilizzato per creare il layout di base e
 * la creazione degli stub RMI per la comunicazione con i client
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 * @see ServerMain
 */
public class ServerApp extends Application {
    private static Connection conn = null;
    public static final URL dbLoginURL = ServerApp.class.getResource("/server_gui/rootLayout.fxml");

    public static synchronized Connection getConnection() {
        return conn;
    }

    public static synchronized void setConnection(Connection connection) {
        conn = connection;
    }

    /**
     * Entry point dell'applicazione JavaFX, chiamato dall'inizializzazione del thread Application
     *
     * @param stage il <strong>primary stage</strong> dell'applicazione
     *              sul quale settare la <strong>scene</strong> principale
     *              L'applicazione può creare altri stage, che non saranno principali
     */
    @Override
    public void start(Stage stage) {

        stage.setResizable(false);

        stage.setOnCloseRequest(event -> shutdown());

        NodeHelpers.createMainStage(stage, dbLoginURL, "Avvio server", null, null);

    }

    /**
     * Rimuove le associazioni degli stub RMI dal registro e chiude l'applicazione dopo un timoute di 1000ms
     */
    public static void shutdown() {
        try {
            Registry registry = LocateRegistry.getRegistry();
            registry.unbind("UserService");
            registry.unbind("SongService");
            registry.unbind("EmotionService");
            registry.unbind("PlaylistService");
        } catch (NotBoundException e) {
            ServerLogger.debug("Service not bound, skipping");
        } catch (RemoteException e) {
            ServerLogger.debug("Registry not found: " + e);

        }

        new Thread(() -> {
            ServerLogger.info("Shutting down...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                ServerLogger.error("Shutdown was interrupted: " + e);
            }
            System.exit(0);
        }).start();

    }

    /**
     * Classe main dell'applicazione che lancia il thread Application.
     * Rinominata in <code>appStart</code> per mantenere univoco il metodo main.
     * E' chiamato dalla classe wrapper <code>ServerMain</code>
     *
     * @param args argomenti di avvio
     * @see ServerMain
     */
    public static void startApp(String[] args) {
        try {

            Registry registry = LocateRegistry.createRegistry(1099);
            PlaylistDAOImpl playlistService = new PlaylistDAOImpl(registry);
            EmotionDAOImpl emotionService = new EmotionDAOImpl(registry);
            SongDAOImpl songService = new SongDAOImpl(registry);
            UserDAOImpl userService = new UserDAOImpl(registry);
            ServerLogger.debug("DAOs registered");

            launch();
        } catch (RemoteException e) {
            ServerLogger.error("Impossibile lanciare l'applicazione: " + e);
        }
    }
}
