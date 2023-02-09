package emotionalsongs.client.gui;

import emotionalsongs.client.ClientApp;
import emotionalsongs.exceptions.RMIRegistryNotFoundException;
import emotionalsongs.exceptions.RMIStubException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Controller per FXML del dialog di benvenuto.
 * Mostra un input per la scelta dell'host del database
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 */
public class ServerHostController {

    @FXML
    public TextField dbHost;
    public Button connectBtn;

    /**
     * Metodo di inizializzazione chiamato alla creazione del dialog.
     * Aggiunge i binding per la validazione dei campi
     */
    public void initialize() {

        connectBtn.disableProperty().bind(dbHost.textProperty().isEmpty());

    }

    /**
     * Effettua la connessione al server presso l'host fornito in input
     */
    @FXML
    private void connect() {

        String serverHost = dbHost.getText();

        try {

            ClientApp.init(serverHost);
            ((Stage) connectBtn.getScene().getWindow()).close();

        } catch (RemoteException e) {
            throw new RMIRegistryNotFoundException(e);
        } catch (NotBoundException e) {
            throw new RMIStubException(e);
        }
    }

}