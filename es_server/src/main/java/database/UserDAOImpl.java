package database;

import common.User;
import common.interfaces.UserDAO;
import server.ServerApp;
import server.ServerLogger;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;

public class UserDAOImpl implements UserDAO {

    public UserDAOImpl(Registry registry) throws RemoteException {
        UserDAO userDAOStub = (UserDAO)
                UnicastRemoteObject.exportObject(this, 3939);
        registry.rebind("UserService", userDAOStub);
    }

    //================================================================================
    // SELECT
    //================================================================================

    @Override
    public User getUser(String username, String pwd) {
        Connection conn = ServerApp.getConnection();

        final String QUERY = "SELECT * "
                + "FROM users "
                + "WHERE username = '" + username + "' LIMIT 1";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(QUERY)) {

            User user = null;

            if (rs.next()) {
                int id = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String cf = rs.getString("cf");
                String address = rs.getString("address");
                String email = rs.getString("email");
//                String password = rs.getString("password");

                user = new User(id, firstName, lastName, cf, address, email, username);
            } else {
                ServerLogger.debug("Null user");
            }

            return user;

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
            return null;
        }

    }

    //================================================================================
    // INSERT
    //================================================================================

    @Override
    public boolean addUser(String firstName, String lastName, String cf, String address, String username, String email, String password) {
        Connection conn = ServerApp.getConnection();
        final String QUERY = "INSERT INTO users (first_name, last_name, cf, address, username, email, password) "
                + "VALUES(?,?,?,?,?,?,?)";

        try (PreparedStatement stmt = conn.prepareStatement(QUERY)) {

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, cf);
            stmt.setString(4, address);
            stmt.setString(5, username);
            stmt.setString(6, email);
            stmt.setString(7, password);

            stmt.executeUpdate();

            ServerLogger.debug("USER " + email + " ADDED");
//            User newUser = getUser(username, password);
//            ServerLogger.debug("NEW USER" + newUser);
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