package es_database;

import es_common.interfaces.UserService;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserService {

    public UserServiceImpl(Registry registry) throws RemoteException {
        UserService userServiceStub = (UserService)
                UnicastRemoteObject.exportObject(this, 3939);
        registry.rebind("UserService", userServiceStub);
    }


    public String getUser() {
        return "pippo";
    }

    public void shutdown() throws RemoteException {
        Registry registry = LocateRegistry.getRegistry();
        try {
            registry.unbind("UserService");
        } catch (NotBoundException ignored) {
        }
        UnicastRemoteObject.unexportObject(this, false);

    }

    public List<String> getUsers() {
        return new ArrayList<>();
//        return DB_MANAGER.getUsers();
    }
}