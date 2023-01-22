package common;

import java.io.Serial;
import java.io.Serializable;

public class Playlist implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;

    public final int id;
    private final String name;

    public Playlist() {
        this.id = 0;
        this.name = "";
    }

    public Playlist(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return (id + "\t" + name + "\t");
    }

    public String getName() {

        return this.name;
    }

}
