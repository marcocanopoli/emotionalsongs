package client_gui;

import client.ClientApp;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class SplashScreenController {

    @FXML
    private AnchorPane window;
    @FXML
    private Button loginBtn;
    @FXML
    private Button signupBtn;
    @FXML
    public Button guestBtn;

    public void initialize() {

        signupBtn.setOnAction(event ->
                ClientApp.createStage("signupView.fxml", "Registrazione utente", true)
        );

        loginBtn.setOnAction(event ->
                ClientApp.createStage("loginView.fxml", "Login", true)
        );

        guestBtn.setOnAction(event ->
                ClientApp.initLayout("rootLayout")
        );

    }
}