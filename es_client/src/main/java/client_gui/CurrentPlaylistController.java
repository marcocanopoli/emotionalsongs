package client_gui;

import client.ClientApp;
import client.ClientContext;
import client_gui.components.SongsTableController;
import common.Playlist;
import common.Song;
import common.interfaces.PlaylistDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.rmi.RemoteException;
import java.util.List;

public class CurrentPlaylistController {

    @FXML
    public Label playlistName;
    @FXML
    public Label playlistSongs;
    @FXML
    public Label playlistDuration;
    @FXML
    private TableView<Song> playlistSongsTable;
    @FXML
    private SongsTableController playlistSongsTableController;

    public void initialize() {
        ClientContext context = ClientContext.getInstance();

        context.addPropertyChangeListener(e -> {
            if (e.getPropertyName().equals("playlist")) {

                Playlist currentPlaylist = (Playlist) e.getNewValue();

                if (currentPlaylist != null) {
                    try {
                        setCurrentPlaylist(currentPlaylist);
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        playlistSongsTableController.addTableEmotionAddBtn(context);
    }

    private void setCurrentPlaylist(Playlist playlist) throws RemoteException {
        PlaylistDAO playlistDAO = ClientApp.getPlaylistDAO();
        List<Song> songs = playlistDAO.getPlaylistSongs(playlist.getId());
        Integer duration = 0;

        for (Song song : songs) {
            duration += song.getDurationInt();
        }

        String durationString = duration == 0 ? "0" : String.format("%d:%02d:%02d", duration / 3600, (duration % 3600) / 60, (duration % 60));

        playlistName.setText(playlist.getName());
        playlistSongs.setText(String.valueOf(songs.size()));
        playlistDuration.setText(durationString);

        playlistSongsTable.getItems().clear();
        playlistSongsTable.getItems().addAll(songs);


    }


}

