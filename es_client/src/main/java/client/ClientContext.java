package client;

import common.Emotion;
import common.Playlist;
import common.Song;
import common.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public final class ClientContext {

    private static final ClientContext INSTANCE = new ClientContext();
    private List<Emotion> emotions;
    private Song currentSong;
    private User user;

    ObservableList<Playlist> userPlaylists = FXCollections.observableArrayList();

    private ClientContext() {
    }

    public static ClientContext getInstance() {
        return INSTANCE;
    }

    public void setEmotions(List<Emotion> emotions) {
        this.emotions = emotions;
    }

    public List<Emotion> getEmotions() {
        return this.emotions;
    }

    public void setCurrentSong(Song song) {
        this.currentSong = song;
    }

    public Song getCurrentSong() {
        return this.currentSong;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    public void setUserPlaylists(List<Playlist> playlists) {
        this.userPlaylists.clear();
        this.userPlaylists.addAll(playlists);
    }

    public void addUserPlaylist(Playlist playlist) {
        this.userPlaylists.add(playlist);
    }

    public ObservableList<Playlist> getUserPlaylists() {
        return this.userPlaylists;
    }
}
