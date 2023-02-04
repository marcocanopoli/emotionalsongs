package client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Logger per l'applicazione client strutturato su 3 livelli di tracciamento: debug, info, error
 * Permette di visualizzare i log in console e il salvataggio in 3 appositi file di testo
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 * {@link <a href="https://logging.apache.org/log4j/2.x/">log4j2</a>}
 */
public class ClientLogger {

    private static final Logger logger = LogManager.getLogger(ClientLogger.class.getName());

    private ClientLogger() {
    }

    /**
     * Logga un messaggio a livello <strong>DEBUG</strong>
     *
     * @param msg il messaggio da loggare
     */
    public static void debug(String msg) {
        logger.debug(msg);
    }

    /**
     * Logga un messaggio a livello <strong>INFO</strong>
     *
     * @param msg il messaggio da loggare
     */
    public static void info(String msg) {
        logger.info(msg);
    }

    /**
     * Logga un messaggio a livello <strong>ERROR</strong>
     *
     * @param msg l'errore da loggare
     */
    public static void error(String msg) {
        logger.error(msg);
    }
}