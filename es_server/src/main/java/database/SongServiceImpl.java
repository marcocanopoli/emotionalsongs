package database;

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

public class SongServiceImpl implements SongService {

    public SongServiceImpl(Registry registry) throws RemoteException {
        SongService songServiceStub = (SongService)
                UnicastRemoteObject.exportObject(this, 3939);
        registry.rebind("SongService", songServiceStub);
    }

    public void searchByString(String searchString) {

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
                + "LIKE ('%"
                + searchString + "%')";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                System.out.println(rs.getString("id") + "\t"
                        + rs.getString("year") + "\t"
                        + rs.getString("author") + "\t"
                        + rs.getString("title"));

            }

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }

    }
}
