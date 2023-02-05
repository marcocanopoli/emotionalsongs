package emotionalsongs.common.interfaces;

import emotionalsongs.common.Playlist;
import emotionalsongs.common.Song;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

/**
 * Interface per il DAO layer riguardante le operazioni sull'entità <code>Playlist</code>.
 * E' utilizzato dal client per conoscere i metodi disponibili e implementata dal server che ne
 * definisce il reale comportameto per i metodi.
 * <p>
 * Contiene mappe di query per le operazioni di <code>SELECT, INSERT, DELETE </code> accessibili tramite chiave enum e
 * strutturate per essere utlizzate tramite <code>PreparedStatement</code> offerto da <code>java.sql</code>
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 * @see Song
 * @see Playlist
 * @see PlaylistSel
 * @see PlaylistIns
 * @see PlaylistDel
 * @see database
 */
public interface PlaylistDAO extends Remote {

    enum PlaylistSel {PLAYLIST_SONGS, USER_PLAYLISTS}

    Map<PlaylistSel, String> playlistSelQueries = Map.ofEntries(
            entry(
                    PlaylistSel.PLAYLIST_SONGS,
                    """
                            SELECT *
                            FROM songs S
                            JOIN playlist_songs PS ON PS.song_id = S.id
                            WHERE PS.playlist_id = ?
                            ORDER BY PS.order_key ASC
                            """
            ),
            entry(
                    PlaylistSel.USER_PLAYLISTS,
                    "SELECT * FROM playlists P WHERE P.user_id = ?"
            )
    );

    enum PlaylistIns {SONG_IN_PLAYLIST, NEW_PLAYLIST}

    Map<PlaylistIns, String> playlistInsQueries = Map.ofEntries(
            entry(
                    PlaylistIns.SONG_IN_PLAYLIST,
                    """
                            INSERT INTO playlist_songs
                            (playlist_id, song_id) VALUES (?,?)
                            ON CONFLICT ON CONSTRAINT playlist_songs_id DO NOTHING
                            """
            ),
            entry(
                    PlaylistIns.NEW_PLAYLIST,
                    "INSERT INTO playlists (user_id, name) VALUES (?,?)"
            )
    );

    enum PlaylistDel {PLAYLIST, PLAYLIST_SONG}

    Map<PlaylistDel, String> playlistDelQueries = Map.ofEntries(
            entry(
                    PlaylistDel.PLAYLIST,
                    "DELETE FROM playlists WHERE id = ?"
            ),
            entry(
                    PlaylistDel.PLAYLIST_SONG,
                    """
                            DELETE FROM playlist_songs
                            WHERE playlist_id = ?
                            AND song_id = ?
                            """
            )
    );

    //================================================================================
    // SELECT
    //================================================================================

    /**
     * Getter delle canzoni incluse in una playlist
     *
     * @param playlistId l'id della playlist
     * @return una lista di canzoni
     * @throws RemoteException se lo stub non è raggiungibile
     */
    List<Song> getPlaylistSongs(int playlistId) throws RemoteException;

    /**
     * Getter delle playlist di uno specifico utente
     *
     * @param userId l'id dell'utente
     * @return una lista di playlist
     * @throws RemoteException se lo stub non è raggiungibile
     */
    List<Playlist> getUserPlaylists(int userId) throws RemoteException;

    //================================================================================
    // INSERT
    //================================================================================

    /**
     * Aggiunge un batch di canzoni ad una playlist
     *
     * @param playlistId l'id della playlist
     * @param songIds    una lista di id di canzoni
     * @return un array contenente i riferimenti ai record inseriti
     * @throws RemoteException se lo stub non è raggiungibile
     */
    int[] addSongsToPlaylist(int playlistId, List<Integer> songIds) throws RemoteException;

    /**
     * Crea una nuova playlist contenente una lista di canzoni
     *
     * @param userId  l'id dell'utente
     * @param name    il nome della playlist
     * @param songIds una lista di id di canzoni
     * @return la playlist appena creata
     * @throws RemoteException se lo stub non è raggiungibile
     */
    Playlist createNewPlaylist(int userId, String name, List<Integer> songIds) throws RemoteException;

    //================================================================================
    // DELETE
    //================================================================================

    /**
     * Elimina una specifica playlist
     *
     * @param playListId l'id della playlist
     * @return il numero di record modificati: 1 se l'operazione è andata a buon fine, 0 altrimenti
     * @throws RemoteException se lo stub non è raggiungibile
     */
    int deletePlaylist(int playListId) throws RemoteException;

    /**
     * Rimuove una canzone da una playlist
     *
     * @param playListId l'id della playlist
     * @param songId     l'id della canzone
     * @return il numero di record modificati: 1 se l'operazione è andata a buon fine, 0 altrimenti
     * @throws RemoteException se lo stub non è raggiungibile
     */
    int deletePlaylistSong(int playListId, int songId) throws RemoteException;
}

