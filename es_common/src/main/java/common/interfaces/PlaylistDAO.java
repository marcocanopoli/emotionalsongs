package common.interfaces;

import common.Playlist;
import common.Song;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface PlaylistDAO extends Remote {

    int[] addSongsToPlaylist(int playlistId, List<Integer> songIds) throws RemoteException;

    Playlist createNewPlaylist(int userId, String name, List<Song> songs) throws RemoteException;

    int deletePlaylist(int playListId) throws RemoteException;

    Playlist getPlaylistById(int playlistId) throws RemoteException;

    Playlist getPlaylistByName(String name) throws RemoteException;

    List<Song> getPlaylistSongs(int playlistId) throws RemoteException;

    List<Playlist> getUserPlaylists(int userId) throws RemoteException;
}

