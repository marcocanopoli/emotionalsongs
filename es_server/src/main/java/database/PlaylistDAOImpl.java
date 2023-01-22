package database;

import common.Playlist;
import common.Song;
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
import java.util.ArrayList;
import java.util.List;

public class PlaylistDAOImpl implements PlaylistDAO {

    public PlaylistDAOImpl(Registry registry) throws RemoteException {
        PlaylistDAO playlistDAOStub = (PlaylistDAO) UnicastRemoteObject.exportObject(this, 3939);
        registry.rebind("PlaylistService", playlistDAOStub);
    }

    @Override
    public Playlist createNewPlaylist(int userId, String name) throws RemoteException {
        Connection conn = ServerApp.getConnection();
        String query = "INSERT INTO playlists (user_id, name) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setString(2, name);

            stmt.executeUpdate();

            return getPlaylistByName(name);

        } catch (SQLException ex) {
            ServerLogger.error("PLAYLIST NOT FOUND: " + ex);
            return null;
        }
    }

    @Override
    public Playlist getPlaylistById(int playlistId) throws RemoteException {
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

    @Override
    public Playlist getPlaylistByName(String name) throws RemoteException {
        Connection conn = ServerApp.getConnection();

        String query = "SELECT * "
                + "FROM playlists "
                + "WHERE (name) "
                + "ILIKE ('%"
                + name + "%') LIMIT 1";

        try (PreparedStatement stmt = conn.prepareStatement(query);) {
            ResultSet rs = stmt.executeQuery();

            Playlist playlist = null;

            if (rs.next()) {
                int id = rs.getInt("id");

                playlist = new Playlist(id, name);
            }

            return playlist;


        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
            return null;
        }

    }

    @Override
    public List<Song> getPlaylistSongs(int playlistId) throws RemoteException {
        Connection conn = ServerApp.getConnection();

        String query = "SELECT * " +
                "FROM songs S " +
                "JOIN playlist_song PS ON PS.song_id = S.id " +
                "WHERE PS.playlist_id = ?";

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

    @Override
    public List<Playlist> getUserPlaylists(int userId) throws RemoteException {
        Connection conn = ServerApp.getConnection();

        String query = "SELECT * "
                + "FROM playlists P "
                + "WHERE  P.user_id = ?";


        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            List<Playlist> results = new ArrayList<>();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");

                results.add(new Playlist(id, name));
            }
            return results;


        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }

        return null;
    }
}
