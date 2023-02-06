package emotionalsongs.common.interfaces;

import emotionalsongs.common.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import static java.util.Map.entry;

/**
 * Interface per il DAO layer riguardante le operazioni sull'entità <code>User</code>.
 * E' utilizzato dal client per conoscere i metodi disponibili e implementata dal server che ne
 * definisce il reale comportameto per i metodi.
 * <p>
 * Contiene mappe di query per le operazioni di <code>INSERT, DELETE</code> accessibili tramite chiave enum e
 * strutturate per essere utlizzate tramite <code>PreparedStatement</code> offerto da <code>java.sql</code>
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 * @see User
 * @see UserSel
 * @see UserIns
 * @see emotionalsongs.database
 */
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

    /**
     * Getter dell'utente secondo username e password
     *
     * @param username lo username
     * @param password la password
     * @return l'utente, se trovato
     * @throws RemoteException se lo stub non è raggiungibile
     */
    User getUser(String username, String password) throws RemoteException;

    //================================================================================
    // INSERT
    //================================================================================

    /**
     * Inserisce un nuov utente a DB
     *
     * @param firstName il nome
     * @param lastName  il cognome
     * @param cf        il codice fiscale
     * @param address   l'indirizzo
     * @param username  lo username
     * @param email     l'indirizzo email
     * @param password  la password
     * @return true se l'utente è stato aggiunto, false altrimenti
     * @throws RemoteException se lo stub non è raggiungibile
     */
    boolean addUser(String firstName, String lastName, String cf, String address, String username, String email, String password) throws RemoteException;

}
