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

/**
 * Contiene metodi utili alla creazione di nodi JavaFX.
 * Implementa <code>Serializable</code> per lo scambio tramite RMI
 *
 * @author Marco Canopoli - Mat.731108 - Sede VA
 * @see javafx.scene.Node
 */
public class NodeHelpers {

    NodeHelpers() {

    }

    /**
     * Fix per centrare i bottoni inseriti nel <code>DialogPane</code> di un dialog di default
     *
     * @param dialogPane il pane che contiene i bottoni
     * @see DialogPane
     */
    private static void centerButtons(DialogPane dialogPane) {
        Region spacer = new Region();
        ButtonBar.setButtonData(spacer, ButtonBar.ButtonData.BIG_GAP);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        dialogPane.applyCss();
        HBox hboxDialogPane = (HBox) dialogPane.lookup(".container");
        hboxDialogPane.getChildren().add(spacer);
    }

    /**
     * Crea un nodo di tipo <code>Alert</code>
     *
     * @param type       il tipo di alert
     * @param title      il titolo dell'alert
     * @param headerText il testo de''header
     * @param message    il messaggio del body
     * @param wait       specifica se interrompere l'esecuzione fino alla scelta o chiusura dell'alert
     * @return true al click su 'OK', false altrimenti
     * @see Alert
     * @see javafx.scene.control.Alert.AlertType
     */
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

    /**
     * Crea un dialog con input di testo
     *
     * @param title        il titolo del dialog
     * @param headerText   il testo dell'header
     * @param message      il messaggio del body
     * @param defaultInput la stringa di default
     * @return la stringa di input
     * @see TextInputDialog
     */
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

    /**
     * Crea il primary stage per una applicazione modificando lo stage in input
     *
     * @param stage    lo stage sul quale settare la scene
     * @param resource l'URL della risorsa FXML
     * @param title    il titolo dello stage
     * @param width    la larghezza in px
     * @param height   l'altezza in px
     * @return lo stage creato
     * @see Stage
     * @see Scene
     * @see FXMLLoader
     */
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

    /**
     * Crea uno stage secondario per una applicazione
     *
     * @param owner    la finestra owner dello stage
     * @param resource l'URL della risorsa FXML
     * @param title    il titolo dello stage
     * @param isModal  se lo stage è una modale rimarrà sempre in primo piano
     * @see Stage
     * @see Scene
     * @see FXMLLoader
     * @see Modality
     */
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
