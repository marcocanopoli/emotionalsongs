package database;

import common.Playlist;
import common.interfaces.PlaylistDAO;
import server.ServerApp;
import server.ServerLogger;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlaylistDAOImpl implements PlaylistDAO {

    public PlaylistDAOImpl(Registry registry) throws RemoteException {
        PlaylistDAO playlistDAOStub = (PlaylistDAO) UnicastRemoteObject.exportObject(this, 3939);
        registry.rebind("PlaylistService", playlistDAOStub);
    }

    @Override
    public Playlist getPlaylist(int playlistId) throws RemoteException {
        Connection conn = ServerApp.getConnection();

        String query = "SELECT * "
                + "FROM playlists P "
                + "WHERE  P.id = ? LIMIT 1";


        try (PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, playlistId);
            ResultSet rs = stmt.executeQuery();

            Playlist playlist = null;

            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");

                playlist = new Playlist(id, name);
            }

            return playlist;


        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }

        return null;
    }
}
