package emotionalsongs.client;

import emotionalsongs.common.NodeHelpers;
import emotionalsongs.common.interfaces.EmotionDAO;
import emotionalsongs.common.interfaces.PlaylistDAO;
import emotionalsongs.common.interfaces.SongDAO;
import emotionalsongs.common.interfaces.UserDAO;
import emotionalsongs.exceptions.RMIRegistryNotFoundException;
import emotionalsongs.exceptions.RMIStubException;
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

/**
 * Classe principale che permette di avviare l'applicazione EmotionalSongs client
 * Estende la classe Application, entry point di un'applicazione JavaFX
 * Include tutti i riferimenti ai file FXML utilizzati per creare le varie viste e sezioni e i riferimenti agli stub
 * RMI con i quali avviene la comunicazione verso l'applicazione server
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 * @see ClientMain
 */
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
    public static final URL stylesheetURL = ClientApp.class.getResource("/emotionalsongs/client_gui/emotionalSongs.css");
    public static final URL rootURL = ClientApp.class.getResource("/emotionalsongs/client_gui/rootLayout.fxml");
    public static final URL playlistsViewURL = ClientApp.class.getResource("/emotionalsongs/client_gui/playlistsView.fxml");
    public static final URL searchViewURL = ClientApp.class.getResource("/emotionalsongs/client_gui/searchView.fxml");
    public static final URL signupURL = ClientApp.class.getResource("/emotionalsongs/client_gui/signupView.fxml");
    public static final URL loginURL = ClientApp.class.getResource("/emotionalsongs/client_gui/loginView.fxml");
    public static final URL songInfoURL = ClientApp.class.getResource("/emotionalsongs/client_gui/songInfoView.fxml");
    public static final URL ratingURL = ClientApp.class.getResource("/emotionalsongs/client_gui/ratingView.fxml");

    public enum ViewName {PLAYLISTS, SEARCH}

    /**
     * Ritorna il riferimento al layer DAO per la gestione delle canzoni
     *
     * @return stub del layer DAO
     */
    public static SongDAO getSongDAO() {
        return songDAO;
    }

    /**
     * Ritorna il riferimento al layer DAO per la gestione delle emozioni
     *
     * @return stub del layer DAO
     */
    public static EmotionDAO getEmotionDAO() {
        return emotionDAO;
    }

    /**
     * Ritorna il riferimento al layer DAO per la gestione delle playlist
     *
     * @return stub del layer DAO
     */
    public static PlaylistDAO getPlaylistDAO() {
        return playlistDAO;
    }

    /**
     * Ritorna il riferimento al layer DAO per la gestione degli utenti
     *
     * @return stub del layer DAO
     */
    public static UserDAO getUserDAO() {
        return userDAO;
    }

    /**
     * Setter del pannello che contiene la viste principali
     *
     * @param view il pannello principale
     */
    public static void setMainView(AnchorPane view) {
        mainView = view;
    }

    /**
     * Setter del primary stage dell'applicazione
     *
     * @param stage il primary stage
     */
    public static void setMainStage(Stage stage) {
        mainStage = stage;
    }

    /**
     * Getter della <strong>window</strong> contenente l'intera applicazione
     *
     * @return la window
     */
    public static Window getWindow() {
        return window;
    }

    /**
     * Setter della <strong>window</strong> principale contenente l'intera applicazione
     *
     * @param appWindow la window
     */
    public static void setWindow(Window appWindow) {
        window = appWindow;
    }

    /**
     * Entry point dell'applicazione JavaFX, chiamato dall'inizializzazione del thread Application
     *
     * @param stage il <strong>primary stage</strong> dell'applicazione
     *              sul quale settare la <strong>scene</strong> principale
     *              L'applicazione può creare altri stage, che non saranno principali
     */
    @Override
    public void start(Stage stage) {

        setWindow(stage.getOwner());

        setMainStage(NodeHelpers.createMainStage(stage, rootURL, "Emotional Songs", 1280, 800));

        if (stylesheetURL != null) mainStage.getScene().getStylesheets().add((stylesheetURL).toExternalForm());

        createViews();
        showView(ViewName.SEARCH);
    }

    /**
     * Inizializza le viste principali da includere nel root layout dell'applicazione
     */
    public static void createViews() {
        try {

            if (playlistsViewURL != null) playlistsView = FXMLLoader.load(playlistsViewURL);
            if (searchViewURL != null) searchView = FXMLLoader.load(searchViewURL);
        } catch (IOException e) {
            ClientLogger.error("Impossibile creare le viste: " + e);
        }

    }

    /**
     * Mostra una specifica vista tra quelle disponibili
     *
     * @param view l'enum identificativo dell vista
     * @see ViewName
     */
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

    /**
     * Classe main dell'applicazione che lancia il thread Application.
     * Rinominata in <code>appStart</code> per mantenere univoco il metodo main.
     * E' chiamato dalla classe wrapper <code>ClientMain</code>
     *
     * @param args argomenti di avvio
     * @see ClientMain
     */
    public static void appStart(String[] args) {

        ClientLogger.debug("Client main");
        String host = args.length >= 1 ? args[0] : null;

        try {
            Registry registry = LocateRegistry.getRegistry(host);
            playlistDAO = (PlaylistDAO) registry.lookup("PlaylistService");
            emotionDAO = (EmotionDAO) registry.lookup("EmotionService");
            songDAO = (SongDAO) registry.lookup("SongService");
            userDAO = (UserDAO) registry.lookup("UserService");
        } catch (RemoteException e) {
            throw new RMIRegistryNotFoundException(e);
        } catch (NotBoundException e) {
            throw new RMIStubException(e);
        }


        launch();
    }
}
