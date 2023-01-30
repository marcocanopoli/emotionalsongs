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
    private Label songAuthor;
    @FXML
    private Label songAlbum;
    @FXML
    private Label songTitle;
    @FXML
    private Label songYear;
    @FXML
    private Label songGenre;
    @FXML
    private Label songDuration;
    @FXML
    private ProgressBar amazementProg;
    @FXML
    private ProgressBar solemnityProg;
    @FXML
    private ProgressBar tendernessProg;
    @FXML
    private ProgressBar nostalgiaProg;
    @FXML
    private ProgressBar calmnessProg;
    @FXML
    private ProgressBar powerProg;
    @FXML
    private ProgressBar joyProg;
    @FXML
    private ProgressBar tensionProg;
    @FXML
    private ProgressBar sadnessProg;
    @FXML
    private ListView<String> commentsList;
    @FXML
    private TextArea currentComment;
    private final HashMap<Integer, Button> commentBtns = new HashMap<>();
    @FXML
    private Button sadnessComments;
    @FXML
    private Button tensionComments;
    @FXML
    private Button joyComments;
    @FXML
    private Button powerComments;
    @FXML
    private Button calmnessComments;
    @FXML
    private Button nostalgiaComments;
    @FXML
    private Button tendernessComments;
    @FXML
    private Button solemnityComments;
    @FXML
    private Button amazementComments;
    @FXML
    private Label amazementTot;
    @FXML
    private Label solemnityTot;
    @FXML
    private Label tendernessTot;
    @FXML
    private Label nostalgiaTot;
    @FXML
    private Label calmnessTot;
    @FXML
    private Label powerTot;
    @FXML
    private Label joyTot;
    @FXML
    private Label tensionTot;
    @FXML
    private Label sadnessTot;

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

        displayProgress(song);

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
                    List<String> notes = songDAO.getSongEmotionNotes(user.getId(), song.id, emotionId);
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

            float[] emoPerc = new float[9];
            int[] emoCount = new int[9];

            for (int i = 0; i < 9; i++) {
                emoCount[i] = emotions.get(i + 1) != null ? emotions.get(i + 1) : 0;

                if (total > 0) {
                    emoPerc[i] = (float) emoCount[i] / total;
                }

            }

            amazementProg.setProgress(emoPerc[0]);
            solemnityProg.setProgress(emoPerc[1]);
            tendernessProg.setProgress(emoPerc[2]);
            nostalgiaProg.setProgress(emoPerc[3]);
            calmnessProg.setProgress(emoPerc[4]);
            powerProg.setProgress(emoPerc[5]);
            joyProg.setProgress(emoPerc[6]);
            tensionProg.setProgress(emoPerc[7]);
            sadnessProg.setProgress(emoPerc[8]);

            amazementTot.setText(emoPerc[0] + "% | " + emoCount[0] + "/" + total);
            solemnityTot.setText(emoPerc[1] + "% | " + emoCount[1] + "/" + total);
            tendernessTot.setText(emoPerc[2] + "% | " + emoCount[2] + "/" + total);
            nostalgiaTot.setText(emoPerc[3] + "% | " + emoCount[3] + "/" + total);
            calmnessTot.setText(emoPerc[4] + "% | " + emoCount[4] + "/" + total);
            powerTot.setText(emoPerc[5] + "% | " + emoCount[5] + "/" + total);
            joyTot.setText(emoPerc[6] + "% | " + emoCount[6] + "/" + total);
            tensionTot.setText(emoPerc[7] + "% | " + emoCount[7] + "/" + total);
            sadnessTot.setText(emoPerc[8] + "% | " + emoCount[8] + "/" + total);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }


}