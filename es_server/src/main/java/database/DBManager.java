package database;

import common.User;
import server.ServerApp;
import server.ServerLogger;

import java.sql.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
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

//    public static void createTable(String query) {
//        Connection conn = EsServer.getConnection();
//        try (Statement stmt = conn.createStatement()) {
//            stmt.executeUpdate(query);
//        } catch (SQLException e) {
//            ServerLogger.error("Table creation error: " + e);
//        }
//    }

    public ResultSet executeQuery(String query) {

        Connection conn = ServerApp.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {


            }
            return rs;

        } catch (SQLException ex) {
            ServerLogger.error("Error: " + ex);
        }

        return null;
    }

    public static void migrate() {
        final String id = "(id SERIAL PRIMARY KEY, ";

        final String CREATE_USERS_TABLE =
                "CREATE TABLE IF NOT EXISTS users " +
                        id +
                        "first_name VARCHAR(60) NOT NULL, " +
                        "last_name VARCHAR(100) NOT NULL, " +
                        "cf VARCHAR(16) UNIQUE NOT NULL, " +
                        "address VARCHAR(200), " +
                        "username VARCHAR(20) UNIQUE NOT NULL, " +
                        "email VARCHAR(60) UNIQUE NOT NULL, " +
                        "password VARCHAR(50) NOT NULL)";
//                        "street_name VARCHAR(100), " +
//                        "street_number VARCHAR(15), " +
//                        "zip_code INTEGER, " +
//                        "city VARCHAR(60), " +
//                        "area VARCHAR(60))";

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
                        "name VARCHAR(20) UNIQUE NOT NULL ," +
                        "description VARCHAR)";

        final String CREATE_PLAYLISTS_TABLE =
                "CREATE TABLE IF NOT EXISTS playlists " +
                        id +
                        "user_id INTEGER REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE ," +
                        "name VARCHAR(256) UNIQUE NOT NULL)";

        final String CREATE_PLAYLIST_SONGS_TABLE =
                "CREATE TABLE IF NOT EXISTS playlist_songs " +
                        "(order_key SERIAL UNIQUE NOT NULL ," +
                        "playlist_id INTEGER REFERENCES playlists (id) ON UPDATE CASCADE ON DELETE CASCADE ," +
                        "song_id INTEGER REFERENCES songs (id) ON UPDATE CASCADE ON DELETE CASCADE ," +
                        "CONSTRAINT playlist_songs_id PRIMARY KEY (playlist_id, song_id))";

        final String CREATE_SONG_EMOTIONS_TABLE =
                "CREATE TABLE IF NOT EXISTS song_emotions " +
                        "(song_id INTEGER REFERENCES songs (id) ON UPDATE CASCADE ON DELETE CASCADE ," +
                        "emotion_id INTEGER REFERENCES emotions (id) ON UPDATE CASCADE ON DELETE CASCADE ," +
                        "user_id INTEGER REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE ," +
                        "rating INTEGER ," +
                        "notes VARCHAR(256) ," +
                        "CONSTRAINT user_song_emotion PRIMARY KEY (user_id, song_id, emotion_id))";

        Connection conn = ServerApp.getConnection();

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(CREATE_USERS_TABLE);
            stmt.executeUpdate(CREATE_SONGS_TABLE);
            stmt.executeUpdate(CREATE_EMOTIONS_TABLE);
            stmt.executeUpdate(CREATE_PLAYLISTS_TABLE);
            stmt.executeUpdate(CREATE_PLAYLIST_SONGS_TABLE);
            stmt.executeUpdate(CREATE_SONG_EMOTIONS_TABLE);
        } catch (SQLException e) {
            ServerLogger.error("Table creation error: " + e);
        }


//        createTable(CREATE_USERS_TABLE);
//        createTable(CREATE_SONGS_TABLE);
//        createTable(CREATE_EMOTIONS_TABLE);
//        createTable(CREATE_PLAYLISTS_TABLE);
//        createTable(CREATE_PLAYLIST_SONG_TABLE);
//        createTable(CREATE_SONG_EMOTION_TABLE);
    }

    public static void seed() throws SQLException {

        Connection conn = ServerApp.getConnection();

        conn.setAutoCommit(false);

        seedUsers(conn);
        seedEmotions(conn);

        conn.setAutoCommit(true);

    }

    public static void seedEmotions(Connection conn) {
        final String SEED_EMOTIONS_QUERY =
                "INSERT INTO emotions (name, description) VALUES (?, ?) " +
                        "ON CONFLICT (name) DO UPDATE " +
                        "SET description = EXCLUDED.description";

        final String[][] records = {
                {"Amazement", "Feeling of wonder or happiness."},
                {"Solemnity", "Feeling of transcendence, inspiration. Thrills."},
                {"Tenderness", "Sensuality, affect, feeling of love."},
                {"Nostalgia", "Dreamy, melancholic, sentimental feelings."},
                {"Calmness", "Relaxation, serenity, meditativeness."},
                {"Power", "Feeling strong, heroic, triumphant, energetic."},
                {"Joy", "Feels like dancing, bouncy feeling, animated, amused."},
                {"Tension", "Feeling nervous, impatient, irritated."},
                {"Sadness", "Feeling depressed, sorrowful."}
        };

        try (PreparedStatement stmt = conn.prepareStatement(SEED_EMOTIONS_QUERY)) {

            for (String[] emotion : records) {
                stmt.setString(1, emotion[0]);
                stmt.setString(2, emotion[1]);
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            ServerLogger.error("Seeder error: " + e);
        }

    }

    public static void seedUsers(Connection conn) {
        final String SEED_USERS_QUERY = "INSERT INTO users (first_name, last_name, cf, address, username, email, password) "
                + "VALUES(?,?,?,?,?,?,?) ON CONFLICT (cf) DO NOTHING";

        final List<User> users = new ArrayList<>();

        users.add(new User(
                "admin",
                "admin",
                "ADMIN",
                null,
                "admin@emotionalsongs.com",
                "admin"));

        try (PreparedStatement stmt = conn.prepareStatement(SEED_USERS_QUERY)) {

            for (User u : users) {
                stmt.setString(1, u.getFirstName());
                stmt.setString(2, u.getLastName());
                stmt.setString(3, u.getCF());
                stmt.setString(4, u.getAddress());
                stmt.setString(5, u.getUsername());
                stmt.setString(6, u.getEmail());
                stmt.setString(7, u.getUsername());
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            ServerLogger.error("Seeder error: " + e);
        }

    }
}

