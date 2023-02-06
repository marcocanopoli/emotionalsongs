package emotionalsongs.server;

import emotionalsongs.common.Song;
import emotionalsongs.common.SongEmotion;
import emotionalsongs.common.interfaces.SongDAO;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione dell'interfaccia <code>SongDAO</code>
 *
 * @see SongDAO
 */
public class SongDAOImpl implements SongDAO {

    private static final String REMOTE_NAME = "SongService";

    /**
     * Costruttore della classe.
     * Si occupa del bind dello stub al registry
     *
     * @param registry il registo RMI
     * @throws RemoteException se la comunicazione col registry o l'export falliscono
     */
    public SongDAOImpl(Registry registry) throws RemoteException {
        SongDAO songDAOStub = (SongDAO) UnicastRemoteObject.exportObject(this, 3939);
        registry.rebind(REMOTE_NAME, songDAOStub);
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
    public synchronized List<Song> getAlbums(String albumText) {
        Connection conn = ServerApp.getConnection();

        final String query = SongDAO.songSelQueries.get(SongSel.ALBUMS);

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + albumText + "%");

            List<Song> results = new ArrayList<>();

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String author = rs.getString("author");
                String album = rs.getString("album");

                results.add(new Song(author, album));

            }
            return results;

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }

        return new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<String> getAuthors(String author) {
        Connection conn = ServerApp.getConnection();

        final String query = SongDAO.songSelQueries.get(SongSel.AUTHORS);

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + author + "%");

            List<String> results = new ArrayList<>();

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                results.add(rs.getString("author"));

            }
            return results;

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }

        return new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<Song> getSongsByAuthorAlbum(String authorText, String albumText) {
        Connection conn = ServerApp.getConnection();

        final String query = SongDAO.songSelQueries.get(SongSel.SONGS_BY_AUTHOR_ALBUM);

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, albumText);
            stmt.setString(2, authorText);

            List<Song> results = new ArrayList<>();

            ResultSet rs = stmt.executeQuery();

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

        return new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<Song> getSongsByAuthorYear(String authorText, Integer yearText) {
        Connection conn = ServerApp.getConnection();
        final String query = SongDAO.songSelQueries.get(
                yearText == null ? SongSel.SONGS_BY_AUTHOR : SongSel.SONGS_BY_AUTHOR_YEAR);

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + authorText + "%");
            if (yearText != null) stmt.setInt(2, yearText);

            List<Song> results = new ArrayList<>();

            ResultSet rs = stmt.executeQuery();

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

        return new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<Song> getSongsByTitle(String titleText) {
        Connection conn = ServerApp.getConnection();
        final String query = SongDAO.songSelQueries.get(SongSel.SONGS_BY_TITLE);

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + titleText + "%");

            List<Song> results = new ArrayList<>();

            ResultSet rs = stmt.executeQuery();

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

        return new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<SongEmotion> getSongEmotions(int songId) {
        Connection conn = ServerApp.getConnection();

        final String query = SongDAO.songSelQueries.get(SongSel.SONG_EMOTIONS);

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, songId);

            List<SongEmotion> results = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int emoId = rs.getInt("emotion_id");
                int userId = rs.getInt("user_id");
                int rating = rs.getInt("rating");
                String notes = rs.getString("notes");

                results.add(new SongEmotion(emoId, songId, userId, rating, notes));
            }
            return results;

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }

        return new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<String> getSongEmotionNotes(int songId, int emotionId) {
        Connection conn = ServerApp.getConnection();

        final String query = SongDAO.songSelQueries.get(SongSel.SONG_EMOTION_NOTES);

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, songId);
            stmt.setInt(2, emotionId);

            ResultSet rs = stmt.executeQuery();
            List<String> results = new ArrayList<>();

            while (rs.next()) {
                String notes = rs.getString("notes");
                results.add(notes);
            }
            return results;

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
            return new ArrayList<>();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<SongEmotion> getUserSongEmotionsCountRating(int userId, int songId) {
        Connection conn = ServerApp.getConnection();

        final String query = SongDAO.songSelQueries.get(SongSel.SONG_EMOTIONS_RATING);

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, songId);
            stmt.setInt(2, userId);

            List<SongEmotion> results = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int emoId = rs.getInt("emotion_id");
                int rating = rs.getInt("rating");
                String notes = rs.getString("notes");

                SongEmotion songEmotion = new SongEmotion(emoId, songId, userId, rating, notes);

                results.add(songEmotion);
            }

            return results;

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
            return new ArrayList<>();
        }
    }

    //================================================================================
    // INSERT
    //================================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void setSongEmotion(int userId, int songId, int emotionId, int rating) {
        Connection conn = ServerApp.getConnection();
        final String query = SongDAO.songInsQueries.get(SongIns.SONG_EMOTION);

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, songId);
            stmt.setInt(3, emotionId);
            stmt.setInt(4, rating);
            stmt.execute();

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void setSongEmotionNotes(int userId, int songId, int emotionId, String notes) {
        Connection conn = ServerApp.getConnection();
        final String query = SongDAO.songInsQueries.get(SongIns.SONG_EMOTION_NOTES);

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, songId);
            stmt.setInt(3, emotionId);
            stmt.setString(4, notes);
            stmt.execute();

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }
    }

    //================================================================================
    // DELETE
    //================================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized int deleteSongEmotion(int userId, int songId, int emotionId) {
        Connection conn = ServerApp.getConnection();
        final String query = SongDAO.songDelQueries.get(SongDel.SONG_EMOTION);

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, songId);
            stmt.setInt(3, emotionId);

            return stmt.executeUpdate();

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }

        return 0;
    }

}
