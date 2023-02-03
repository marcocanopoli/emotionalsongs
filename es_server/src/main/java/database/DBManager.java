package database;

import common.StringHelpers;
import common.User;
import server.ServerApp;
import server.ServerLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.MessageFormat;
import java.util.*;

import static java.util.Map.entry;

public class DBManager {
    private static int port = 5433;

    public DBManager() {

    }

    public DBManager(int port) {
        setPort(port);
    }

    public static String getPort() {
        return String.valueOf(port);
    }

    public static void setPort(int newPort) {
        port = newPort;
    }

    public Connection openConnection(String host, String dbName, String user, String password) {

        String url = MessageFormat.format("jdbc:postgresql://{0}:{1}/{2}", host, getPort(), dbName);
        Properties connProps = new Properties();
        connProps.put("user", user);
        connProps.put("password", password);

        try {
            Connection conn = DriverManager.getConnection(url, connProps);
            ServerLogger.info("Connected to DB");
            return conn;
        } catch (SQLException e) {
            ServerLogger.error("Server not connected: " + e);
            return null;
        }
    }

    public static boolean createDB(String host, String oldDBName, String newDBName, String user, String password) throws SQLException {
        String url = MessageFormat.format("jdbc:postgresql://{0}:{1}/", host, getPort());
        Properties connProps = new Properties();
        connProps.put("user", user);
        connProps.put("password", password);
        Connection conn = DriverManager.getConnection(url, connProps);

        final String DROP_DB_QUERY = "DROP DATABASE IF EXISTS " + oldDBName;
        final String CREATE_DB_QUERY = "CREATE DATABASE " + newDBName;

        try (Statement stmt = conn.createStatement()) {

            int dropped = stmt.executeUpdate(DROP_DB_QUERY);
            ServerLogger.debug(oldDBName.toUpperCase() + (dropped > 0 ? " database dropped" : " database not found, drop skipped"));
            stmt.executeUpdate(CREATE_DB_QUERY);
            ServerLogger.debug(newDBName.toUpperCase() + " database created");
            return true;
        } catch (SQLException e) {
            ServerLogger.error("Database creation error: " + e);
        }
        return false;
    }

    public static void migrate() {
        enum TableMigration {USERS, EMOTIONS, SONGS, SONG_EMOTIONS, PLAYLISTS, PLAYLIST_SONGS}

        final Map<TableMigration, String> createTableQueries = Map.ofEntries(
                entry(
                        TableMigration.USERS,
                        """
                                CREATE TABLE IF NOT EXISTS users
                                (id SERIAL PRIMARY KEY,
                                first_name VARCHAR(60) NOT NULL,
                                last_name VARCHAR(100) NOT NULL,
                                cf VARCHAR(16) UNIQUE NOT NULL,
                                address VARCHAR(200),
                                username VARCHAR(20) UNIQUE NOT NULL,
                                email VARCHAR(60) UNIQUE NOT NULL,
                                password VARCHAR(50) NOT NULL)
                                """
//                        "street_name VARCHAR(100), " +
//                        "street_number VARCHAR(15), " +
//                        "zip_code INTEGER, " +
//                        "city VARCHAR(60), " +
//                        "area VARCHAR(60))";
                ),
                entry(
                        TableMigration.EMOTIONS,
                        """
                                CREATE TABLE IF NOT EXISTS emotions
                                (id SERIAL PRIMARY KEY,
                                name VARCHAR(20) UNIQUE NOT NULL,
                                description VARCHAR)
                                """
                ),
                entry(
                        TableMigration.SONGS,
                        """
                                CREATE TABLE IF NOT EXISTS songs
                                (id SERIAL PRIMARY KEY,
                                title VARCHAR NOT NULL,
                                author VARCHAR NOT NULL,
                                year SMALLINT NOT NULL,
                                album VARCHAR,
                                genre VARCHAR,
                                duration INTEGER)
                                """
                ),
                entry(
                        TableMigration.SONG_EMOTIONS,
                        """
                                CREATE TABLE IF NOT EXISTS song_emotions
                                (song_id INTEGER REFERENCES songs (id) ON UPDATE CASCADE ON DELETE CASCADE ,
                                emotion_id INTEGER REFERENCES emotions (id) ON UPDATE CASCADE ON DELETE CASCADE ,
                                user_id INTEGER REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE ,
                                rating INTEGER ,
                                notes VARCHAR(256) ,
                                CONSTRAINT user_song_emotion PRIMARY KEY (user_id, song_id, emotion_id))
                                """
                ),
                entry(
                        TableMigration.PLAYLISTS,
                        """
                                CREATE TABLE IF NOT EXISTS playlists
                                (id SERIAL PRIMARY KEY,
                                user_id INTEGER REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE ,
                                name VARCHAR(256) UNIQUE NOT NULL)
                                """
                ),
                entry(
                        TableMigration.PLAYLIST_SONGS,
                        """
                                CREATE TABLE IF NOT EXISTS playlist_songs
                                (order_key SERIAL UNIQUE NOT NULL ,
                                playlist_id INTEGER REFERENCES playlists (id) ON UPDATE CASCADE ON DELETE CASCADE ,
                                song_id INTEGER REFERENCES songs (id) ON UPDATE CASCADE ON DELETE CASCADE ,
                                CONSTRAINT playlist_songs_id PRIMARY KEY (playlist_id, song_id))"""
                )
        );

        Connection conn = ServerApp.getConnection();

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableQueries.get(TableMigration.USERS));
            ServerLogger.debug("USERS table was migrated");
            stmt.executeUpdate(createTableQueries.get(TableMigration.EMOTIONS));
            ServerLogger.debug("EMOTIONS table was migrated");
            stmt.executeUpdate(createTableQueries.get(TableMigration.SONGS));
            ServerLogger.debug("SONGS table was migrated");
            stmt.executeUpdate(createTableQueries.get(TableMigration.SONG_EMOTIONS));
            ServerLogger.debug("SONG_EMOTIONS table was migrated");
            stmt.executeUpdate(createTableQueries.get(TableMigration.PLAYLISTS));
            ServerLogger.debug("PLAYLISTS table was migrated");
            stmt.executeUpdate(createTableQueries.get(TableMigration.PLAYLIST_SONGS));
            ServerLogger.debug("PLAYLIST_SONGS table was migrated");
        } catch (SQLException e) {
            ServerLogger.error("Migration error: " + e);
        }
    }

    public static void seedUsers() {
        final String SEED_USERS_QUERY =
                """
                        INSERT INTO users
                        (first_name, last_name, cf, address, username, email, password)
                        VALUES(?,?,?,?,?,?,?) ON CONFLICT (cf) DO NOTHING
                        """;

        final List<User> users = new ArrayList<>();

        users.add(new User(
                "admin",
                "admin",
                "ADMIN",
                null,
                "admin@emotionalsongs.com",
                "admin"));

        Connection conn = ServerApp.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(SEED_USERS_QUERY)) {

            conn.setAutoCommit(false);

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
            conn.setAutoCommit(true);
            ServerLogger.debug("Users seeder executed");
        } catch (SQLException e) {
            ServerLogger.error("Users seeder error: " + e);
        }

    }

    public static void seedEmotions() {
        final String SEED_EMOTIONS_QUERY =
                """
                        INSERT INTO emotions (name, description) VALUES (?, ?)
                        ON CONFLICT (name) DO UPDATE
                        SET description = EXCLUDED.description
                        """;

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

        Connection conn = ServerApp.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(SEED_EMOTIONS_QUERY)) {

            conn.setAutoCommit(false);

            for (String[] emotion : records) {
                stmt.setString(1, emotion[0]);
                stmt.setString(2, emotion[1]);
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
            ServerLogger.debug("Emotions seeder executed");
        } catch (SQLException e) {
            ServerLogger.error("Seeder error: " + e);
        }

    }

    public static void seedSongs() throws IOException {

        final String SEED_SONGS_QUERY =
                """
                        INSERT INTO songs
                        (author,title,year,album,genre,duration) VALUES (?,?,?,?,?,?)
                        """;

        Connection conn = ServerApp.getConnection();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
                DBManager.class.getResourceAsStream("songsData.csv")), StandardCharsets.UTF_8));
             PreparedStatement stmt = conn.prepareStatement(SEED_SONGS_QUERY)) {

            conn.setAutoCommit(false);


            String line = reader.readLine();
            String prevAuthor = "";
            int lineCounter = 0;
            int sameAuthorCounter = 0;
            int albumCounter = 0;
            int totalAlbums = 0;
            Random random = new Random();

            while (line != null) {
                lineCounter++;
                ServerLogger.debug("Processing: " + lineCounter);

                String[] fields = line.split(",");
                String author = fields[0].trim();
                String title = fields[1].trim();
                int year = Integer.parseInt(fields[2]);
                String genre = fields[3].trim().equals("null") ? "" : fields[3].trim();
                int duration;

                if (fields[4].trim().equals("null")) {
                    duration = random.nextInt(300 - 120) + 120;
                } else {
                    duration = Integer.parseInt(fields[4]);
                }

                if (prevAuthor.equals(author)) {
                    sameAuthorCounter++;
                    if (sameAuthorCounter >= 5) {
                        sameAuthorCounter = 0;
                        albumCounter++;
                    }
                } else {
                    albumCounter = 0;
                    sameAuthorCounter = 0;
                    totalAlbums++;
                }

                String album = "album_" + StringHelpers.toAlphabetic(totalAlbums) + albumCounter;
                prevAuthor = author;

                stmt.setString(1, author);
                stmt.setString(2, title);
                stmt.setInt(3, year);
                stmt.setString(4, album);
                stmt.setString(5, genre);
                stmt.setInt(6, duration);

                stmt.addBatch();

                line = reader.readLine();
            }

            stmt.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
            ServerLogger.debug("Songs seeder executed");
        } catch (SQLException e) {
            ServerLogger.error("Seeder error: " + e);
        }
    }
}


