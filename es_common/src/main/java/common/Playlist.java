package common;

import java.io.Serial;
import java.io.Serializable;

public class Playlist implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;

    private int id;
    private int userId;
    private String name;

    public Playlist(int userId, String name) {
        this.setUserId(userId);
        this.setName(name);
    }

    public Playlist(int id, int userId, String name) {
        this.setId(id);
        this.setUserId(userId);
        this.setName(name);
    }

    public final int getId() {
        return id;
    }

    public final void setId(int id) {
        this.id = id;
    }

    public final int getUserId() {
        return userId;
    }

    public final void setUserId(int userId) {
        this.userId = userId;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return (id + "\t" + name + "\t");
    }
}
