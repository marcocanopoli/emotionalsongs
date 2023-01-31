package common.interfaces;

import common.Song;
import common.SongEmotion;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

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

    List<Song> getAlbums(String album) throws RemoteException;

    List<String> getAuthors(String author) throws RemoteException;

    List<Song> getSongsByAuthorAlbum(String authorText, String albumText) throws RemoteException;

    List<Song> getSongsByAuthorYear(String authorText, Integer yearText) throws RemoteException;

    List<Song> getSongsByTitle(String searchString) throws RemoteException;

    HashMap<Integer, Integer> getSongEmotions(int songId) throws RemoteException;

    int getSongEmotionsCount(int songId) throws RemoteException;

    List<String> getSongEmotionNotes(int songId, int emotionId) throws RemoteException;

    List<SongEmotion> getSongEmotionsRating(int userId, int songId) throws RemoteException;

    //================================================================================
    // INSERT
    //================================================================================

    void setSongEmotion(int userId, int songId, int emotionId, int rating) throws RemoteException;

    void setSongEmotionNotes(int userId, int songId, int emotionId, String notes) throws RemoteException;

    //================================================================================
    // DELETE
    //================================================================================

    int deleteSongEmotion(int userId, int songId, int emotionId) throws RemoteException;
}
