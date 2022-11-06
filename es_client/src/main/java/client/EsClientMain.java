package client;

import common.interfaces.SongService;
import common.interfaces.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class EsClientMain extends Application {
    static UserService userService;
    static SongService songService;

    public static UserService getUserService() {
        return userService;
    }

    public static SongService getSongService() {
        return songService;
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(EsClientMain.class.getResource("/client_gui/client-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Client login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws RemoteException, NotBoundException {

        ClientLogger.debug("Client main");
        String host = args.length >= 1 ? args[0] : null;
        Registry registry = LocateRegistry.getRegistry(host);
        userService = (UserService)
                registry.lookup("UserService");
        songService = (SongService)
                registry.lookup("SongService");

        launch();
    }
}
