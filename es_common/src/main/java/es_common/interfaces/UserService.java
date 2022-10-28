package es_common.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface UserService extends Remote {

    List<String> getUsers() throws RemoteException;
}
