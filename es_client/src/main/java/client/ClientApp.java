package client;

import common.interfaces.EmotionDAO;
import common.interfaces.PlaylistDAO;
import common.interfaces.SongDAO;
import common.interfaces.UserDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientApp extends Application {

    private static Stage window;
    private static AnchorPane mainView;
    private static VBox searchView;
    private static TabPane playlistsView;
    private static PlaylistDAO playlistDAO;
    private static EmotionDAO emotionDAO;
    private static SongDAO songDAO;
    private static UserDAO userDAO;


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
    public void start(Stage stage) {
        setStage(stage);
        window.setTitle("Emotional Songs");
        window.setMinWidth(1280);
        window.setMinHeight(800);

        try {
            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(
                            ClientApp.class.getResource("/client_gui/rootLayout.fxml")));
            Scene scene = new Scene(root);

            scene.getStylesheets().add(
                    Objects.requireNonNull(
                            ClientApp.class.getResource("/client_gui/emotionalSongs.css")).toExternalForm());

            window.setScene(scene);
            window.show();
            window.centerOnScreen();

            createViews();
            showSearchView();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setStage(Stage stage) {
        window = stage;
    }

    public static void createStage(String resourceName, String title, boolean isModal) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientApp.class.getResource("/client_gui/" + resourceName));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();

            stage.initOwner(window);
            stage.initModality(isModal ? Modality.APPLICATION_MODAL : Modality.NONE);
            stage.setTitle(title);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.setAlwaysOnTop(true);


            if (isModal) {
                stage.showAndWait();
            } else {
                stage.show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean createAlert(Alert.AlertType type, String title, String headerText, String message, boolean wait, boolean visibleConcel) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(message);

        Region spacer = new Region();
        ButtonBar.setButtonData(spacer, ButtonBar.ButtonData.BIG_GAP);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        alert.getDialogPane().applyCss();
        HBox hboxDialogPane = (HBox) alert.getDialogPane().lookup(".container");
        hboxDialogPane.getChildren().add(spacer);

        AtomicBoolean res = new AtomicBoolean(false);

        if (!visibleConcel) {
            final Button cancelBtn = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
            cancelBtn.setVisible(false);
        }

        if (wait) {
            alert.showAndWait()
                    .filter(response -> response == ButtonType.OK)
                    .ifPresent(response -> res.set(true));
        } else {
            alert.show();
        }

        return res.get();
    }

    public static void createViews() throws IOException {
        playlistsView = FXMLLoader.load(
                Objects.requireNonNull(
                        ClientApp.class.getResource("/client_gui/playlistsView.fxml")));

        searchView = FXMLLoader.load(
                Objects.requireNonNull(
                        ClientApp.class.getResource("/client_gui/searchView.fxml")));
    }

    public static void showSearchView() {
        mainView.getChildren().clear();
        mainView.getChildren().add(ClientApp.searchView);
    }

    public static void showPlaylistsView() {
        mainView.getChildren().clear();
        ClientApp.playlistsView.getSelectionModel().select(0);
        mainView.getChildren().add(ClientApp.playlistsView);
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
