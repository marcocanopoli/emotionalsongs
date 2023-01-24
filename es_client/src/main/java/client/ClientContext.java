package client;

import common.Emotion;
import common.Playlist;
import common.Song;
import common.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

public final class ClientContext {

    private static final ClientContext INSTANCE = new ClientContext();
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private List<Emotion> emotions;
    private Song currentSong;
    private User user;

    ObservableList<Playlist> userPlaylists = FXCollections.observableArrayList();

    private ClientContext() {
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public static ClientContext getInstance() {
        return INSTANCE;
    }

    public void setEmotions(List<Emotion> newEmotions) {
        emotions = newEmotions;
    }

    public List<Emotion> getEmotions() {
        return emotions;
    }

    public void setCurrentSong(Song song) {
        currentSong = song;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public void setUser(User newUser) {
        User oldUser = user;
        user = newUser;
        if (newUser == null) {
            userPlaylists.clear();
        }
        support.firePropertyChange("user", oldUser, newUser);
    }

    public User getUser() {
        return user;
    }

    public void setUserPlaylists(List<Playlist> playlists) {
        userPlaylists.clear();
        userPlaylists.addAll(playlists);
    }

    public void addUserPlaylist(Playlist playlist) {
        userPlaylists.add(playlist);
    }

    public ObservableList<Playlist> getUserPlaylists() {
        return userPlaylists;
    }

}
