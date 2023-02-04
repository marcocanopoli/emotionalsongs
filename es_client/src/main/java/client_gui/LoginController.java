package client_gui;

import client.ClientApp;
import client.ClientContext;
import client.ClientLogger;
import common.User;
import common.interfaces.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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

        User user = userDAO.getUser(username.getText(), pwd.getText());
        context.setUser(user);

        ClientLogger.info("Utente '" + user.getUsername() + "': accesso effettuato");

        ((Stage) confirmLoginBtn.getScene().getWindow()).close();
    }

}