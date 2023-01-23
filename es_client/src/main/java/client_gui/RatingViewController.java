package client_gui;

import client.ClientApp;
import client.ClientContext;
import common.Emotion;
import common.Song;
import common.User;
import common.interfaces.SongDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RatingViewController {
    @FXML
    public Button sadnessComment;
    @FXML
    public Button tensionComment;
    @FXML
    public Button joyComment;
    @FXML
    public Button powerComment;
    @FXML
    public Button calmnessComment;
    @FXML
    public Button nostalgiaComment;
    @FXML
    public Button tendernessComment;
    @FXML
    public Button solemnityComment;
    @FXML
    public Button amazementComment;
    @FXML
    public VBox emotionsBox;
    @FXML
    private ToggleGroup amazementGrp;
    @FXML
    private ToggleGroup solemnityGrp;
    @FXML
    private ToggleGroup tendernessGrp;
    @FXML
    private ToggleGroup nostalgiaGrp;
    @FXML
    private ToggleGroup calmnessGrp;
    @FXML
    private ToggleGroup powerGrp;
    @FXML
    private ToggleGroup joyGrp;
    @FXML
    private ToggleGroup tensionGrp;
    @FXML
    private ToggleGroup sadnessGrp;
    @FXML
    private Button amazementReset;
    @FXML
    private Button solemnityReset;
    @FXML
    private Button tendernessReset;
    @FXML
    private Button nostalgiaReset;
    @FXML
    private Button calmnessReset;
    @FXML
    private Button powerReset;
    @FXML
    private Button joyReset;
    @FXML
    private Button tensionReset;
    @FXML
    private Button sadnessReset;
    private final HashMap<Integer, ToggleGroup> toggleGroups = new HashMap<>();
    private final HashMap<Integer, Button> resetBtns = new HashMap<>();
    private final HashMap<Integer, Button> commentBtns = new HashMap<>();

    public void initialize() throws IOException {
        ClientContext context = ClientContext.getInstance();
        Song song = context.getCurrentSong();
        User user = context.getUser();

        SongDAO songDAO = ClientApp.getSongDAO();

        List<Emotion> emotions = context.getEmotions();


        setRatingResetsListeners(songDAO, song, user.getID());
        setRatingListeners(songDAO, song, user.getID());
        setEmotionsCommentsListeners();
        displayRatings(songDAO, song, user.getID());
    }

    private void initEmotions(List<Emotion> emotions, SongDAO songDAO, Song song, int userId) {

//        HBox labels = new HBox();
//        labels.setSpacing(24);
//        labels.setPadding(new Insets(0, 0, 0, 75));

        if (song != null) {


            for (Emotion emo : emotions) {
                HBox emoBox = new HBox();
                emoBox.setSpacing(15);

                Button resetBtn = new Button("Reset");

                resetBtn.setOnAction(event -> {
                    try {
//                        group.getValue().selectToggle(null);
                        songDAO.deleteSongEmotion(userId, song.id, emo.getId());
                        displayRatings(songDAO, song, userId);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });

                emoBox.getChildren().add(resetBtn);

                for (int i = 1; i < 5; i++) {
                    RadioButton radio = new RadioButton();
                    radio.setUserData(i);
                    radio.setToggleGroup(new ToggleGroup());
                    emoBox.getChildren().add(radio);
                }

                Button notesBtn = new Button("Commenta");

                notesBtn.setOnAction(event -> {
                    System.out.println("Comment");
                });

                emoBox.getChildren().add(notesBtn);

            }
        }
    }

    private void setRatingListeners(SongDAO songDAO, Song song, int userId) {
        toggleGroups.put(1, amazementGrp);
        toggleGroups.put(2, solemnityGrp);
        toggleGroups.put(3, tendernessGrp);
        toggleGroups.put(4, nostalgiaGrp);
        toggleGroups.put(5, calmnessGrp);
        toggleGroups.put(6, powerGrp);
        toggleGroups.put(7, joyGrp);
        toggleGroups.put(8, tensionGrp);
        toggleGroups.put(9, sadnessGrp);

        for (Map.Entry<Integer, ToggleGroup> group :
                toggleGroups.entrySet()) {

            group.getValue().selectedToggleProperty().addListener((observable, oldVal, newVal) ->
                    {

                        if (newVal != null && song != null) {

                            int emotionId = group.getKey();
                            int newRating = Integer.parseInt((String) newVal.getUserData());

                            try {
                                songDAO.setSongEmotion(userId, song.id, emotionId, newRating);
//                                songsController.displayProgress(songDAO);
                                displayRatings(songDAO, song, userId);
                            } catch (RemoteException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
            );

        }
    }

    private void setRatingResetsListeners(SongDAO songDAO, Song song, int userId) {

        resetBtns.put(1, amazementReset);
        resetBtns.put(2, solemnityReset);
        resetBtns.put(3, tendernessReset);
        resetBtns.put(4, nostalgiaReset);
        resetBtns.put(5, calmnessReset);
        resetBtns.put(6, powerReset);
        resetBtns.put(7, joyReset);
        resetBtns.put(8, tensionReset);
        resetBtns.put(9, sadnessReset);

        for (Map.Entry<Integer, Button> group :
                resetBtns.entrySet()) {

            group.getValue().setOnAction(event -> {
                if (song != null) {

                    int emotionId = group.getKey();

                    try {
//                        group.getValue().selectToggle(null);
                        songDAO.deleteSongEmotion(userId, song.id, emotionId);
//                        songsController.displayProgress(songDAO);
                        displayRatings(songDAO, song, userId);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }

            });

        }
    }

    private void setEmotionsCommentsListeners() {

        commentBtns.put(1, amazementComment);
        commentBtns.put(2, solemnityComment);
        commentBtns.put(3, tendernessComment);
        commentBtns.put(4, nostalgiaComment);
        commentBtns.put(5, calmnessComment);
        commentBtns.put(6, powerComment);
        commentBtns.put(7, joyComment);
        commentBtns.put(8, tensionComment);
        commentBtns.put(9, sadnessComment);

        for (Map.Entry<Integer, Button> group :
                commentBtns.entrySet()) {

            group.getValue().setOnAction(event -> {
                System.out.println("Comment");
            });

        }
    }

    void displayRatings(SongDAO songDAO, Song song, int userId) {
        try {
            HashMap<Integer, Integer> ratings = songDAO.getSongEmotionsRating(userId, song.id);

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
}