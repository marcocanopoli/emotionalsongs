package client_gui;

import client.ClientApp;
import common.Song;
import common.interfaces.SongDAO;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

public class SongsControllerOld {
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
    private TableColumn<Song, Void> emotionViewColumn;
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

                    addEmotionsBtns();

                    //Actions on single table row click
//                    songsTable.setRowFactory(songs -> {
//                        TableRow<Song> row = new TableRow<>();
//                        row.setOnMouseClicked(evt -> {
//                            Song current = row.getItem();
//                            setCurrentSong(songDAO, current);
//                        });
//                        return row;
//                    });
                    songsTable.getItems().clear();
                    songsTable.getItems().addAll(results);

                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void addEmotionsBtns() {
        Callback<TableColumn<Song, Void>, TableCell<Song, Void>> cellFactory = param ->
                new TableCell<>() {
                    final HBox btnContainer;

                    private final Button viewBtn = new Button("Vedi");
                    private final Button insertBtn = new Button("Inserisci");

                    {
                        viewBtn.setOnAction(event1 -> {
                            Song song = getTableView().getItems().get(getIndex());
                            System.out.println(song);
                        });

                        insertBtn.setOnAction(event1 -> {
                            Song song = getTableView().getItems().get(getIndex());
                            System.out.println(song);
                        });

                        if (ClientApp.user != null) {
                            btnContainer = new HBox(10, viewBtn, insertBtn);
                        } else {
                            btnContainer = new HBox(viewBtn);
                        }
                        btnContainer.setAlignment(Pos.CENTER);
                    }


                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btnContainer);
                        }
                    }
                };

        emotionViewColumn.setCellFactory(cellFactory);
    }

//    public void setCurrentSong(SongDAO songDAO, Song song) {
//        currentSong = song;
//
//        songAuthor.setText(currentSong.getAuthor());
//        songAlbum.setText(currentSong.getAlbum());
//        songTitle.setText(currentSong.getTitle());
//        songYear.setText(currentSong.getYear());
//        songGenre.setText(currentSong.getGenre());
//        songDuration.setText(currentSong.getDuration());
//
//        displaySongStats(songDAO);
//
//    }

//    public void setRatingController(RatingController controller) {
//        ratingController = controller;
//    }

//    public void showRatingPane() {
//        try {
//            FXMLLoader loader = new FXMLLoader(ClientApp.class.getResource("/client_gui/ratingPane.fxml"));
//            AnchorPane ratingPane = loader.load();
//            ClientApp.ratingController = loader.getController();
//            bottomPane.getItems().add(ratingPane);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

}