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

    public List<SongEmotion> getSongEmotionsRating(int userId, int songId) {
        Connection conn = ServerApp.getConnection();

        String query = "SELECT *  " +
                "FROM song_emotion SE " +
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

    public List<String> getSongEmotionNotes(int userId, int songId, int emotionId) {
        Connection conn = ServerApp.getConnection();

        String query = "SELECT SE.notes  " +
                "FROM song_emotion SE " +
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

    public HashMap<Integer, Integer> getSongEmotions(int songId) {
        Connection conn = ServerApp.getConnection();

        String query = "SELECT E.id, COUNT(E.id) " +
                "FROM song_emotion SE " +
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

    public int getSongEmotionsCount(int songId) {
        Connection conn = ServerApp.getConnection();

        String query = "SELECT COUNT(E.id) " +
                "FROM song_emotion SE " +
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

    public void setSongEmotion(int userId, int songId, int emotionId, int rating) {
        Connection conn = ServerApp.getConnection();
        String query = "INSERT INTO song_emotion (user_id, song_id, emotion_id, rating) VALUES (?,?,?,?) " +
                "ON CONFLICT ON CONSTRAINT song_emotion_user_id DO UPDATE " +
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

    public int deleteSongEmotion(int userId, int songId, int emotionId) {
        Connection conn = ServerApp.getConnection();
        String query = "DELETE FROM song_emotion " +
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

    public List<Song> searchByString(String searchString) {
        Connection conn = ServerApp.getConnection();
//        String query = "SELECT * "
//                + "FROM songs "
//                + "WHERE author LIKE '%"
//                + searchString + "%'"
//                + "OR title LIKE '%"
//                + searchString + "%'"
//                + "OR album LIKE '%"
//                + searchString + "%'";

        String query = "SELECT * "
                + "FROM songs "
                + "WHERE (author, title, album)::text "
                + "ILIKE ('%"
                + searchString + "%')"
                + "ORDER BY author ASC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
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
}
