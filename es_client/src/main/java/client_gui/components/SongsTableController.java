package client_gui.components;

import client.ClientApp;
import client.ClientContext;
import common.Song;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class SongsTableController {
    @FXML
    private TableView<Song> songsTable;

    public void initialize() {

        String[][] columns = {
                {"author", "Autore"},
                {"title", "Titolo"},
                {"album", "Album"},
                {"year", "Anno"},
                {"genre", "Genere"},
                {"duration", "Durata"}
        };

        for (String[] column : columns) {
            TableColumn<Song, String> tableCol = new TableColumn<>(column[1]);
            tableCol.setCellValueFactory(new PropertyValueFactory<>(column[0]));
            tableCol.setMinWidth(100);
            tableCol.setResizable(true);
            songsTable.getColumns().add(tableCol);
        }

        addEmotionsInfoBtn();
    }


    private void addEmotionsInfoBtn() {
        ClientContext context = ClientContext.getInstance();

        TableColumn<Song, Void> emotionColumn = new TableColumn<>("Dettagli");
        Callback<TableColumn<Song, Void>, TableCell<Song, Void>> cellFactory = param -> new TableCell<>() {
            final HBox btnBox = new HBox();

            final Button viewBtn = new Button("Vedi emozioni");

            {
                viewBtn.setOnAction(event1 -> {
                    Song song = songsTable.getItems().get(getIndex());
                    context.setCurrentSong(song);
                    ClientApp.createStage("songInfoView.fxml", "Info canzone", true);
                });

                btnBox.getChildren().add(viewBtn);
                btnBox.setAlignment(Pos.CENTER);
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnBox);
                }
            }
        };


        emotionColumn.setMinWidth(120);
        emotionColumn.setCellFactory(cellFactory);
        songsTable.getColumns().add(emotionColumn);
    }

}
