package client;

import client_gui.RatingController;
import client_gui.SongsController;
import common.User;
import common.interfaces.PlaylistDAO;
import common.interfaces.SongDAO;
import common.interfaces.UserDAO;
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

public class ClientApp extends Application {

    private static Stage window;
    //    public static Song currentSong;

    public static SongsController songsController;
    public static RatingController ratingController;
    public static String currentView;
    public static User user = null;
    static PlaylistDAO playlistDAO;
    static SongDAO songDAO;
    static UserDAO userDAO;


    public static SongDAO getSongDAO() {
        return songDAO;
    }

    public static PlaylistDAO getPlaylistDAO() {
        return playlistDAO;
    }

    public static UserDAO getUserDAO() {
        return userDAO;
    }


    @Override
    public void start(Stage stage) {
        ClientApp.window = stage;
        ClientApp.window.setMinHeight(850);
        ClientApp.window.setMinWidth(1064);
        ClientApp.window.setTitle("Emotional Songs");

        initLayout("splashScreen");
    }

    public static void initLayout(String layout) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientApp.class.getResource("/client_gui/" + layout + ".fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            ClientApp.window.setScene(scene);
            ClientApp.window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Stage createStage(String resourceName, String title, boolean isModal) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientApp.class.getResource("/client_gui/" + resourceName));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();

            stage.initOwner(ClientApp.window);
            stage.initModality(isModal ? Modality.WINDOW_MODAL : Modality.NONE);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
            return stage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void showSongsView(AnchorPane view) {
        try {
            if (ClientApp.currentView == null || !ClientApp.currentView.equals("songs")) {

                FXMLLoader loader = new FXMLLoader(ClientApp.class.getResource("/client_gui/songsView.fxml"));
                AnchorPane songsView = loader.load();
                songsController = loader.getController();

                view.getChildren().clear();
                view.getChildren().add(songsView);
                ClientApp.currentView = "songs";

//                if (ClientContext.getUser() != null) {
//                    ClientApp.songsController.showRatingPane();
//                    ClientApp.ratingController.setSongsController(ClientApp.songsController);
//                }
//
//                ClientApp.songsController.setRatingController(ClientApp.ratingController);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void showPlaylistsView(AnchorPane view) {
        try {
            if (ClientApp.currentView == null || !ClientApp.currentView.equals("playlists")) {

                FXMLLoader loader = new FXMLLoader(ClientApp.class.getResource("/client_gui/playlistsView.fxml"));
                AnchorPane playlistsView = loader.load();
//                playlistsController = loader.getController();

                view.getChildren().clear();
                view.getChildren().add(playlistsView);
                ClientApp.currentView = "playlists";
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void appStart(String[] args) throws RemoteException, NotBoundException {

        ClientLogger.debug("Client main");
        String host = args.length >= 1 ? args[0] : null;
        Registry registry = LocateRegistry.getRegistry(host);

        playlistDAO = (PlaylistDAO)
                registry.lookup("PlaylistService");
        songDAO = (SongDAO)
                registry.lookup("SongService");
        userDAO = (UserDAO)
                registry.lookup("UserService");

        launch();
    }
}
