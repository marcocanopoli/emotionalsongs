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
import java.util.List;

public class SongServiceImpl implements SongService {

    public SongServiceImpl(Registry registry) throws RemoteException {
        SongService songServiceStub = (SongService)
                UnicastRemoteObject.exportObject(this, 3939);
        registry.rebind("SongService", songServiceStub);
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
