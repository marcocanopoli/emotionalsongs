package client;

import common.NodeHelpers;
import common.interfaces.EmotionDAO;
import common.interfaces.PlaylistDAO;
import common.interfaces.SongDAO;
import common.interfaces.UserDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Objects;

public class ClientApp extends Application {

    private static Window window;
    private static Stage mainStage;
    private static AnchorPane mainView;
    private static VBox searchView;
    private static TabPane playlistsView;
    private static PlaylistDAO playlistDAO;
    private static EmotionDAO emotionDAO;
    private static SongDAO songDAO;
    private static UserDAO userDAO;
    public static final URL stylesheetURL = ClientApp.class.getResource("/client_gui/emotionalSongs.css");
    public static final URL rootURL = ClientApp.class.getResource("/client_gui/rootLayout.fxml");
    public static final URL playlistsViewURL = ClientApp.class.getResource("/client_gui/playlistsView.fxml");
    public static final URL searchViewURL = ClientApp.class.getResource("/client_gui/searchView.fxml");
    public static final URL signupURL = ClientApp.class.getResource("/client_gui/signupView.fxml");
    public static final URL loginURL = ClientApp.class.getResource("/client_gui/loginView.fxml");
    public static final URL songInfoURL = ClientApp.class.getResource("/client_gui/songInfoView.fxml");
    public static final URL ratingURL = ClientApp.class.getResource("/client_gui/ratingView.fxml");

    public enum ViewName {PLAYLISTS, SEARCH}

    public static SongDAO getSongDAO() {
        return songDAO;
    }

    public static EmotionDAO getEmotionDAO() {
        return emotionDAO;
    }

    public static PlaylistDAO getPlaylistDAO() {
        return playlistDAO;
    }

    public static UserDAO getUserDAO() {
        return userDAO;
    }

    public static void setMainView(AnchorPane view) {
        mainView = view;
    }

    @Override
    public void start(Stage stage) throws IOException {

        setWindow(stage.getOwner());

        stage.setMinWidth(1280);
        stage.setMinHeight(800);

        setMainStage(Objects.requireNonNull(NodeHelpers.createStage(
                null, stage, rootURL, "Emotional Songs", false)).getKey());

        if (stylesheetURL != null) mainStage.getScene().getStylesheets().add((stylesheetURL).toExternalForm());

        createViews();
        showView(ViewName.SEARCH);
    }

    public static void setMainStage(Stage stage) {
        mainStage = stage;
    }

    public static Window getWindow() {
        return window;
    }

    public static void setWindow(Window appWindow) {
        window = appWindow;
    }

    public static void createViews() throws IOException {

        if (playlistsViewURL != null) playlistsView = FXMLLoader.load(playlistsViewURL);
        if (searchViewURL != null) searchView = FXMLLoader.load(searchViewURL);

    }

    public static void showView(ViewName view) {
        mainView.getChildren().clear();

        switch (view) {
            case SEARCH -> mainView.getChildren().add(ClientApp.searchView);
            case PLAYLISTS -> {
                ClientApp.playlistsView.getSelectionModel().select(0);
                mainView.getChildren().add(ClientApp.playlistsView);
            }
        }
    }

    public static void appStart(String[] args) throws RemoteException, NotBoundException {

        ClientLogger.debug("Client main");
        String host = args.length >= 1 ? args[0] : null;
        Registry registry = LocateRegistry.getRegistry(host);

        playlistDAO = (PlaylistDAO) registry.lookup("PlaylistService");
        emotionDAO = (EmotionDAO) registry.lookup("EmotionService");
        songDAO = (SongDAO) registry.lookup("SongService");
        userDAO = (UserDAO) registry.lookup("UserService");

        launch();
    }
}
