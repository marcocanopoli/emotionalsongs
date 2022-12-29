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

                    //Actions on single table row click
                    songsTable.setRowFactory(songs -> {
                        TableRow<Song> row = new TableRow<>();
                        row.setOnMouseClicked(evt -> {
                            Song current = row.getItem();
//                            ClientContext.getInstance().setSong(row.getItem());
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
        songAuthor.setText(song.getAuthor());
        songAlbum.setText(song.getAlbum());
        songTitle.setText(song.getTitle());
        songYear.setText(song.getYear());
        songGenre.setText(song.getGenre());
        songDuration.setText(song.getDuration());

        HashMap<Integer, ToggleGroup> toggleGroups = new HashMap<>();
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
//                        System.out.println(newVal.getUserData());
                        int emotionId = group.getKey();
                        int rating = Integer.parseInt((String) newVal.getUserData());

                        try {
                            songDAO.setSongEmotion(1, song.id, emotionId, rating);
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        }
//        toggleGroups.forEach(group -> {
//        });

//        solemnityGrp.selectedToggleProperty().addListener((observable, oldVal, newVal) ->
//                System.out.println(newVal.getUserData())
//        );
//        tendernessGrp.selectedToggleProperty().addListener((observable, oldVal, newVal) ->
//                System.out.println(newVal.getUserData())
//        );
//        nostalgiaGrp.selectedToggleProperty().addListener((observable, oldVal, newVal) ->
//                System.out.println(newVal.getUserData())
//        );
//        calmnessGrp.selectedToggleProperty().addListener((observable, oldVal, newVal) ->
//                System.out.println(newVal.getUserData())
//        );
//        powerGrp.selectedToggleProperty().addListener((observable, oldVal, newVal) ->
//                System.out.println(newVal.getUserData())
//        );
//        joyGrp.selectedToggleProperty().addListener((observable, oldVal, newVal) ->
//                System.out.println(newVal.getUserData())
//        );
//        tensionGrp.selectedToggleProperty().addListener((observable, oldVal, newVal) ->
//                System.out.println(newVal.getUserData())
//        );
//        sadnessGrp.selectedToggleProperty().addListener((observable, oldVal, newVal) ->
//                System.out.println(newVal.getUserData())
//        );

        try {
            HashMap<Integer, Integer> emotions = songDAO.getSongEmotions(song.id);
            int total = songDAO.getSongEmotionsCount(song.id);

            float[] emotionsValues = new float[9];

            for (int i = 0; i < 9; i++) {
                emotionsValues[i] = emotions.get(i + 1) != null ? emotions.get(i + 1) : 0;

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
    }
}