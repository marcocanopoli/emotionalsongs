package common;

import java.io.Serial;
import java.io.Serializable;

public class Emotion implements Serializable {

    @Serial
    private static final long serialVersionUID = 1;

    public final int id;
    private final String name;
    private final String description;

    public Emotion(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public final int getId() {
        return id;
    }

    public String getName() {

        return this.name;
    }

    public String getDescription() {

        return this.description;
    }

    @Override
    public String toString() {
        return (id + "\t" + name + "\t" + description);
    }

}
