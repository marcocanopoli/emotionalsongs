package database;

import common.Song;
import common.interfaces.SongService;
import server.EsServer;
import server.ServerLogger;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SongServiceImpl implements SongService {

    public SongServiceImpl(Registry registry) throws RemoteException {
        SongService songServiceStub = (SongService) UnicastRemoteObject.exportObject(this, 3939);
        registry.rebind("SongService", songServiceStub);
    }

    public HashMap<Integer, Integer> getSongEmotions(int songId) {
        Connection conn = EsServer.getConnection();


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
                System.out.println(emoId + " " + count);

            }
            return results;

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }

        return null;
    }

    public List<Song> searchByString(String searchString) {
        Connection conn = EsServer.getConnection();
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
                + searchString + "%')";

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

//              System.out.println(id + "\t" + year + "\t" + author + "\t" + title + "\t" + album + "\t" + genre + "\t" + duration);
                results.add(new Song(id, title, author, year, album, genre, duration));

            }
            return results;

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }

        return null;
    }
}
