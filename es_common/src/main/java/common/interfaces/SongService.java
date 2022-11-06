package common.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SongService extends Remote {

    void searchByString(String searchString) throws RemoteException;
}
