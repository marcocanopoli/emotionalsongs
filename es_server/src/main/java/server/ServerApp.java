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

public class ServerApp extends Application {
    private static Connection conn = null;
    public static final URL dbLoginURL = ServerApp.class.getResource("/server_gui/dbLoginView.fxml");

    public static synchronized Connection getConnection() {
        return conn;
    }

    public static synchronized void setConnection(Connection connection) {
        conn = connection;
    }

    @Override
    public void start(Stage stage) {

        stage.setResizable(false);

        stage.setOnCloseRequest(event -> {
            try {
                shutdown();
            } catch (RemoteException e) {
                ServerLogger.debug("Shutdown exception: " + e);
            }
        });

        NodeHelpers.createMainStage(stage, dbLoginURL, "Avvio server", 330, 300);

    }

    public static void shutdown() throws RemoteException {
        Registry registry = LocateRegistry.getRegistry();
        try {
            registry.unbind("UserService");
            registry.unbind("SongService");
            registry.unbind("EmotionService");
            registry.unbind("PlaylistService");
        } catch (NotBoundException e) {
            ServerLogger.debug("UserService not bound, skipping");
        }

        new Thread(() -> {
            ServerLogger.debug("Shutting down...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                ServerLogger.debug("Shutdown was interrupted: " + e);
            }
            System.exit(0);
        }).start();

    }

    public static void register(String[] args) throws RemoteException {

        Registry registry = LocateRegistry.createRegistry(1099);
        PlaylistDAOImpl playlistService = new PlaylistDAOImpl(registry);
        EmotionDAOImpl emotionService = new EmotionDAOImpl(registry);
        SongDAOImpl songService = new SongDAOImpl(registry);
        UserDAOImpl userService = new UserDAOImpl(registry);
        ServerLogger.info("Server initialised");

        launch();
    }
}
