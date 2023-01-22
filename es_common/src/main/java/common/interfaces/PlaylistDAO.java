package common.interfaces;

import common.Playlist;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PlaylistDAO extends Remote {

    Playlist getPlaylist(int playlistId) throws RemoteException;
}
