package database;

import common.interfaces.UserService;
import server.EsServer;
import server.ServerLogger;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserServiceImpl implements UserService {

    public UserServiceImpl(Registry registry) throws RemoteException {
        UserService userServiceStub = (UserService)
                UnicastRemoteObject.exportObject(this, 3939);
        registry.rebind("UserService", userServiceStub);
    }

    public void addUser() {
        Connection conn = EsServer.getConnection();
        String query = "INSERT INTO users (email) "
                + "VALUES(?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "pippo@pippo.com");

            stmt.executeUpdate();
        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }
    }

    public void shutdown() throws RemoteException {
        Registry registry = LocateRegistry.getRegistry();
        try {
            registry.unbind("UserService");
        } catch (NotBoundException ignored) {
        }
        UnicastRemoteObject.unexportObject(this, false);

    }
}