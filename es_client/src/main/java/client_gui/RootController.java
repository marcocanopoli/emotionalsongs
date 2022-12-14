package client_gui;

import client.EsClientMain;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class RootController {

    @FXML
    private AnchorPane window;
    @FXML
    private Button loginBtn;
    @FXML
    private Button signupBtn;
    @FXML
    private Button menuSearchBtn;
    @FXML
    private AnchorPane mainView;

    public void initialize() {
        EsClientMain.showSongsView(mainView);

        signupBtn.setOnAction(event ->
                EsClientMain.createStage("signupView.fxml", "Registrazione utente", true)
        );

        loginBtn.setOnAction(event ->
                EsClientMain.createStage("loginView.fxml", "Login", true)
        );

        menuSearchBtn.setOnAction(event ->
                EsClientMain.showSongsView(mainView)
        );

    }
}