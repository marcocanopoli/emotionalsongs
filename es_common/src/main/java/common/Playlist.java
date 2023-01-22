package common;

import java.io.Serial;
import java.io.Serializable;

public class Playlist implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;

    private final int id;
    private final String name;

    public Playlist() {
        this.id = -1;
        this.name = "";
    }

    public Playlist(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public final int getId() {
        return id;
    }

    public final String getName() {
        return name;
    }

    @Override
    public String toString() {
        return (id + "\t" + name + "\t");
    }
}
