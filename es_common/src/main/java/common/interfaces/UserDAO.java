package common.interfaces;

import common.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import static java.util.Map.entry;

public interface UserDAO extends Remote {

    enum UserSel {USER}

    Map<UserSel, String> userSelQueries = Map.ofEntries(
            entry(
                    UserSel.USER,
                    """
                            SELECT *
                            FROM users
                            WHERE username = ?
                            AND password = ?
                            LIMIT 1
                            """
            )
    );

    enum UserIns {USER}

    Map<UserIns, String> userInsQueries = Map.ofEntries(
            entry(
                    UserIns.USER,
                    """
                            INSERT INTO users
                            (first_name, last_name, cf, address, username, email, password)
                            VALUES(?,?,?,?,?,?,?)
                            """
            )
    );

    //================================================================================
    // SELECT
    //================================================================================

    User getUser(String username, String password) throws RemoteException;

    //================================================================================
    // INSERT
    //================================================================================

    boolean addUser(String firstName, String lastName, String cf, String address, String username, String email, String password) throws RemoteException;

}
