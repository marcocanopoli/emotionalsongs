package emotionalsongs.server;

import emotionalsongs.common.Playlist;
import emotionalsongs.common.Song;
import emotionalsongs.common.interfaces.PlaylistDAO;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione dell'interfaccia <code>PlaylistDAO</code>
 *
 * @see PlaylistDAO
 */
public class PlaylistDAOImpl implements PlaylistDAO {

    private static final String REMOTE_NAME = "PlaylistService";

    /**
     * Costruttore della classe.
     * Si occupa del bind dello stub al registry
     *
     * @param registry il registo RMI
     * @throws RemoteException se la comunicazione col registry o l'export falliscono
     */
    public PlaylistDAOImpl(Registry registry) throws RemoteException {
        PlaylistDAO playlistDAOStub = (PlaylistDAO) UnicastRemoteObject.exportObject(this, 3939);
        registry.rebind(REMOTE_NAME, playlistDAOStub);
    }

    /**
     * Esegue l'unbind dal registro e l'unexport del remote object
     *
     * @param registry il registro RMI
     */
    public void unbind(Registry registry) {
        try {
            registry.unbind(REMOTE_NAME);
        } catch (NotBoundException | RemoteException e) {
            ServerLogger.error(REMOTE_NAME + " unbinding failed");
        }

    }

    //================================================================================
    // SELECT
    //================================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<Song> getPlaylistSongs(int playlistId) {
        Connection conn = ServerApp.getConnection();

        final String query = PlaylistDAO.playlistSelQueries.get(PlaylistSel.PLAYLIST_SONGS);

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, playlistId);
            ResultSet rs = stmt.executeQuery();

            List<Song> results = new ArrayList<>();

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                int year = rs.getInt("year");
                String album = rs.getString("album");
                String genre = rs.getString("genre");
                Integer duration = rs.getInt("duration") > 0 ? rs.getInt("duration") : null;

                results.add(new Song(id, title, author, year, album, genre, duration));

            }
            return results;


        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<Playlist> getUserPlaylists(int userId) {
        Connection conn = ServerApp.getConnection();

        final String query = PlaylistDAO.playlistSelQueries.get(PlaylistSel.USER_PLAYLISTS);

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            List<Playlist> results = new ArrayList<>();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");

                results.add(new Playlist(id, userId, name));
            }
            return results;


        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }

        return null;
    }

    //================================================================================
    // INSERT
    //================================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized int[] addSongsToPlaylist(int playlistId, List<Integer> songIds) {
        Connection conn = ServerApp.getConnection();
        final String query = PlaylistDAO.playlistInsQueries.get(PlaylistIns.SONG_IN_PLAYLIST);

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            for (int id : songIds) {
                stmt.setInt(1, playlistId);
                stmt.setInt(2, id);
                stmt.addBatch();
            }
            conn.setAutoCommit(false);

            int[] affected = stmt.executeBatch();
            conn.commit();

            conn.setAutoCommit(true);

            return affected;


        } catch (SQLException ex) {
            ServerLogger.error("ERROR: " + ex);
            return new int[0];
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized Playlist createNewPlaylist(int userId, String name, List<Integer> songIds) {
        Connection conn = ServerApp.getConnection();
        final String query = PlaylistDAO.playlistInsQueries.get(PlaylistIns.NEW_PLAYLIST);

        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, userId);
            stmt.setString(2, name);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating playlist failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    addSongsToPlaylist(generatedKeys.getInt(1), songIds);
                    return new Playlist(generatedKeys.getInt(1), userId, name);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

        } catch (SQLException ex) {
            ServerLogger.error("PLAYLIST NOT FOUND: " + ex);
            return null;
        }
    }

    //================================================================================
    // DELETE
    //================================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized int deletePlaylist(int playListId) {
        Connection conn = ServerApp.getConnection();

        final String query = PlaylistDAO.playlistDelQueries.get(PlaylistDel.PLAYLIST);

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, playListId);

            return stmt.executeUpdate();

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }

        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized int deletePlaylistSong(int playListId, int songId) {
        Connection conn = ServerApp.getConnection();

        final String query = PlaylistDAO.playlistDelQueries.get(PlaylistDel.PLAYLIST_SONG);

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, playListId);
            stmt.setInt(2, songId);

            return stmt.executeUpdate();

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }

        return 0;
    }
}
