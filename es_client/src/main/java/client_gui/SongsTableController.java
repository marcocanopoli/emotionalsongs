package client_gui;

import common.Song;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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
    }

}
