package server;

/**
 * Classe wrapper per l'avvio dell'applicazione server.
 * E' necessaria una classe wrapper in quanto l'entry point <code>ServerApp</code>
 * estende il thread <code>Application</code> causando, in fase di build,
 * una non corretta pacchettizazione e compilazione del JAR eseguibile da parte del plugin
 * {@link <a href="https://maven.apache.org/plugins/maven-shade-plugin/" >maven-shade-plugin</a>}
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 * @see ServerApp
 */
public class ServerMain {

    /**
     * Wrapper del main dell'applicazione
     *
     * @param args eventuali argomenti
     */
    public static void main(String[] args) {

        ServerApp.startApp(args);
    }
}
