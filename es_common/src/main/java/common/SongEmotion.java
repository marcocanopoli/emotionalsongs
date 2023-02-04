package common;

import java.io.Serial;
import java.io.Serializable;

/**
 * Record contenente l'entità <code>SongEmotion</code> salvata sul DB.
 * Implementa <code>Serializable</code> per lo scambio tramite RMI
 *
 * @param emotionId l'id dell'emozione
 * @param songId    l'id della canzone
 * @param userId    l'id dell'utente
 * @param rating    il rating da 1 a 5 associato
 * @author Marco Canopoli - Mat.731108 - Sede VA
 * @see java.io.Serializable
 * @see java.lang.Record
 */
public record SongEmotion(int emotionId, int songId, int userId, int rating,
                          String notes) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1;

    @Override
    public String toString() {
        return (emotionId + "\t" + rating + "\t" + notes);
    }
}
