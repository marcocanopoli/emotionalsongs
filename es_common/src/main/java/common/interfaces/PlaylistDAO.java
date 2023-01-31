package common.interfaces;

import common.Playlist;
import common.Song;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

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

    List<Song> getPlaylistSongs(int playlistId) throws RemoteException;

    List<Playlist> getUserPlaylists(int userId) throws RemoteException;

    //================================================================================
    // INSERT
    //================================================================================

    int[] addSongsToPlaylist(int playlistId, List<Integer> songIds) throws RemoteException;

    Playlist createNewPlaylist(int userId, String name, List<Integer> songIds) throws RemoteException;

    //================================================================================
    // DELETE
    //================================================================================

    int deletePlaylist(int playListId) throws RemoteException;

    int deletePlaylistSong(int playListId, int songId) throws RemoteException;
}

