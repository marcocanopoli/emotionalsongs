package server;

import database.SongServiceImpl;
import database.UserServiceImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;

public class EsServer extends Application {
    private static Connection conn = null;

    public static synchronized Connection getConnection() {
        return conn;
    }

    public static synchronized void setConnection(Connection conn) {
        EsServer.conn = conn;
    }

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(EsServer.class.getResource("/server_gui/dbLoginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);
        stage.setTitle("Accesso al database");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        stage.setOnCloseRequest(event -> {
            try {
                shutdown();
            } catch (RemoteException e) {
                ServerLogger.debug("Shutdown exception: " + e);
            }
        });

    }

    public static void shutdown() throws RemoteException {
        Registry registry = LocateRegistry.getRegistry();
        try {
            registry.unbind("UserService");
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

    public static void main(String[] args) throws RemoteException {

        Registry registry = LocateRegistry.createRegistry(1099);
        UserServiceImpl userService = new UserServiceImpl(registry);
        SongServiceImpl songService = new SongServiceImpl(registry);
        ServerLogger.info("Server initialised");

        launch();
    }
}
