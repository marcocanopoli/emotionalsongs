package client;

import common.Song;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ClientContext implements PropertyChangeListener {
    private static final ClientContext instance = new ClientContext();

    private Song song = new Song();
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public ClientContext() {
        pcs.addPropertyChangeListener(this);
    }

    public void propertyChange(PropertyChangeEvent evt) {
//        System.out.println(evt.getNewValue());
//        SongInfoController.setSong((Song) evt.getNewValue());
    }

    public static ClientContext getInstance() {
        return instance;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song newSong) {
        Song oldSong = song;
        song = newSong;
        pcs.firePropertyChange("song", oldSong, newSong);
    }


}
