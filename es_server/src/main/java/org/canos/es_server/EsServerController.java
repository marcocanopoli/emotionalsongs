package org.canos.es_server;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class EsServerController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}