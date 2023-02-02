package database;

import common.User;
import common.interfaces.UserDAO;
import server.ServerApp;
import server.ServerLogger;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

        final String query = UserDAO.userSelQueries.get(UserSel.USER);

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, pwd);

            ResultSet rs = stmt.executeQuery();

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
        final String query = UserDAO.userInsQueries.get(UserIns.USER);

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, cf);
            stmt.setString(4, address);
            stmt.setString(5, username);
            stmt.setString(6, email);
            stmt.setString(7, password);

            stmt.executeUpdate();

            ServerLogger.debug("USER " + email + " added");
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