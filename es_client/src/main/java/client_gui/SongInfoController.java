package client_gui;

import client.ClientApp;
import client.ClientContext;
import common.Song;
import common.User;
import common.interfaces.SongDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SongInfoController {
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
    public ListView<String> commentsList;
    @FXML
    public TextArea currentComment;
    private final HashMap<Integer, Button> commentBtns = new HashMap<>();
    @FXML
    public Button sadnessComments;
    @FXML
    public Button tensionComments;
    @FXML
    public Button joyComments;
    @FXML
    public Button powerComments;
    @FXML
    public Button calmnessComments;
    @FXML
    public Button nostalgiaComments;
    @FXML
    public Button tendernessComments;
    @FXML
    public Button solemnityComments;
    @FXML
    public Button amazementComments;

    public void initialize() {
        ClientContext context = ClientContext.getInstance();
        User user = context.getUser();
        Song song = context.getCurrentSong();
        SongDAO songDAO = ClientApp.getSongDAO();

        setCurrentSong();
        setShowCommentsListeners(songDAO, song, user);
    }

    public void setCurrentSong() {

        Song song = ClientContext.getInstance().getCurrentSong();

        songAuthor.setText(song.getAuthor());
        songAlbum.setText(song.getAlbum());
        songTitle.setText(song.getTitle());
        songYear.setText(song.getYear());
        songGenre.setText(song.getGenre());
        songDuration.setText(song.getDuration());

        displaySongStats(song);

    }

    private void setShowCommentsListeners(SongDAO songDAO, Song song, User user) {
        commentBtns.put(1, amazementComments);
        commentBtns.put(2, solemnityComments);
        commentBtns.put(3, tendernessComments);
        commentBtns.put(4, nostalgiaComments);
        commentBtns.put(5, calmnessComments);
        commentBtns.put(6, powerComments);
        commentBtns.put(7, joyComments);
        commentBtns.put(8, tensionComments);
        commentBtns.put(9, sadnessComments);

        for (Map.Entry<Integer, Button> group :
                commentBtns.entrySet()) {

            group.getValue().setOnAction(event -> {

                int emotionId = group.getKey();

                try {
                    List<String> notes = songDAO.getSongEmotionNotes(user.getID(), song.id, emotionId);
                    ObservableList<String> notesList = FXCollections.observableArrayList(notes);

                    commentsList.setItems(notesList);
                    currentComment.setText(null);

                    commentsList.setOnMouseClicked(row -> {
                        String comment = commentsList.getSelectionModel().getSelectedItem();
                        currentComment.setText(comment);
                    });


                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }


            });

        }
    }

    void displayProgress(Song song) {
        SongDAO songDAO = ClientApp.getSongDAO();

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

            amazementProg.setProgress(emotionsValues[0]);
            solemnityProg.setProgress(emotionsValues[1]);
            tendernessProg.setProgress(emotionsValues[2]);
            nostalgiaProg.setProgress(emotionsValues[3]);
            calmnessProg.setProgress(emotionsValues[4]);
            powerProg.setProgress(emotionsValues[5]);
            joyProg.setProgress(emotionsValues[6]);
            tensionProg.setProgress(emotionsValues[7]);
            sadnessProg.setProgress(emotionsValues[8]);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private void displaySongStats(Song song) {
        displayProgress(song);
    }


}