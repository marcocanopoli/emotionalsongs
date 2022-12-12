package client_gui;

import client.EsClientMain;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Objects;

public class RootController {
    @FXML
    private AnchorPane window;
    @FXML
    private HBox topView;
    @FXML
    private HBox bottomView;
    @FXML
    private Button loginBtn;
    @FXML
    private Button signupBtn;
    @FXML
    private Button menuSearchBtn;
    @FXML
    private SplitPane mainView;

    public void initialize() {

        signupBtn.setOnAction(event ->
                EsClientMain.createStage("signupView.fxml", "Registrazione utente", true)
        );

        loginBtn.setOnAction(event ->
                EsClientMain.createStage("loginView.fxml", "Login", true)
        );

        menuSearchBtn.setOnAction(event -> {
            try {
                VBox searchView = FXMLLoader.load(Objects.requireNonNull(RootController.class.getResource("/client_gui/searchView.fxml")));
                mainView.getItems().clear();
                mainView.getItems().add(searchView);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }
}