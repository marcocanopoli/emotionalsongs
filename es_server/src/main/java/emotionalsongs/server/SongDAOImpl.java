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

        List<Song> results = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + albumText + "%");


            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String author = rs.getString("author");
                String album = rs.getString("album");

                results.add(new Song(author, album));

            }

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }

        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<String> getAuthors(String author) {
        Connection conn = ServerApp.getConnection();

        final String query = SongDAO.songSelQueries.get(SongSel.AUTHORS);

        List<String> results = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + author + "%");


            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                results.add(rs.getString("author"));

            }

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }

        return results;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<Song> getSongsByAuthorAlbum(String authorText, String albumText) {
        Connection conn = ServerApp.getConnection();

        final String query = SongDAO.songSelQueries.get(SongSel.SONGS_BY_AUTHOR_ALBUM);

        List<Song> results = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, albumText);
            stmt.setString(2, authorText);

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

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }
        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<Song> getSongsByAuthorYear(String authorText, Integer yearText) {
        Connection conn = ServerApp.getConnection();
        final String query = SongDAO.songSelQueries.get(
                yearText == null ? SongSel.SONGS_BY_AUTHOR : SongSel.SONGS_BY_AUTHOR_YEAR);

        List<Song> results = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + authorText + "%");
            if (yearText != null) stmt.setInt(2, yearText);


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

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }

        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<Song> getSongsByTitle(String titleText) {
        Connection conn = ServerApp.getConnection();
        final String query = SongDAO.songSelQueries.get(SongSel.SONGS_BY_TITLE);
        List<Song> results = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + titleText + "%");


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

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }

        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<SongEmotion> getSongEmotions(int songId) {
        Connection conn = ServerApp.getConnection();

        final String query = SongDAO.songSelQueries.get(SongSel.SONG_EMOTIONS);

        List<SongEmotion> results = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, songId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int emoId = rs.getInt("emotion_id");
                int userId = rs.getInt("user_id");
                int rating = rs.getInt("rating");
                String notes = rs.getString("notes");

                results.add(new SongEmotion(emoId, songId, userId, rating, notes));
            }

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }

        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<String> getSongEmotionNotes(int songId, int emotionId) {
        Connection conn = ServerApp.getConnection();

        final String query = SongDAO.songSelQueries.get(SongSel.SONG_EMOTION_NOTES);

        List<String> results = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, songId);
            stmt.setInt(2, emotionId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String notes = rs.getString("notes");
                results.add(notes);
            }

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }
        return results;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<SongEmotion> getUserSongEmotionsCountRating(int userId, int songId) {
        Connection conn = ServerApp.getConnection();

        final String query = SongDAO.songSelQueries.get(SongSel.SONG_EMOTIONS_RATING);

        List<SongEmotion> results = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, songId);
            stmt.setInt(2, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int emoId = rs.getInt("emotion_id");
                int rating = rs.getInt("rating");
                String notes = rs.getString("notes");

                SongEmotion songEmotion = new SongEmotion(emoId, songId, userId, rating, notes);

                results.add(songEmotion);
            }


        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }
        return results;
    }

    //================================================================================
    // INSERT
    //================================================================================

    /**
     * {@inheritDoc}
     */
    public synchronized boolean addSong(String title, String author, String album, Integer year, String genre, Integer duration) throws RemoteException {
        Connection conn = ServerApp.getConnection();
        final String query = SongDAO.songInsQueries.get(SongIns.SONG);

        int affectedRows = 0;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, album);
            stmt.setInt(4, year);
            stmt.setString(5, genre);
            stmt.setInt(6, duration);

            affectedRows = stmt.executeUpdate();

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }

        return affectedRows > 0;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void setSongEmotion(int userId, int songId, int emotionId, int rating, String notes) {
        Connection conn = ServerApp.getConnection();
        final String query = SongDAO.songInsQueries.get(SongIns.SONG_EMOTION);

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, songId);
            stmt.setInt(3, emotionId);
            stmt.setInt(4, rating);
            stmt.setString(5, notes);
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

        int affectedRows = 0;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, songId);
            stmt.setInt(3, emotionId);

            affectedRows = stmt.executeUpdate();

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }

        return affectedRows;
    }

}
