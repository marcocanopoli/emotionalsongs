package client_gui;

import client.ClientApp;
import client.ClientContext;
import client_gui.components.SongsTableController;
import common.NodeHelpers;
import common.Playlist;
import common.Song;
import common.StringHelpers;
import common.interfaces.PlaylistDAO;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.rmi.RemoteException;

public class CurrentPlaylistController {

    @FXML
    public Label playlistName;
    @FXML
    public Label playlistSongs;
    @FXML
    public Label playlistDuration;
    @FXML
    public Button deletePlaylistBtn;
    @FXML
    private TableView<Song> playlistSongsTable;
    @FXML
    private SongsTableController playlistSongsTableController;
    private final PlaylistDAO playlistDAO = ClientApp.getPlaylistDAO();
    private final ClientContext context = ClientContext.getInstance();
    private Playlist currentPlaylist = context.getCurrentPlaylist();
    private final ObservableList<Song> currentPlaylistSongs = FXCollections.observableArrayList();

    public void initialize() {

        Property<ObservableList<Song>> playlistSongsProperty = new SimpleObjectProperty<>(currentPlaylistSongs);

        playlistSongs.textProperty().bind(Bindings.size(currentPlaylistSongs).asString());
        playlistSongsTable.itemsProperty().bind(playlistSongsProperty);

        context.addPropertyChangeListener(e -> {
            if (e.getPropertyName().equals("playlist")) {

                currentPlaylist = (Playlist) e.getNewValue();

                if (currentPlaylist != null) {
                    try {
                        initCurrentPlaylist();
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    playlistName.setText("");
                    playlistDuration.setText("0");
                }
            }
        });

        playlistSongsTableController.addTableEmotionAddBtn();
        playlistSongsTableController.addRemoveSongBtn(currentPlaylistSongs, true);
    }

    private void initCurrentPlaylist() throws RemoteException {
        currentPlaylistSongs.setAll(playlistDAO.getPlaylistSongs(currentPlaylist.getId()));
        playlistName.setText(currentPlaylist.getName());
        playlistDuration.setText(StringHelpers.getSongsListDurationString(currentPlaylistSongs));
    }

    @FXML
    private void deletePlaylist() throws RemoteException {
        String msg = "Sei sicuro di voler eliminare la playlist '" + currentPlaylist.getName() + "' ?";

        boolean res = NodeHelpers.createAlert(
                Alert.AlertType.CONFIRMATION, "Conferma eliminazione playlist", null, msg, true);

        if (res) {
            int deleted = playlistDAO.deletePlaylist(currentPlaylist.getId());

            if (deleted > 0) {
                context.removeUserPlaylist(currentPlaylist);
                context.setCurrentPlaylist(null);
            }

        }

    }


}

