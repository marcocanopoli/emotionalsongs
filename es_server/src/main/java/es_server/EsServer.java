package es_server;

import es_database.UserServiceImpl;
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

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(EsServer.class.getResource("/gui/db-login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);
        stage.setTitle("Accesso al database");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        stage.setOnCloseRequest(event -> {
            try {
                shutdown();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public static void shutdown() throws RemoteException {
        Registry registry = LocateRegistry.getRegistry();
        try {
            registry.unbind("UserService");
        } catch (NotBoundException ignored) {

        }
        new Thread(() -> {
            ServerLogger.debug("Shutting down...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            System.exit(0);
        }).start();

    }

    public static void main(String[] args) throws RemoteException {

        Registry registry = LocateRegistry.createRegistry(1099);
        UserServiceImpl userService = new UserServiceImpl(registry);
        ServerLogger.info("Server initialised");

        launch();
    }
}
