package emotionalsongs.database;

import emotionalsongs.common.User;
import emotionalsongs.common.interfaces.UserDAO;
import emotionalsongs.server.ServerApp;
import emotionalsongs.server.ServerLogger;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementazione dell'interfaccia <code>UserDAO</code>
 *
 * @see UserDAO
 */
public class UserDAOImpl implements UserDAO {

    private static final String REMOTE_NAME = "UserService";

    /**
     * Costruttore della classe.
     * Si occupa del bind dello stub al registry
     *
     * @param registry il registo RMI
     * @throws RemoteException se la comunicazione col registry o l'export falliscono
     */
    public UserDAOImpl(Registry registry) throws RemoteException {
        UserDAO userDAOStub = (UserDAO)
                UnicastRemoteObject.exportObject(this, 3939);
        registry.rebind(REMOTE_NAME, userDAOStub);
    }


    /**
     * Esegue l'unbind dal registro e l'unexport del remote object
     *
     * @param registry il registro RMI
     */
    public void unexport(Registry registry) {
        try {
            registry.unbind(REMOTE_NAME);
            UnicastRemoteObject.unexportObject(this, false);
        } catch (NotBoundException | RemoteException e) {
            ServerLogger.error(REMOTE_NAME + " unexport failed");
        }

    }

    //================================================================================
    // SELECT
    //================================================================================

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

}