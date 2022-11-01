package common.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UserService extends Remote {

    void addUser() throws RemoteException;
}
