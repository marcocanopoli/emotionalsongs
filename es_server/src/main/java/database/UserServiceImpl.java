package database;

import common.interfaces.UserService;
import server.EsServer;
import server.ServerLogger;

import java.rmi.RemoteException;
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

    @Override
    public boolean addUser(String firstName, String lastName, String cf, String address, String username, String email, String password) {
        Connection conn = EsServer.getConnection();
        String query = "INSERT INTO users (first_name, last_name, cf, address, username, email, password) "
                + "VALUES(?,?,?,?,?,?,?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, cf);
            stmt.setString(4, address);
            stmt.setString(5, username);
            stmt.setString(6, email);
            stmt.setString(7, password);

            stmt.executeUpdate();
//            int rows = stmt.executeUpdate();
//            ServerLogger.debug(String.valueOf(rows));
            ServerLogger.debug("USER " + email + " ADDED");
            return true;
        } catch (SQLException ex) {
            ServerLogger.error("USER NOT ADDED: " + ex);
            return false;
        }
    }

//    public void shutdown() throws RemoteException {
//        Registry registry = LocateRegistry.getRegistry();
//        try {
//            registry.unbind("UserService");
//        } catch (NotBoundException ignored) {
//        }
//        UnicastRemoteObject.unexportObject(this, false);
//
//    }
}