package common;

import java.io.Serial;
import java.io.Serializable;

public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1;
    private final int id;
    private final String firstName;
    private final String lastName;
    private final String address;
    private final String cf;
    private final String username;
    private final String email;
//    private final String password;

    public User(
            int id,
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

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }
}
