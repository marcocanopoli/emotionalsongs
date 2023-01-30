package common;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

public class NodeHelpers {
    public static boolean createAlert(
            Alert.AlertType type,
            String title,
            String headerText,
            String message,
            boolean wait) {

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(message);

        AtomicBoolean res = new AtomicBoolean(false);

        DialogPane dialog = alert.getDialogPane();

        if (!wait) {
            final Button cancelBtn = (Button) dialog.lookupButton(ButtonType.CANCEL);
            final ButtonBar buttonBar = (ButtonBar) dialog.getChildren().get(2);
            buttonBar.getButtons().remove(cancelBtn);
        }

        Region spacer = new Region();
        ButtonBar.setButtonData(spacer, ButtonBar.ButtonData.BIG_GAP);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        dialog.applyCss();
        HBox hboxDialogPane = (HBox) dialog.lookup(".container");
        hboxDialogPane.getChildren().add(spacer);

        if (wait) {
            alert.showAndWait()
                    .filter(response -> response == ButtonType.OK)
                    .ifPresent(response -> res.set(true));
        } else {
            alert.show();
        }

        return res.get();
    }

    public static Stage createMainStage(Stage stage, URL resource, String title, int width, int height) {
        try {
            if (resource != null) {
                FXMLLoader fxmlLoader = new FXMLLoader(resource);
                Scene scene = new Scene(fxmlLoader.load(), width, height);
                stage.setTitle(title);
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.show();
            }
            return stage;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void createStage(Window owner, URL resource, String title, boolean isModal) {
        try {
            if (resource != null) {
                FXMLLoader fxmlLoader = new FXMLLoader(resource);
                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = new Stage();

                stage.initOwner(owner);
                stage.initModality(isModal ? Modality.APPLICATION_MODAL : Modality.NONE);
                stage.setAlwaysOnTop(isModal);
                stage.setResizable(false);
                stage.setTitle(title);
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
