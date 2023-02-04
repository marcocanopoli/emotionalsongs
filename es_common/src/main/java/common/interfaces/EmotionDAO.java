package common.interfaces;

import common.Emotion;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

/**
 * Interface per il DAO layer riguardante le operazioni sull'entità <code>Emotion</code>.
 * E' utilizzato dal client per conoscere i metodi disponibili e implementata dal server che ne
 * definisce il reale comportameto per i metodi.
 * <p>
 * Contiene una mappa di query accessibili tramite chiave enum <code>EmoSel</code>
 * strutturate per essere utlizzate tramite <code>PreparedStatement</code> offerto da <code>java.sql</code>
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 * @see Emotion
 * @see EmoSel
 * @see database
 */
public interface EmotionDAO extends Remote {
    enum EmoSel {ALL_EMO}

    Map<EmoSel, String> emoSelQueries = Map.ofEntries(
            entry(
                    EmoSel.ALL_EMO,
                    """
                            SELECT *
                            FROM emotions
                            ORDER BY id ASC
                            """
            )
    );

    //================================================================================
    // SELECT
    //================================================================================

    /**
     * Getter di tutte le emozioni disponibili a DB
     *
     * @return una lista di <code>Emotion</code>
     * @throws RemoteException se lo stub non è raggiungibile
     */
    List<Emotion> getAllEmotions() throws RemoteException;
}
