package client_gui;

import client.EsClientMain;
import common.Song;
import common.interfaces.SongDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

public class SongsController {
    @FXML
    public Label songAuthor;
    @FXML
    public Label songAlbum;
    @FXML
    public Label songTitle;
    @FXML
    public Label songYear;
    @FXML
    public Label songGenre;
    @FXML
    public Label songDuration;
    @FXML
    public ProgressBar amazementProg;
    @FXML
    public ProgressBar solemnityProg;
    @FXML
    public ProgressBar tendernessProg;
    @FXML
    public ProgressBar nostalgiaProg;
    @FXML
    public ProgressBar calmnessProg;
    @FXML
    public ProgressBar powerProg;
    @FXML
    public ProgressBar joyProg;
    @FXML
    public ProgressBar tensionProg;
    @FXML
    public ProgressBar sadnessProg;
    @FXML
    private Button searchBtn;
    @FXML
    private TextField searchText;
    @FXML
    private TableView<Song> songsTable;
    @FXML
    private TableColumn<Song, String> authorColumn;
    @FXML
    private TableColumn<Song, String> albumColumn;
    @FXML
    private TableColumn<Song, String> yearColumn;
    @FXML
    private TableColumn<Song, String> titleColumn;
    @FXML
    private TableColumn<Song, String> genreColumn;
    @FXML
    private TableColumn<Song, String> durationColumn;

    public void initialize() {

        searchBtn.setOnAction(event -> {
            SongDAO songDAO = EsClientMain.getSongDAO();
            String searched = searchText.getText().trim();

            try {
                if (!searched.isEmpty()) {
                    List<Song> results = songDAO.searchByString(searched);
                    authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
                    albumColumn.setCellValueFactory(new PropertyValueFactory<>("album"));
                    yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
                    titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
                    genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
                    durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));

                    songsTable.setRowFactory(songs -> {
                        TableRow<Song> row = new TableRow<>();
                        row.setOnMouseClicked(evt -> {
                            Song current = row.getItem();
//                            ClientContext.getInstance().setSong(row.getItem());
                            songAuthor.setText(current.getAuthor());
                            songAlbum.setText(current.getAlbum());
                            songTitle.setText(current.getTitle());
                            songYear.setText(current.getYear());
                            songGenre.setText(current.getGenre());
                            songDuration.setText(current.getDuration());
                            try {
                                HashMap<Integer, Integer> emotions = songDAO.getSongEmotions(current.id);
                                int total = songDAO.getSongEmotionsCount(current.id);

                                float[] emotionsValues = new float[9];

                                for (int i = 0; i < 8; i++) {
                                    emotionsValues[i] = emotions.get(i) != null ? emotions.get(i) : 0;

                                    if (total > 0) {
                                        emotionsValues[i] = emotionsValues[i] / total;
                                    }

                                }

                                if (total > 0) {
                                    amazementProg.setProgress(emotionsValues[0]);
                                    solemnityProg.setProgress(emotionsValues[1]);
                                    tendernessProg.setProgress(emotionsValues[2]);
                                    nostalgiaProg.setProgress(emotionsValues[3]);
                                    calmnessProg.setProgress(emotionsValues[4]);
                                    powerProg.setProgress(emotionsValues[5]);
                                    joyProg.setProgress(emotionsValues[6]);
                                    tensionProg.setProgress(emotionsValues[7]);
                                    sadnessProg.setProgress(emotionsValues[8]);
                                } else {
                                    amazementProg.setProgress(0);
                                    solemnityProg.setProgress(0);
                                    tendernessProg.setProgress(0);
                                    nostalgiaProg.setProgress(0);
                                    calmnessProg.setProgress(0);
                                    powerProg.setProgress(0);
                                    joyProg.setProgress(0);
                                    tensionProg.setProgress(0);
                                    sadnessProg.setProgress(0);
                                }
                            } catch (RemoteException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        return row;
                    });
                    songsTable.getItems().clear();
                    songsTable.getItems().addAll(results);

//                    for (Song song : results) {
//                        System.out.println(song);
//                    }

                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }
}