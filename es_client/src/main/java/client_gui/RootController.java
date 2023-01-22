package client_gui;

import client.ClientApp;
import client.ClientContext;
import common.Playlist;
import common.User;
import common.interfaces.PlaylistDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.rmi.RemoteException;
import java.util.List;

public class RootController {
    @FXML
    public Button logoutBtn;
    @FXML
    public Button menuPlaylistsBtn;
    @FXML
    private Button menuSearchBtn;
    @FXML
    private AnchorPane mainView;

    public void initialize() throws RemoteException {
        ClientContext context = ClientContext.getInstance();
        User user = context.getUser();

        if (user != null) {
            PlaylistDAO playlistDAO = ClientApp.getPlaylistDAO();
            List<Playlist> playlists = playlistDAO.getUserPlaylists(user.getID());
            context.setUserPlaylists(playlists);
        }

        menuPlaylistsBtn.setDisable(context.getUser() == null);

        ClientApp.showSongsView(mainView);

        menuSearchBtn.setOnAction(event ->
                ClientApp.showSongsView(mainView)
        );

        menuPlaylistsBtn.setOnAction(event ->
                ClientApp.showPlaylistsView(mainView)
        );

        logoutBtn.setOnAction(event -> {
            context.setUser(null);
            ClientApp.currentView = null;
            ClientApp.initLayout("splashScreen");
        });
    }
}