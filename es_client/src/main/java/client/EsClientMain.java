package client;

import common.interfaces.SongService;
import common.interfaces.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class EsClientMain extends Application {

    private static Stage window;
    private AnchorPane topView;
    private AnchorPane bottomView;
    static UserService userService;
    static SongService songService;

    public static UserService getUserService() {
        return userService;
    }

    public static SongService getSongService() {
        return songService;
    }

    @Override
    public void start(Stage stage) {
        EsClientMain.window = stage;
        EsClientMain.window.setTitle("Emotional Songs");

        initRootLayout();
    }

    public void initRootLayout() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(EsClientMain.class.getResource("/client_gui/esClientRootLayout.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            EsClientMain.window.setScene(scene);
            EsClientMain.window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createStage(String resourceName, String title, boolean isModal) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(EsClientMain.class.getResource("/client_gui/" + resourceName));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.initOwner(EsClientMain.window);
            stage.initModality(isModal ? Modality.WINDOW_MODAL : Modality.NONE);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showSignupView() {
        createStage("signupView.fxml", "Registrazione utente", true);
    }

    public static void showLoginView() {
        createStage("signupView.fxml", "Registrazione utente", true);
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
