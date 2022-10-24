package org.canos.es_database;

import org.canos.es_server.ServerLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QueryExecutor {
    static final String DB_URL = "jdbc:postgresql://localhost:5432/emotionalsongs?currentSchema=public";
    static final String USER = "postgres";
    static final String PASS = "postgres";

    public Connection openConnection() {

        try {

            return DriverManager.getConnection(DB_URL, USER, PASS);

        } catch (SQLException e) {

            ServerLogger.error(String.valueOf(e));
            return null;

        }
    }

    public List<String> getUsers() {

        try (Connection conn = openConnection();
             Statement stmt = conn.createStatement()) {
            List<String> emails = new ArrayList<>();

            ResultSet rs = stmt.executeQuery("SELECT * FROM users");
            while (rs.next()) {
                emails.add(rs.getString("email"));
            }
            return emails;


        } catch (SQLException e) {
            ServerLogger.error(String.valueOf(e));
            return new ArrayList<>();
        }
    }
}
