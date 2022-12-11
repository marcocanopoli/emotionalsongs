package client_gui;

import client.EsClientMain;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

public class EsClientRootController {
    @FXML
    public AnchorPane window;
    @FXML
    public HBox topView;
    @FXML
    public HBox bottomView;
    public AnchorPane searchBtn;
    @FXML
    public Button loginBtn;
    @FXML
    public Button signupBtn;

    @FXML

    public void initialize() {

        signupBtn.setOnAction(event ->
                EsClientMain.createStage("signupView.fxml", "Registrazione utente", true)
        );

        loginBtn.setOnAction(event -> {
            EsClientMain.createStage("loginView.fxml", "Login", true);
//            try {
//                VBox loginView = FXMLLoader.load(Objects.requireNonNull(EsClientRootController.class.getResource("/client_gui/loginView.fxml")));
//                topView.getChildren().clear();
//                topView.getChildren().add(loginView);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
        });

//        SongService songService = EsClientMain.getSongService();
//        songService.searchByString("Jimmy");

    }
}