package client;

import common.Playlist;
import common.Song;
import common.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public final class ClientContext {

    private static final ClientContext INSTANCE = new ClientContext();
    private User user;

    private Song currentSong;

    ObservableList<Playlist> userPlaylists = FXCollections.observableArrayList();

    private ClientContext() {
    }

    public static ClientContext getInstance() {
        return INSTANCE;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    public void setCurrentSong(Song song) {
        this.currentSong = song;
    }

    public Song getCurrentSong() {
        return this.currentSong;
    }

    public void setUserPlaylists(List<Playlist> playlists) {
        this.userPlaylists.addAll(playlists);
    }

    public void addUserPlaylist(Playlist playlist) {
        this.userPlaylists.add(playlist);
    }

    public ObservableList<Playlist> getUserPlaylists() {
        return this.userPlaylists;
    }
}
