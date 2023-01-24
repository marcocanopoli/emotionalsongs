package common;

import java.io.Serial;
import java.io.Serializable;

public record SongEmotion(int emotionId, int songId, int userId, int rating,
                          String notes) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1;

    @Override
    public String toString() {
        return (emotionId + "\t" + rating + "\t" + notes);
    }
}
