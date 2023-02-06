package emotionalsongs.client.gui;

import emotionalsongs.client.ClientApp;
import emotionalsongs.client.ClientContext;
import emotionalsongs.common.Emotion;
import emotionalsongs.common.NodeHelpers;
import emotionalsongs.common.Playlist;
import emotionalsongs.common.User;
import emotionalsongs.common.interfaces.EmotionDAO;
import emotionalsongs.common.interfaces.PlaylistDAO;
import emotionalsongs.exceptions.RMIStubException;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Window;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Controller per FXML del layout di base dell'applicazione, nel quale vengono
 * innestate le viste principali.
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 */
public class RootController {

    @FXML
    public Button logoutBtn;
    @FXML
    public Button signupBtn;
    @FXML
    public Button loginBtn;
    @FXML
    public Label userLabel;
    @FXML
    public ListView<Playlist> playlistsList;
    @FXML
    private Button menuSearchBtn;
    @FXML
    public Button menuPlaylistsBtn;
    @FXML
    private AnchorPane mainView;
    private final Window window = ClientApp.getWindow();

    private final ClientContext context = ClientContext.getInstance();


    /**
     * Metodo di inizializzazione chiamato alla creazione del layout.
     * Setta il pannello principale in cui inserire le viste,
     * aggiunge listener ai bottoni di login e logout e di cambio vista.
     * E' in ascolto dei cambiamenti all'utente per bloccare o sbloccare funzionalità.
     *
     * @see ClientContext
     */
    public void initialize() {
        ClientApp.setMainView(mainView);

        EmotionDAO emotionDAO = ClientApp.getEmotionDAO();
        try {
            List<Emotion> emotions = emotionDAO.getAllEmotions();
            context.setEmotions(emotions);
        } catch (RemoteException e) {
            throw new RMIStubException(e);
        }


        context.addPropertyChangeListener(e -> {
            if (e.getPropertyName().equals("user")) {

                User newUser = (User) e.getNewValue();

                if (newUser != null) {
                    initPlaylistList();
                } else {
                    context.setCurrentPlaylist(null);
                    ClientApp.showView(ClientApp.ViewName.SEARCH);
                }
                userLabel.setText(newUser != null ? "Ciao, " + newUser.getUsername() : "");
                menuPlaylistsBtn.setDisable(newUser == null);
                loginBtn.setDisable(newUser != null);
                signupBtn.setDisable(newUser != null);
                logoutBtn.setDisable(newUser == null);
                playlistsList.setVisible(newUser != null);
            }
        });

        playlistsList.setVisible(false);

        menuSearchBtn.setOnAction(event -> ClientApp.showView(ClientApp.ViewName.SEARCH));

        menuPlaylistsBtn.setOnAction(event -> ClientApp.showView(ClientApp.ViewName.PLAYLISTS));

        signupBtn.setOnAction(event ->
                NodeHelpers.createStage(
                        window, ClientApp.signupURL, "Registrazione utente", true)
        );

        loginBtn.setOnAction(event ->
                NodeHelpers.createStage(
                        window, ClientApp.loginURL, "Effettua il login", true)
        );

        logoutBtn.setOnAction(event -> {
            final String msg = "Sei sicuro di voler uscire dal tuo account?";
            final boolean res = NodeHelpers.createAlert(Alert.AlertType.CONFIRMATION, "Conferma", null, msg, true);
            if (res) context.setUser(null);

        });
    }

    /**
     * Inizializza le playlist dell'utente al momento del login.
     * Setta i listener per popolare la lista di playlist in caso di aggiunta o rimozione.
     */
    private void initPlaylistList() {
        PlaylistDAO playlistDAO = ClientApp.getPlaylistDAO();
        User user = context.getUser();
        try {
            List<Playlist> playlists = playlistDAO.getUserPlaylists(user.getId());
            context.setUserPlaylists(playlists);
        } catch (RemoteException e) {
            throw new RMIStubException(e);
        }

        ObservableList<Playlist> userPlaylists = context.getUserPlaylists();

        userPlaylists.addListener((ListChangeListener.Change<? extends Playlist> playlist) ->
                playlistsList.setItems(FXCollections.observableArrayList(userPlaylists))
        );

        playlistsList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Playlist playlist, boolean empty) {
                super.updateItem(playlist, empty);
                setText(empty ? "" : playlist.getName());
            }
        });

        playlistsList.setItems(FXCollections.observableArrayList(userPlaylists));

        playlistsList.setOnMouseClicked(playlist -> {
            Playlist current = playlistsList.getSelectionModel().getSelectedItem();
            if (current != null) context.setCurrentPlaylist(current);
            ClientApp.showView(ClientApp.ViewName.PLAYLISTS);
        });
    }
}