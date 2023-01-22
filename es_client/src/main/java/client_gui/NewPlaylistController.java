package client_gui;

import client.ClientApp;
import client.ClientContext;
import common.Playlist;
import common.User;
import common.interfaces.PlaylistDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.rmi.RemoteException;

public class NewPlaylistController {

    @FXML
    public AnchorPane newPlaylistView;
    @FXML
    private TextArea newPlaylistPrompt;
    @FXML
    private Button createPlaylistBtn;

    public void initialize() {
        ClientContext context = ClientContext.getInstance();
        PlaylistDAO playlistDAO = ClientApp.getPlaylistDAO();

        newPlaylistPrompt.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= 256 ? change : null));

        newPlaylistPrompt.textProperty().addListener((observable, oldValue, newValue) -> {
            createPlaylistBtn.setDisable(newValue.isEmpty());
        });

        createPlaylistBtn.setOnAction(event -> {
            try {
                createNewPlaylist(playlistDAO, context);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void createNewPlaylist(PlaylistDAO playlistDAO, ClientContext context) throws RemoteException {
        User user = context.getUser();

        Playlist newPlaylist = playlistDAO.createNewPlaylist(user.getID(), newPlaylistPrompt.getText());

        if (newPlaylist != null) {
            context.addUserPlaylist(newPlaylist);
            ((Stage) (newPlaylistView.getScene().getWindow())).close();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Errore");
            alert.setHeaderText(null);
            alert.setContentText("La playlist '" + newPlaylistPrompt.getText() + "' esiste già!");

            alert.showAndWait();

        }

    }

}