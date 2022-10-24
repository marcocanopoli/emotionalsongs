package org.canos.es_database;

import org.canos.es_common.interfaces.UserService;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class UserServiceImpl implements UserService {

    public UserServiceImpl(Registry registry) throws RemoteException {
        UserService userServiceStub = (UserService)
                UnicastRemoteObject.exportObject(this, 3939);
        registry.rebind("userService", userServiceStub);
    }

    private final QueryExecutor queryExecutor = new QueryExecutor();

    public String getUser() {
        return "pippo";
    }

    public List<String> getUsers() {
        return queryExecutor.getUsers();
    }
}