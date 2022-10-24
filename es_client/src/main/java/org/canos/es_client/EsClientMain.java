package org.canos.es_client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.canos.es_common.interfaces.UserService;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class EsClientMain extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(EsClientMain.class.getResource("client-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Client login");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) throws RemoteException, NotBoundException {

        ClientLogger.debug("Client main");
        String host = args.length >= 1 ? args[0] : null;
        Registry registry = LocateRegistry.getRegistry(host);
        UserService userService = (UserService)
                registry.lookup("userService");
        List<String> emails = userService.getUsers();
        ClientLogger.info(emails.toString());

        launch();
    }
}
