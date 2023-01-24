package common.interfaces;

import common.Playlist;
import common.Song;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface PlaylistDAO extends Remote {

    Integer addSongToPlaylist(int playlistId, int songId) throws RemoteException;

    Playlist createNewPlaylist(int userId, String name) throws RemoteException;

    Playlist getPlaylistById(int playlistId) throws RemoteException;

    Playlist getPlaylistByName(String name) throws RemoteException;

    List<Song> getPlaylistSongs(int playlistId) throws RemoteException;

    List<Playlist> getUserPlaylists(int userId) throws RemoteException;
}

