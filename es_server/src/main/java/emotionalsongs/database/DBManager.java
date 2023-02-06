package emotionalsongs.database;

import emotionalsongs.common.NodeHelpers;
import emotionalsongs.common.PasswordEncrypter;
import emotionalsongs.common.StringHelpers;
import emotionalsongs.common.User;
import emotionalsongs.common.exceptions.EncryptionException;
import emotionalsongs.server.ServerApp;
import emotionalsongs.server.ServerLogger;
import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.text.MessageFormat;
import java.util.*;

import static java.util.Map.entry;

/**
 * La classe si occupa di gestire il database <code>PostGreSQL</code>.
 * Contiene metodi per l'apertura di connessioni verso il DB,
 * per la creazione di DB e per l'esecuzione di migrazioni e seeder.
 * Fornisce getter per la porta di comunicazione scelta.
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 */
public class DBManager {

    public DBManager() {

    }

    /**
     * Apre una connessione verso il database tramite driver JDBC
     *
     * @param host     l'indirizzo dell'host del DB
     * @param dbName   il nome del DB
     * @param user     l'utente di accesso a <code>PostGreSQL</code>
     * @param password la password di accesso a <code>PostGreSQL</code>
     * @return la connessione aperta
     * @see Driver
     * @see Connection
     */
    public Connection openConnection(String host, String port, String dbName, String user, String password) {

        String url = MessageFormat.format("jdbc:postgresql://{0}:{1}/{2}", host, port, dbName);
        Properties connProps = new Properties();
        connProps.put("user", user);
        connProps.put("password", password);

        try {
            return DriverManager.getConnection(url, connProps);
        } catch (SQLException e) {
            ServerLogger.error("Server not connected: " + e);
            NodeHelpers.createAlert(null,
                    Alert.AlertType.ERROR,
                    "Errore di connessione",
                    """
                            Impossibile connettersi al database.
                            Controllare le credenziali di accesso e riprovare.""",
                    e.getMessage(),
                    false);
            return null;
        }
    }

    /**
     * Effettua il drop di un database e ne crea uno nuovo.
     * Se il database da eliminare non viene trovato, l'operazione viene saltata .
     *
     * @param host      l'indirizzo dell'host del DB
     * @param oldDBName il nome del database da eliminare
     * @param newDBName il nome del nuovo database
     * @param user      l'utente di accesso a <code>PostGreSQL</code>
     * @param password  la password di accesso a <code>PostGreSQL</code>
     * @return true se il database è creato correttamente, false altrimenti
     */
    public static boolean createDB(String host, String port, String oldDBName, String newDBName, String user, String password) {
        String url = MessageFormat.format("jdbc:postgresql://{0}:{1}/", host, port);
        Properties connProps = new Properties();
        connProps.put("user", user);
        connProps.put("password", password);
        final String DROP_DB_QUERY = "DROP DATABASE IF EXISTS " + oldDBName;
        final String CREATE_DB_QUERY = "CREATE DATABASE " + newDBName;

        try (Connection conn = DriverManager.getConnection(url, connProps);
             Statement stmt = conn.createStatement()) {

            if (oldDBName != null) {
                stmt.execute(DROP_DB_QUERY);
                ServerLogger.debug(oldDBName.toUpperCase() + " database dropped");
            }

            stmt.execute(CREATE_DB_QUERY);
            ServerLogger.debug(newDBName.toUpperCase() + " database created");
            return true;
        } catch (SQLException e) {
            ServerLogger.error("Database creation error: " + e);
            NodeHelpers.createAlert(null,
                    Alert.AlertType.ERROR,
                    "Errore di creazione database",
                    "Impossibile creare il database",
                    e.getMessage(),
                    false);
            return false;
        }
    }

    /**
     * Migra le tabelle essenziali al funzionamento dell'applicazione.
     * Contiene le query di migrazione mappate secondo una chiave enum <code>TableMigration</code>;
     */
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
                                password VARCHAR NOT NULL)
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

    /**
     * Effettua il seed degli utenti prefefiniti per l'applicazione
     */
    public static void seedUsers() {
        final String SEED_USERS_QUERY =
                """
                        INSERT INTO users
                        (first_name, last_name, cf, address, username, email, password)
                        VALUES(?,?,?,?,?,?,?) ON CONFLICT (cf) DO NOTHING
                        """;

        final List<User> users = new ArrayList<>();

        users.add(new User(
                1,
                "admin",
                "admin",
                "ADMIN",
                null,
                "admin@emotionalsongs.com",
                "admin"));

        users.add(new User(
                1,
                "Marco",
                "Canopoli",
                "CNPMRC91M25A290J",
                "Via Brebbia 333, Cadrezzate con Osmate (VA)",
                "marco.canopoli@emotionalsongs.com",
                "canos"));

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
                stmt.setString(7, PasswordEncrypter.encryptPassword(u.getUsername() + "123"));
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
            ServerLogger.debug("Users seeder executed");
        } catch (SQLException e) {
            ServerLogger.error("Users seeder error: " + e);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new EncryptionException(null, e);
        }
    }

    /**
     * Effettua il seed delle emozioni predefinite per l'applicazione
     */
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

    /**
     * Effettua il seed di un dataset iniziale di canzoni.
     * Ai fini dimostrativi del funzionamento dell'applicazione e in mancanza di un dataset completo,
     * crea un nome album ed una durata fittizi per i record senza tali informazioni.
     *
     * @see StringHelpers per la creazie di stringhe alfabetiche
     */
    public static void seedSongs() {

        final String SEED_SONGS_QUERY =
                """
                        INSERT INTO songs
                        (author,title,year,album,genre,duration) VALUES (?,?,?,?,?,?)
                        """;

        InputStream datasetStream = DBManager.class.getResourceAsStream("songsData.csv");

        if (datasetStream != null) {
            Connection conn = ServerApp.getConnection();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(datasetStream));
                 PreparedStatement stmt = conn.prepareStatement(SEED_SONGS_QUERY)) {

                conn.setAutoCommit(false);


                String line = reader.readLine();
                String prevAuthor = "";
                int lineCounter = 0;
                int sameAuthorCounter = 0;
                int albumCounter = 0;
                int totalAlbums = 0;
                Random random = new Random();

                ServerLogger.info("Processing songs dataset...");
                while (line != null) {
                    lineCounter++;

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

                ServerLogger.info("Executing songs dataset batch insert...");
                stmt.executeBatch();
                conn.commit();
                conn.setAutoCommit(true);
                ServerLogger.info(lineCounter + " songs added to DB");
                ServerLogger.debug("Songs seeder executed");
            } catch (SQLException e) {
                ServerLogger.error("Seeder error: " + e);
            } catch (IOException e) {
                ServerLogger.error("Songs dataset file not found: " + e);
            }

        } else {
            ServerLogger.error("Songs data file not found");
        }
    }
}


