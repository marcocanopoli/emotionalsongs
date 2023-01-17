package client_gui;

import client.ClientApp;
import common.Song;
import common.interfaces.SongDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ToggleGroup amazementGrp;
    @FXML
    public ToggleGroup solemnityGrp;
    @FXML
    public ToggleGroup tendernessGrp;
    @FXML
    public ToggleGroup nostalgiaGrp;
    @FXML
    public ToggleGroup calmnessGrp;
    @FXML
    public ToggleGroup powerGrp;
    @FXML
    public ToggleGroup joyGrp;
    @FXML
    public ToggleGroup tensionGrp;
    @FXML
    public ToggleGroup sadnessGrp;
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
    private HashMap<Integer, ToggleGroup> toggleGroups = new HashMap<>();

    private Song currentSong;


    public void initialize() {
        SongDAO songDAO = ClientApp.getSongDAO();

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

        setRatingListeners(songDAO);
    }

    private void setRatingListeners(SongDAO songDAO) {

        toggleGroups.put(1, amazementGrp);
        toggleGroups.put(2, solemnityGrp);
        toggleGroups.put(3, tendernessGrp);
        toggleGroups.put(4, nostalgiaGrp);
        toggleGroups.put(5, calmnessGrp);
        toggleGroups.put(6, powerGrp);
        toggleGroups.put(7, joyGrp);
        toggleGroups.put(8, tensionGrp);
        toggleGroups.put(9, sadnessGrp);

//        Collections.addAll(toggleGroups, amazementGrp, solemnityGrp, tendernessGrp, nostalgiaGrp, calmnessGrp, powerGrp, joyGrp, tensionGrp, sadnessGrp);
        for (Map.Entry<Integer, ToggleGroup> group :
                toggleGroups.entrySet()) {

            group.getValue().selectedToggleProperty().addListener((observable, oldVal, newVal) ->
                    {

                        if (newVal != null && currentSong != null) {

                            int emotionId = group.getKey();
                            int newRating = Integer.parseInt((String) newVal.getUserData());

                            try {

//                                if (oldVal != null && oldRating == newRating) {
//                                    group.getValue().selectToggle(null);
//                                    songDAO.deleteSongEmotion(1, currentSong.id, emotionId);
//                                } else {
                                songDAO.setSongEmotion(1, currentSong.id, emotionId, newRating);
//                                }
                                displayProgress(songDAO);
                            } catch (RemoteException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
            );

        }
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

    private void displayProgress(SongDAO songDAO) {
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

    private void displayRatings(SongDAO songDAO) {
        try {
            HashMap<Integer, Integer> ratings = songDAO.getSongEmotionsRating(1, currentSong.id);

            for (Map.Entry<Integer, ToggleGroup> group :
                    toggleGroups.entrySet()) {

                int emotionId = group.getKey();
                boolean emotionIsRated = ratings.containsKey(emotionId);

                if (emotionIsRated) {
                    int rating = ratings.get(emotionId) - 1;
                    Toggle currentToggle = group.getValue().getToggles().get(rating);
                    group.getValue().selectToggle(currentToggle);
                } else {
                    group.getValue().selectToggle(null);
                }

            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private void displaySongStats(SongDAO songDAO) {
        displayProgress(songDAO);
        displayRatings(songDAO);
    }

}