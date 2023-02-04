package common.interfaces;

import common.Song;
import common.SongEmotion;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

/**
 * Interface per il DAO layer riguardante le operazioni sull'entità <code>Song</code>.
 * E' utilizzato dal client per conoscere i metodi disponibili e implementata dal server che ne
 * definisce il reale comportameto per i metodi.
 * <p>
 * Contiene mappe di query per le operazioni di <code>SELECT, INSERT, DELETE </code> accessibili tramite chiave enum e
 * strutturate per essere utlizzate tramite <code>PreparedStatement</code> offerto da <code>java.sql</code>
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 * @see Song
 * @see SongSel
 * @see SongIns
 * @see SongDel
 * @see database
 */
public interface SongDAO extends Remote {

    enum SongSel {
        ALBUMS, AUTHORS, SONGS_BY_AUTHOR_ALBUM, SONGS_BY_AUTHOR, SONGS_BY_AUTHOR_YEAR, SONGS_BY_TITLE,
        SONG_EMOTIONS, SONG_EMOTIONS_COUNT, SONG_EMOTION_NOTES, SONG_EMOTIONS_RATING
    }

    Map<SongSel, String> songSelQueries = Map.ofEntries(
            entry(
                    SongSel.ALBUMS,
                    """
                            SELECT author, album
                            FROM songs
                            WHERE album ILIKE ?
                            GROUP BY author, album
                            ORDER BY album ASC
                            """
            ),
            entry(
                    SongSel.AUTHORS,
                    """
                            SELECT author
                            FROM songs
                            WHERE author ILIKE ?
                            GROUP BY author
                            ORDER BY author ASC
                            """
            ),
            entry(
                    SongSel.SONGS_BY_AUTHOR_ALBUM,
                    """
                            SELECT  *
                            FROM songs
                            WHERE album = ?
                            AND author = ?
                            ORDER BY title ASC
                            """
            ),
            entry(
                    SongSel.SONGS_BY_AUTHOR,
                    """
                            SELECT *
                            FROM songs
                            WHERE author
                            ILIKE ?
                            ORDER BY author ASC
                            """
            ),
            entry(
                    SongSel.SONGS_BY_AUTHOR_YEAR,
                    """
                            SELECT *
                            FROM songs
                            WHERE author
                            AND year = ?
                            ILIKE ?
                            ORDER BY author ASC
                            """
            ),
            entry(
                    SongSel.SONGS_BY_TITLE,
                    """
                            SELECT *
                            FROM songs
                            WHERE title
                            ILIKE ?
                            ORDER BY title ASC
                            """
            ),
            entry(
                    SongSel.SONG_EMOTIONS,
                    """
                            SELECT E.id, COUNT(E.id)
                            FROM song_emotions SE
                            JOIN emotions E ON SE.emotion_id = E.id
                            JOIN songs S ON S.id = SE.song_id
                            WHERE S.id = ?
                            GROUP BY E.id
                            """
            ),
            entry(
                    SongSel.SONG_EMOTIONS_COUNT,
                    """
                            SELECT COUNT(E.id)
                            FROM song_emotions SE
                            JOIN emotions E ON SE.emotion_id = E.id
                            JOIN songs S ON S.id = SE.song_id
                            WHERE S.id = ?
                            """
            ),
            entry(
                    SongSel.SONG_EMOTION_NOTES,
                    """
                            SELECT SE.notes
                            FROM song_emotions SE
                            WHERE SE.song_id  = ?
                            AND SE.emotion_id = ?
                            """
            ),
            entry(
                    SongSel.SONG_EMOTIONS_RATING,
                    """
                            SELECT *
                            FROM song_emotions SE
                            JOIN emotions E ON SE.emotion_id = E.id
                            JOIN songs S ON S.id = SE.song_id
                            JOIN users U on U.id = SE.user_id
                            WHERE S.id = ?
                            AND U.id = ?
                            """
            )
    );

    enum SongIns {SONG_EMOTION, SONG_EMOTION_NOTES}

    Map<SongIns, String> songInsQueries = Map.ofEntries(
            entry(
                    SongIns.SONG_EMOTION,
                    """
                            INSERT INTO song_emotions
                            (user_id, song_id, emotion_id, rating) VALUES (?,?,?,?)
                            ON CONFLICT ON CONSTRAINT user_song_emotion DO UPDATE
                            SET rating = excluded.rating
                            """
            ),
            entry(
                    SongIns.SONG_EMOTION_NOTES,
                    """
                            INSERT INTO song_emotions
                            (user_id, song_id, emotion_id, notes) VALUES (?,?,?,?)
                            ON CONFLICT ON CONSTRAINT user_song_emotion DO UPDATE
                            SET notes = excluded.notes
                            """
            )
    );

    enum SongDel {SONG_EMOTION}

    Map<SongDel, String> songDelQueries = Map.ofEntries(
            entry(
                    SongDel.SONG_EMOTION,
                    """
                            DELETE FROM song_emotions
                            WHERE user_id = ?
                            AND song_id = ?
                            AND emotion_id = ?
                            """
            )
    );

    //================================================================================
    // SELECT
    //================================================================================

    /**
     * Getter degli album contenenti nel titolo la stringa in input
     *
     * @param album il titolo da cercare
     * @return una lista di canzoni rappresentative dell'album di appartenenza
     * @throws RemoteException se lo stub non è raggiungibile
     */
    List<Song> getAlbums(String album) throws RemoteException;

    /**
     * Getter degli autori contententi nel nome la string in input
     *
     * @param author il nome da cercare
     * @return una lista di nomi di autori
     * @throws RemoteException se lo stub non è raggiungibile
     */
    List<String> getAuthors(String author) throws RemoteException;

    /**
     * Ricerca canzoni secondo autore e album
     *
     * @param authorText l'autore
     * @param albumText  l'album
     * @return una lista di canzoni
     * @throws RemoteException se lo stub non è raggiungibile
     */
    List<Song> getSongsByAuthorAlbum(String authorText, String albumText) throws RemoteException;

    /**
     * Ricerca canzoni secondo autore e anno
     *
     * @param authorText l'autore
     * @param yearText   l'anno
     * @return una lista di canzoni
     * @throws RemoteException se lo stub non è raggiungibile
     */
    List<Song> getSongsByAuthorYear(String authorText, Integer yearText) throws RemoteException;

    /**
     * Ricerca canzoni tramite titolo
     *
     * @param titleText il titolo
     * @return una lista di canzoni
     * @throws RemoteException se lo stub non è raggiungibile
     */
    List<Song> getSongsByTitle(String titleText) throws RemoteException;

    /**
     * Getter del totale per ogni emozione di tutte le emozioni associate ad una canzone
     *
     * @param songId l'id della canzone
     * @return una mappa di id canzone -> count del totale
     * @throws RemoteException se lo stub non è raggiungibile
     */
    HashMap<Integer, Integer> getSongEmotionsCount(int songId) throws RemoteException;

    /**
     * Getter del totale di voti per le emozioni di una singola canzone
     *
     * @param songId l'id della canzone
     * @return il conteggio totale
     * @throws RemoteException se lo stub non è raggiungibile
     */
    int getSongEmotionsCountTotal(int songId) throws RemoteException;

    /**
     * Getter delle note di una singola emozione di una canzone
     *
     * @param songId    l'id della canzone
     * @param emotionId l'id dell'emozione
     * @return una lista di note associate all'emozione
     * @throws RemoteException se lo stub non è raggiungibile
     */
    List<String> getSongEmotionNotes(int songId, int emotionId) throws RemoteException;

    /**
     * Getter del rating e note associate alle emozioni di una canzone da un utente definito
     *
     * @param userId l'id dell'utente
     * @param songId l'id della canzone
     * @return una lista di emozioni con userId, rating e note
     * @throws RemoteException se lo stub non è raggiungibile
     * @see SongEmotion
     */
    List<SongEmotion> getUserSongEmotionsCountRating(int userId, int songId) throws RemoteException;

    //================================================================================
    // INSERT
    //================================================================================

    /**
     * Inserisce il rating per una emozione di una canzone da parte di un utente definito
     *
     * @param userId    l'id dell'utente
     * @param songId    l'id della canzone
     * @param emotionId l'id dell'emozione
     * @param rating    il rating da 1 a 5
     * @throws RemoteException se lo stub non è raggiungibile
     */
    void setSongEmotion(int userId, int songId, int emotionId, int rating) throws RemoteException;

    /**
     * Inserisce le note per una emozione di una canzone da parte di un utente definito
     *
     * @param userId    l'id dell'utente
     * @param songId    l'id della canzone
     * @param emotionId l'id dell'emozione
     * @param notes     le note fino a 256 caratteri
     * @throws RemoteException se lo stub non è raggiungibile
     */
    void setSongEmotionNotes(int userId, int songId, int emotionId, String notes) throws RemoteException;

    //================================================================================
    // DELETE
    //================================================================================

    /**
     * Elimina il rating associato ad una emozione di una canzone per un utente definito
     *
     * @param userId    l'id dell'utente
     * @param songId    l'id della canzone
     * @param emotionId l'id dell'emozione
     * @return il numero di record modificati: 1 se l'operazione è andata a buon fine, 0 altrimenti
     * @throws RemoteException se lo stub non è raggiungibile
     */
    int deleteSongEmotion(int userId, int songId, int emotionId) throws RemoteException;
}
