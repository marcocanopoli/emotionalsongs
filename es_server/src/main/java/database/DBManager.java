package database;

import server.EsServer;
import server.ServerLogger;

import java.sql.*;
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

    public static void createTable(String query) {
        Connection conn = EsServer.getConnection();
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            ServerLogger.error("Error: " + e);
        }
    }

    public static void migrate() {
        final String id = "(id SERIAL PRIMARY KEY, ";

        final String CREATE_USERS_TABLE =
                "CREATE TABLE IF NOT EXISTS users " +
                        id +
                        "first_name VARCHAR(60) NOT NULL, " +
                        "last_name VARCHAR(100) NOT NULL, " +
                        "email VARCHAR(60) UNIQUE NOT NULL, " +
                        "password VARCHAR(50) NOT NULL, " +
                        "street_name VARCHAR(100), " +
                        "street_number VARCHAR(15), " +
                        "zip_code INTEGER, " +
                        "city VARCHAR(60), " +
                        "area VARCHAR(60))";

        final String CREATE_SONGS_TABLE =
                "CREATE TABLE IF NOT EXISTS songs " +
                        id +
                        "title VARCHAR NOT NULL, " +
                        "author VARCHAR NOT NULL, " +
                        "year SMALLINT NOT NULL, " +
                        "album VARCHAR, " +
                        "genre VARCHAR, " +
                        "duration INTEGER)";

        final String CREATE_EMOTIONS_TABLE =
                "CREATE TABLE IF NOT EXISTS emotions " +
                        id +
                        "name VARCHAR(20) NOT NULL)";

        final String CREATE_PLAYLISTS_TABLE =
                "CREATE TABLE IF NOT EXISTS playlists " +
                        id +
                        "user_id INTEGER REFERENCES users (id))";

        final String CREATE_PLAYLIST_SONG_TABLE =
                "CREATE TABLE IF NOT EXISTS playlist_song " +
                        "(playlist_id INTEGER REFERENCES playlists (id) ON UPDATE CASCADE ON DELETE CASCADE ," +
                        "song_id INTEGER REFERENCES songs (id) ON UPDATE CASCADE ON DELETE CASCADE ," +
                        "CONSTRAINT playlist_song_id PRIMARY KEY (playlist_id, song_id))";

        final String CREATE_SONG_EMOTION_TABLE =
                "CREATE TABLE IF NOT EXISTS song_emotion " +
                        "(song_id INTEGER REFERENCES songs (id) ON UPDATE CASCADE ON DELETE CASCADE ," +
                        "emotion_id INTEGER REFERENCES emotions (id) ON UPDATE CASCADE ON DELETE CASCADE ," +
                        "CONSTRAINT song_emotion_id PRIMARY KEY (song_id, emotion_id))";


        createTable(CREATE_USERS_TABLE);
        createTable(CREATE_SONGS_TABLE);
        createTable(CREATE_EMOTIONS_TABLE);
        createTable(CREATE_PLAYLISTS_TABLE);
        createTable(CREATE_PLAYLIST_SONG_TABLE);
        createTable(CREATE_SONG_EMOTION_TABLE);
    }

    public static void seed() {
        final String SEED_EMOTIONS_QUERY = "INSERT INTO emotions (name) VALUES (?)";
        final String[] records = {"PIPPO", "PLUTO"};

        Connection conn = EsServer.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(SEED_EMOTIONS_QUERY)) {

            conn.setAutoCommit(false);

            for (String emotion : records) {
                stmt.setString(1, emotion);
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            ServerLogger.error("Error: " + e);
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
