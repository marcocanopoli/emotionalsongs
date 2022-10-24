package org.canos.es_server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.canos.es_database.UserServiceImpl;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class EsServer extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(EsServer.class.getResource("server-login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);
        stage.setTitle("Server login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws RemoteException {

        Registry registry = LocateRegistry.createRegistry(1099);
        UserServiceImpl userService = new UserServiceImpl(registry);
        ServerLogger.info("Server initialised");

        launch();


    }
}
