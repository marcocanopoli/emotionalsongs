package common;

import javafx.beans.binding.Bindings;
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
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class NodeHelpers {

    NodeHelpers() {

    }

    private static void centerButtons(DialogPane dialogPane) {
        Region spacer = new Region();
        ButtonBar.setButtonData(spacer, ButtonBar.ButtonData.BIG_GAP);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        dialogPane.applyCss();
        HBox hboxDialogPane = (HBox) dialogPane.lookup(".container");
        hboxDialogPane.getChildren().add(spacer);
    }

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

        DialogPane dialogPane = alert.getDialogPane();
        centerButtons(dialogPane);

        if (!wait) {
            final Button cancelBtn = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
            final ButtonBar buttonBar = (ButtonBar) dialogPane.getChildren().get(2);
            buttonBar.getButtons().remove(cancelBtn);
        }

        if (wait) {
            alert.showAndWait()
                    .filter(response -> response == ButtonType.OK)
                    .ifPresent(response -> res.set(true));
        } else {
            alert.show();
        }

        return res.get();
    }

    public static String createTextInputDialog(
            String title,
            String headerText,
            String message,
            String defaultInput) {

        TextInputDialog dialog = new TextInputDialog(defaultInput);
        DialogPane dialogPane = dialog.getDialogPane();
        centerButtons(dialogPane);

        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        dialog.setContentText(message);

        Optional<String> result = dialog.showAndWait();

        Button okBtn = (Button) dialogPane.lookupButton(ButtonType.OK);
        TextField input = dialog.getEditor();

        okBtn.disableProperty().bind(Bindings.isNotEmpty(input.textProperty()));

        return result.orElse(null);
    }

    public static Stage createMainStage(Stage stage, URL resource, String title, Integer width, Integer height) {
        try {
            if (resource != null) {
                FXMLLoader fxmlLoader = new FXMLLoader(resource);
                Scene scene;

                if (width != null && height != null) {
                    scene = new Scene(fxmlLoader.load(), width, height);
                } else {
                    scene = new Scene(fxmlLoader.load());
                }

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
