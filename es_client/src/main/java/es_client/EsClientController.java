package es_client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class EsClientController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}