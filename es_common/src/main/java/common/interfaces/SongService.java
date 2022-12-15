package common.interfaces;

import common.Song;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

public interface SongService extends Remote {

    HashMap<Integer, Integer> getSongEmotions(int songId) throws RemoteException;

    List<Song> searchByString(String searchString) throws RemoteException;
}
