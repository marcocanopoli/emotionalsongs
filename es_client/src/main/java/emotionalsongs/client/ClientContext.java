package emotionalsongs.client;

import emotionalsongs.common.Emotion;
import emotionalsongs.common.Playlist;
import emotionalsongs.common.Song;
import emotionalsongs.common.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

/**
 * Il <strong>context</strong> dell'applicazione sviluppato come singleton pattern
 * Contiene i riferimenti ad oggetti accessibili da varie parti dell'applicazione
 * Implementa vari listener per rendere l'applicazione reattiva ai cambiamenti di tali oggetti
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 * @see PropertyChangeListener
 */
public final class ClientContext {
    private static final ClientContext INSTANCE = new ClientContext();
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private List<Emotion> emotions;
    private Song currentSong;
    private Playlist currentPlaylist;
    private User user;
    private final ObservableList<Playlist> userPlaylists = FXCollections.observableArrayList();
    private final ObservableList<Song> searchedSongs = FXCollections.observableArrayList();
    private final ObservableList<Song> newPlaylistSongs = FXCollections.observableArrayList();

    private ClientContext() {
    }

    /**
     * Aggiunge a <code>PropertyChangeSupport</code> un listener per le proprietà osservabili
     *
     * @param listener il listener da settare
     * @see PropertyChangeSupport
     * @see PropertyChangeListener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    /**
     * Getter dell'istanza del Context
     *
     * @return l'istanza finale del context
     */
    public static ClientContext getInstance() {
        return INSTANCE;
    }

    /**
     * Setter della lista di emozioni disponibili
     *
     * @param newEmotions la lista di <code>Emotion</code>
     */
    public void setEmotions(List<Emotion> newEmotions) {
        emotions = newEmotions;
    }

    /**
     * Getter della lista di emozioni disponibili
     *
     * @return la lista di <code>Emotion</code>
     */
    public List<Emotion> getEmotions() {
        return emotions;
    }

    /**
     * Setta le canzoni attualmente ricercate
     *
     * @param newSongs la lista di <code>Song</code>
     */
    public void setSearchedSongs(List<Song> newSongs) {

        searchedSongs.setAll(newSongs);
    }

    /**
     * Getter della lista di canzoni attualmente ricercate
     *
     * @return la lista di <code>Song</code>
     */
    public ObservableList<Song> getSearchedSongs() {

        return searchedSongs;
    }

    /**
     * Aggiunge una lista di canzoni alla playlist in fase di creazione
     *
     * @param newSongs la lista di canzoni
     */
    public void addNewPlaylistSongs(List<Song> newSongs) {

        newPlaylistSongs.addAll(newSongs);
    }

    /**
     * Getter delle canzoni presenti nella playlist in fase di creazione
     *
     * @return la lista di canzoni
     */
    public ObservableList<Song> getNewPlaylistSongs() {

        return newPlaylistSongs;
    }

    /**
     * Setter della canzone attualmente in visualizzazione
     *
     * @param song la canzone
     */
    public void setCurrentSong(Song song) {
        currentSong = song;
    }

    /**
     * Getter della canzone attualmente in visualizzazione
     *
     * @return la canzone
     */
    public Song getCurrentSong() {
        return currentSong;
    }

    /**
     * Setter della playlist attualmente in visualizzazione
     * Triggera il listener definito in <code>addPropertyChangeListener</code>
     *
     * @param playlist la playlist
     */
    public void setCurrentPlaylist(Playlist playlist) {
        Playlist oldPlaylist = currentPlaylist;
        currentPlaylist = playlist;
        support.firePropertyChange("playlist", oldPlaylist, playlist);
    }

    /**
     * Getter della playlist attualmente in visualizzazione
     *
     * @return la playlist
     */
    public Playlist getCurrentPlaylist() {
        return currentPlaylist;
    }

    /**
     * Setter dell'utente loggato
     * Triggera il listener definito in <code>addPropertyChangeListener</code>
     *
     * @param newUser l'utente
     */
    public void setUser(User newUser) {
        User oldUser = user;
        user = newUser;
        if (newUser == null) {
            userPlaylists.clear();
        }
        support.firePropertyChange("user", oldUser, newUser);
    }

    /**
     * Getter dell'utente loggato
     *
     * @return l'utente
     */
    public User getUser() {
        return user;
    }

    /**
     * Setter delle playlist dell'utente
     *
     * @param playlists le playlist
     */
    public void setUserPlaylists(List<Playlist> playlists) {
        userPlaylists.clear();
        userPlaylists.addAll(playlists);
    }

    /**
     * Getter delle playlist dell'utente
     *
     * @return la lista di playlist
     */
    public ObservableList<Playlist> getUserPlaylists() {
        return userPlaylists;
    }

    /**
     * Aggiunge una nuova playlist alle playlist dell'utente
     * Triggera il listener definito in <code>addPropertyChangeListener</code>
     *
     * @param playlist la nuova playlist
     */
    public void addUserPlaylist(Playlist playlist) {
        userPlaylists.add(playlist);
        support.firePropertyChange("newPlaylist", userPlaylists, playlist);

    }

    /**
     * Rimuove una playlist dalla lista di playlist dell'utente
     * Triggera il listener definito in <code>addPropertyChangeListener</code>
     *
     * @param playlist la playlist da rimuovere
     */
    public void removeUserPlaylist(Playlist playlist) {
        userPlaylists.remove(playlist);
        support.firePropertyChange("deletePlaylist", userPlaylists, playlist);
    }

}
