package common.interfaces;

import common.Song;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface SongService extends Remote {

    List<Song> searchByString(String searchString) throws RemoteException;
}
