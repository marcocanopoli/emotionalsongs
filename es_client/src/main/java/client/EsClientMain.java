package client;

import common.interfaces.SongDAO;
import common.interfaces.UserDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
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
    private static String currentView;
    static UserDAO userDAO;
    static SongDAO songDAO;

    public static UserDAO getUserDAO() {
        return userDAO;
    }

    public static SongDAO getSongDAO() {
        return songDAO;
    }

    @Override
    public void start(Stage stage) {
        EsClientMain.window = stage;
        EsClientMain.window.setTitle("Emotional Songs");

        initRootLayout();
    }

    public void initRootLayout() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(EsClientMain.class.getResource("/client_gui/rootLayout.fxml"));
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

    public static void showSongsView(AnchorPane view) {
        try {
            if (EsClientMain.currentView == null || !EsClientMain.currentView.equals("songs")) {

                FXMLLoader loader = new FXMLLoader(EsClientMain.class.getResource("/client_gui/songsView.fxml"));
                SplitPane songsView = loader.load();

                view.getChildren().clear();
                view.getChildren().add(songsView);
                EsClientMain.currentView = "songs";
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws RemoteException, NotBoundException {

        ClientLogger.debug("Client main");
        String host = args.length >= 1 ? args[0] : null;
        Registry registry = LocateRegistry.getRegistry(host);
        userDAO = (UserDAO)
                registry.lookup("UserService");
        songDAO = (SongDAO)
                registry.lookup("SongService");

        launch();
    }
}
