package common;

import java.io.Serial;
import java.io.Serializable;

/**
 * Record contenente l'entità <code>Emotion</code> salvata sul DB.
 * Implementa <code>Serializable</code> per lo scambio tramite RMI
 *
 * @param id          l'id
 * @param name        il nome
 * @param description la descrizione
 * @author Marco Canopoli - Mat.731108 - Sede VA
 * @see java.io.Serializable
 * @see java.lang.Record
 */
public record Emotion(int id, String name, String description) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1;

    @Override
    public String toString() {
        return (id + "\t" + name + "\t" + description);
    }

}
