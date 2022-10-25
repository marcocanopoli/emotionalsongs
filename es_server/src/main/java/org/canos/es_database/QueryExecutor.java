package org.canos.es_database;

import org.canos.es_server.ServerLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class QueryExecutor {
    static final String DB_URL = "jdbc:postgresql://localhost:5432/emotionalsongs?currentSchema=public";
    static final String USER = "postgres";
    static final String PASS = "postgres";

    public static Connection openConnection(String user, String password) {

        Connection conn = null;
        Properties connProps = new Properties();
        connProps.put("user", user);
        connProps.put("password", password);

        try {

            conn = DriverManager.getConnection(DB_URL, connProps);
            ServerLogger.info("Connected to DB");
            return conn;

        } catch (SQLException e) {

            ServerLogger.error("Server not connected : " + e);
            return null;

        }
    }

//    public List<String> getUsers() {
//
//        try (Connection conn = openConnection();
//             Statement stmt = conn.createStatement()) {
//            List<String> emails = new ArrayList<>();
//
//            ResultSet rs = stmt.executeQuery("SELECT * FROM users");
//            while (rs.next()) {
//                emails.add(rs.getString("email"));
//            }
//            return emails;
//
//
//        } catch (SQLException e) {
//            ServerLogger.error(String.valueOf(e));
//            return new ArrayList<>();
//        }
//    }
}
