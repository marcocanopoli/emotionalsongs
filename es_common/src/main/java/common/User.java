package common;

import java.io.Serial;
import java.io.Serializable;


/**
 * Contiene l'entità <code>User</code> salvata sul DB.
 * Implementa <code>Serializable</code> per lo scambio tramite RMI
 * Contiene costruttore e getter dei parametri.
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 * @see java.io.Serializable
 */
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1;
    private final Integer id;
    private final String firstName;
    private final String lastName;
    private final String address;
    private final String cf;
    private final String username;
    private final String email;

    public User(
            Integer id,
            String firstName,
            String lastName,
            String cf,
            String address,
            String email,
            String username) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.cf = cf;
        this.address = address;
        this.email = email;
        this.username = username;
    }

    @Override
    public String toString() {
        return (id + "\t" + firstName + "\t" + lastName + "\t" + cf + "\t" + address + "\t" + email + "\t" + username);
    }

    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCF() {
        return cf;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

}
