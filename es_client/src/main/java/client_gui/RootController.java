package client_gui;

import client.ClientApp;
import client.ClientContext;
import common.Emotion;
import common.Playlist;
import common.User;
import common.interfaces.EmotionDAO;
import common.interfaces.PlaylistDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.rmi.RemoteException;
import java.util.List;

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
    private Button menuSearchBtn;
    @FXML
    public Button menuPlaylistsBtn;
    @FXML
    private AnchorPane mainView;

    public void initialize() throws RemoteException {
        ClientContext context = ClientContext.getInstance();

        PlaylistDAO playlistDAO = ClientApp.getPlaylistDAO();
        EmotionDAO emotionDAO = ClientApp.getEmotionDAO();
        List<Emotion> emotions = emotionDAO.getEmotions();

        context.setEmotions(emotions);

        context.addPropertyChangeListener(e -> {
            if (e.getPropertyName().equals("user")) {

                User newUser = (User) e.getNewValue();

                if (newUser != null) {
                    try {
                        List<Playlist> playlists = playlistDAO.getUserPlaylists(newUser.getId());
                        context.setUserPlaylists(playlists);
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                userLabel.setText(newUser != null ? "Ciao, " + newUser.getFirstName() : "");
                menuPlaylistsBtn.setDisable(newUser == null);
                loginBtn.setDisable(newUser != null);
                signupBtn.setDisable(newUser != null);
                logoutBtn.setDisable(newUser == null);
            }
        });


        ClientApp.showSongsView(mainView);

        menuSearchBtn.setOnAction(event ->
                ClientApp.showSongsView(mainView)
        );

        menuPlaylistsBtn.setOnAction(event ->
                ClientApp.showPlaylistsView(mainView)
        );

        signupBtn.setOnAction(event ->
                ClientApp.createStage("signupView.fxml", "Registrazione utente", true)
        );

        loginBtn.setOnAction(event ->
                ClientApp.createStage("loginView.fxml", "Login", true)
        );

        logoutBtn.setOnAction(event -> {
            context.setUser(null);
        });
    }
}