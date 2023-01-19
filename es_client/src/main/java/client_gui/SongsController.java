package client_gui;

import client.ClientApp;
import common.Song;
import common.interfaces.SongDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
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
    public SplitPane bottomPane;
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

    @FXML
    private RatingController ratingController;

    public static Song currentSong;

    public void initialize() throws IOException {

//        ratingController = loader.getController();

        SongDAO songDAO = ClientApp.getSongDAO();

//        if (ClientApp.user != null) {
////            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client_gui/ratingPane.fxml"));
////            AnchorPane ratingPane = loader.load();
////            bottomPane.getItems().add(ratingPane);
//
//            ClientApp.showRatingPane(bottomPane);
//        }

        searchBtn.setOnAction(event -> {
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

                    //Actions on single table row click
                    songsTable.setRowFactory(songs -> {
                        TableRow<Song> row = new TableRow<>();
                        row.setOnMouseClicked(evt -> {
                            Song current = row.getItem();
                            setCurrentSong(songDAO, current);
                        });
                        return row;
                    });
                    songsTable.getItems().clear();
                    songsTable.getItems().addAll(results);

                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void setCurrentSong(SongDAO songDAO, Song song) {
        currentSong = song;

        songAuthor.setText(currentSong.getAuthor());
        songAlbum.setText(currentSong.getAlbum());
        songTitle.setText(currentSong.getTitle());
        songYear.setText(currentSong.getYear());
        songGenre.setText(currentSong.getGenre());
        songDuration.setText(currentSong.getDuration());

        displaySongStats(songDAO);

    }

    void displayProgress(SongDAO songDAO) {
        try {
            HashMap<Integer, Integer> emotions = songDAO.getSongEmotions(currentSong.id);

            int total = songDAO.getSongEmotionsCount(currentSong.id);

            float[] emotionsValues = new float[9];

            for (int i = 0; i < 9; i++) {
                emotionsValues[i] = emotions.get(i + 1) != null ? emotions.get(i + 1) : 0;

                if (total > 0) {
                    emotionsValues[i] = emotionsValues[i] / total;
                }

            }

//            if (total > 0) {
            amazementProg.setProgress(emotionsValues[0]);
            solemnityProg.setProgress(emotionsValues[1]);
            tendernessProg.setProgress(emotionsValues[2]);
            nostalgiaProg.setProgress(emotionsValues[3]);
            calmnessProg.setProgress(emotionsValues[4]);
            powerProg.setProgress(emotionsValues[5]);
            joyProg.setProgress(emotionsValues[6]);
            tensionProg.setProgress(emotionsValues[7]);
            sadnessProg.setProgress(emotionsValues[8]);
//            } else {
//                amazementProg.setProgress(0);
//                solemnityProg.setProgress(0);
//                tendernessProg.setProgress(0);
//                nostalgiaProg.setProgress(0);
//                calmnessProg.setProgress(0);
//                powerProg.setProgress(0);
//                joyProg.setProgress(0);
//                tensionProg.setProgress(0);
//                sadnessProg.setProgress(0);
//            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private void displaySongStats(SongDAO songDAO) {
        displayProgress(songDAO);
        ratingController.displayRatings(songDAO);
    }

    public void setRatingController(RatingController controller) {
        ratingController = controller;
    }

    public void showRatingPane() {
        try {
            FXMLLoader loader = new FXMLLoader(ClientApp.class.getResource("/client_gui/ratingPane.fxml"));
            AnchorPane ratingPane = loader.load();
            ClientApp.ratingController = loader.getController();
            bottomPane.getItems().add(ratingPane);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}