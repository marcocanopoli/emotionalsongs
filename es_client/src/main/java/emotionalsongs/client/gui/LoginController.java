package emotionalsongs.client.gui;

import emotionalsongs.client.ClientApp;
import emotionalsongs.client.ClientContext;
import emotionalsongs.client.ClientLogger;
import emotionalsongs.common.NodeHelpers;
import emotionalsongs.common.User;
import emotionalsongs.common.interfaces.UserDAO;
import emotionalsongs.exceptions.RMIStubException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.rmi.RemoteException;

/**
 * Controller per FXML del dialog di login.
 * Mostra un form di login con username e password
 * e effettua l'operazione di login.
 * Include le validazioni per la password
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 */
public class LoginController {

    @FXML
    public TextField username;
    @FXML
    public PasswordField pwd;
    @FXML
    public Button confirmLoginBtn;

    /**
     * Metodo di inizializzazione chiamato alla creazione del dialog.
     * Aggiunge i listener per la validazione della password e i campi vuoti
     */
    public void initialize() {


//        if (ClientContext.getUser() != null) {
//            username.setDisable(true);
//            pwd.setDisable(true);
//            confirmLoginBtn.setText("Logout");
//        }
    }

    /**
     * Effettua il login ricercando sul database l'utente corrispondente
     * alle credenziali di accesso inserite
     */
    @FXML
    private void login() {
        ClientContext context = ClientContext.getInstance();
        UserDAO userDAO = ClientApp.getUserDAO();
        try {
            User user = userDAO.getUser(username.getText(), pwd.getText());
            Stage owner = (Stage) confirmLoginBtn.getScene().getWindow();

            if (user != null) {
                context.setUser(user);
                ClientLogger.debug("Utente '" + user.getUsername() + "': accesso effettuato");
                owner.close();
            } else {
                ClientLogger.error("Invalid credentials");
                NodeHelpers.createAlert(owner,
                        Alert.AlertType.ERROR,
                        "Credenziali non corrette",
                        "Utente non trovato",
                        """
                                L'utente non esiste o le credenziali inserite non sono valide.
                                Controllare username e password e riprovare""",
                        true);
            }


        } catch (RemoteException e) {
            throw new RMIStubException(e);
        }
    }

}