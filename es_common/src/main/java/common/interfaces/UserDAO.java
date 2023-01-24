package common.interfaces;

import common.User;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UserDAO extends Remote {

    boolean addUser(String firstName, String lastName, String cf, String address, String username, String email, String password) throws RemoteException;

    User getUser(String username, String password) throws RemoteException;
}
