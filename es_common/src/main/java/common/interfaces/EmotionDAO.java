package common.interfaces;

import common.Emotion;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

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

    List<Emotion> getAllEmotions() throws RemoteException;
}
