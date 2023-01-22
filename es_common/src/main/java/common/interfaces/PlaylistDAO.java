package common.interfaces;

import common.Playlist;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface PlaylistDAO extends Remote {

    Playlist getPlaylistById(int playlistId) throws RemoteException;

    List<Playlist> getUserPlaylists(int userId) throws RemoteException;
}
