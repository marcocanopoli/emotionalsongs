package org.canos.es_common;

public class User {
    private final String firstName;
    private final String lastName;
    private final UserAddress address;
    private final String email;
    private final String id;
    private final String password;

    private User(String firstName,
                 String lastName,
                 UserAddress address,
                 String email,
                 String id,
                 String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.email = email;
        this.id = id;
        this.password = password;
    }

    public String getID() {
        return this.id;
    }
}
