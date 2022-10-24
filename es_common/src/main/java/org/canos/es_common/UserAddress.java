package org.canos.es_common;

public class UserAddress {
    private final String street;
    private final String streetNumber;
    private final int zipCode;
    private final String city;
    private final String area;

    private UserAddress(String street, String streetNumber, int zipCode, String city, String area) {
        this.street = street;
        this.streetNumber = streetNumber;
        this.zipCode = zipCode;
        this.city = city;
        this.area = area;
    }
}
