package client;

import common.Song;
import common.User;

public final class ClientContext {

    private static final ClientContext INSTANCE = new ClientContext();
    private User user;

    private Song currentSong;

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
}
