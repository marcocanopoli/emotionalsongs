package common;

import java.io.Serial;
import java.io.Serializable;

public record Emotion(int id, String name, String description) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1;

    @Override
    public String toString() {
        return (id + "\t" + name + "\t" + description);
    }

}
