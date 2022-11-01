package database;

import server.ServerLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;

public class DBManager {
    private int port = 5433;
    private String dbms = "postgresql";

    public DBManager() {

    }

    public DBManager(int port, String dbms) {
        this.port = port;
        this.dbms = dbms;
    }

    public Connection openConnection(String host, String dbName, String user, String password) {

        Connection conn = null;
        String url = MessageFormat.format(
                "jdbc:{0}://{1}:{2}/{3}",
                this.dbms, host, String.valueOf(this.port), dbName);
        Properties connProps = new Properties();
        connProps.put("user", user);
        connProps.put("password", password);

        try {

            conn = DriverManager.getConnection(url, connProps);
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
