package emotionalsongs.common;

import java.io.Serial;
import java.io.Serializable;

/**
 * Contiene l'entità <code>Playlist</code> salvata sul DB.
 * Implementa <code>Serializable</code> per lo scambio tramite RMI
 * Contiene costruttore e getter dei parametri.
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 * @see java.io.Serializable
 */
public class Playlist implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;

    private int id;
    private int userId;
    private String name;

    public Playlist(int id, int userId, String name) {
        this.id = id;
        this.userId = userId;
        this.name = name;
    }

    public final int getId() {
        return id;
    }

    public final int getUserId() {
        return userId;
    }

    public final String getName() {
        return name;
    }

    @Override
    public String toString() {
        return (id + "\t" + name + "\t");
    }
}
