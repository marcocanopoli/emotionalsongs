package client_gui;

import client.EsClientMain;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Objects;

public class RootController {
    @FXML
    public AnchorPane window;
    @FXML
    public HBox topView;
    @FXML
    public HBox bottomView;
    @FXML
    public Button loginBtn;
    @FXML
    public Button signupBtn;
    @FXML
    public Button menuSearchBtn;

    @FXML

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
                topView.getChildren().clear();
                topView.getChildren().add(searchView);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }
}