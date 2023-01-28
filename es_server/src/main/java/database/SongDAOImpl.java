package database;

import common.Song;
import common.SongEmotion;
import common.interfaces.SongDAO;
import server.ServerApp;
import server.ServerLogger;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SongDAOImpl implements SongDAO {

    public SongDAOImpl(Registry registry) throws RemoteException {
        SongDAO songDAOStub = (SongDAO) UnicastRemoteObject.exportObject(this, 3939);
        registry.rebind("SongService", songDAOStub);
    }

    @Override
    public List<SongEmotion> getSongEmotionsRating(int userId, int songId) {
        Connection conn = ServerApp.getConnection();

        final String query = "SELECT *  " +
                "FROM song_emotions SE " +
                "JOIN emotions E ON SE.emotion_id = E.id " +
                "JOIN songs S ON S.id = SE.song_id " +
                "JOIN users U on U.id = SE.user_id " +
                "WHERE S.id = " + songId +
                "AND U.id = " + userId;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            List<SongEmotion> results = new ArrayList<>();

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

    @Override
    public List<String> getSongEmotionNotes(int userId, int songId, int emotionId) {
        Connection conn = ServerApp.getConnection();

        final String query = "SELECT SE.notes  " +
                "FROM song_emotions SE " +
                "WHERE SE.song_id  = " + songId +
                "AND SE.user_id  = " + userId +
                "AND SE.emotion_id = " + emotionId;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
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

    @Override
    public HashMap<Integer, Integer> getSongEmotions(int songId) {
        Connection conn = ServerApp.getConnection();

        final String query = "SELECT E.id, COUNT(E.id) " +
                "FROM song_emotions SE " +
                "JOIN emotions E ON SE.emotion_id = E.id " +
                "JOIN songs S ON S.id = SE.song_id " +
                "WHERE S.id = " + songId +
                " GROUP BY E.id";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            HashMap<Integer, Integer> results = new HashMap<>();

            while (rs.next()) {
                int emoId = rs.getInt("id");
                int count = rs.getInt("count");

                results.put(emoId, count);

            }
            return results;

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }

        return new HashMap<>();
    }

    @Override
    public HashMap<String, String> getAlbums(String album) {
        Connection conn = ServerApp.getConnection();

        final String query = "SELECT author, album " +
                "FROM songs " +
                "WHERE album ILIKE ? " +
                "GROUP BY author, album " +
                "ORDER BY album ASC";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + album + "%");

            HashMap<String, String> results = new HashMap<>();

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                results.put(rs.getString("author"), rs.getString("album"));

            }
            return results;

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }

        return new HashMap<>();
    }

    @Override
    public List<String> getAuthors(String author) {
        Connection conn = ServerApp.getConnection();

        final String query = "SELECT author " +
                "FROM songs " +
                "WHERE author ILIKE ? " +
                "GROUP BY author " +
                "ORDER BY author ASC";

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

    @Override
    public int getSongEmotionsCount(int songId) {
        Connection conn = ServerApp.getConnection();

        final String query = "SELECT COUNT(E.id) " +
                "FROM song_emotions SE " +
                "JOIN emotions E ON SE.emotion_id = E.id " +
                "JOIN songs S ON S.id = SE.song_id " +
                "WHERE S.id = " + songId;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            int count = 0;

            while (rs.next()) {

                count = rs.getInt("count");

            }

            return count;

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }

        return 0;
    }

    @Override
    public void setSongEmotion(int userId, int songId, int emotionId, int rating) {
        Connection conn = ServerApp.getConnection();
        final String query = "INSERT INTO song_emotions (user_id, song_id, emotion_id, rating) VALUES (?,?,?,?) " +
                "ON CONFLICT ON CONSTRAINT user_song_emotion DO UPDATE " +
                "SET rating = excluded.rating ";

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

    @Override
    public void setSongEmotionNotes(int userId, int songId, int emotionId, String notes) {
        Connection conn = ServerApp.getConnection();
        final String query = "INSERT INTO song_emotions (user_id, song_id, emotion_id, notes) VALUES (?,?,?,?) " +
                "ON CONFLICT ON CONSTRAINT user_song_emotion DO UPDATE " +
                "SET notes = excluded.notes ";

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

    @Override
    public int deleteSongEmotion(int userId, int songId, int emotionId) {
        Connection conn = ServerApp.getConnection();
        final String query = "DELETE FROM song_emotions " +
                "WHERE user_id = ?" +
                "AND song_id = ?" +
                "AND emotion_id = ?";

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

    @Override
    public List<Song> searchByTitle(String titleText) {
        Connection conn = ServerApp.getConnection();
        final String query = "SELECT * "
                + "FROM songs "
                + "WHERE title "
                + "ILIKE ? "
                + "ORDER BY title ASC";

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

    @Override
    public List<Song> searchByAuthorYear(String authorText, Integer yearText) {
        Connection conn = ServerApp.getConnection();
        String yearQuery = yearText != null ? "AND year = ? " : "";

        String query = "SELECT * "
                + "FROM songs "
                + "WHERE author "
                + "ILIKE ? "
                + yearQuery
                + "ORDER BY author ASC";

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

    @Override
    public List<Song> searchByAlbum(String albumText) {
        Connection conn = ServerApp.getConnection();

        String query = "SELECT * "
                + "FROM songs "
                + "WHERE album = ? "
                + "ORDER BY title ASC";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, albumText);

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
}
