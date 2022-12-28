package common.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UserDAO extends Remote {

    boolean addUser(String firstName, String lastName, String cf, String address, String username, String email, String password) throws RemoteException;
}
