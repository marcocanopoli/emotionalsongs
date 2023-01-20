package client_gui;

import client.ClientApp;
import common.interfaces.SongDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import static client_gui.SongsController.currentSong;

public class RatingController {
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
    private final HashMap<Integer, Button> resets = new HashMap<>();

    private SongsController songsController;


    public void initialize() throws IOException {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client_gui/songsView.fxml"));
//        SplitPane songsView = loader.load();
//        songsController = loader.getController();

        SongDAO songDAO = ClientApp.getSongDAO();

        setRatingResetsListeners(songDAO);
        setRatingListeners(songDAO);
    }
    
    public void setSongsController(SongsController controller) {
        songsController = controller;
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

        for (Map.Entry<Integer, ToggleGroup> group :
                toggleGroups.entrySet()) {

            group.getValue().selectedToggleProperty().addListener((observable, oldVal, newVal) ->
                    {

                        if (newVal != null && currentSong != null) {

                            int emotionId = group.getKey();
                            int newRating = Integer.parseInt((String) newVal.getUserData());

                            try {
                                songDAO.setSongEmotion(1, currentSong.id, emotionId, newRating);
                                songsController.displayProgress(songDAO);
                            } catch (RemoteException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
            );

        }
    }

    private void setRatingResetsListeners(SongDAO songDAO) {

        resets.put(1, amazementReset);
        resets.put(2, solemnityReset);
        resets.put(3, tendernessReset);
        resets.put(4, nostalgiaReset);
        resets.put(5, calmnessReset);
        resets.put(6, powerReset);
        resets.put(7, joyReset);
        resets.put(8, tensionReset);
        resets.put(9, sadnessReset);

        for (Map.Entry<Integer, Button> group :
                resets.entrySet()) {

            group.getValue().setOnAction(event -> {
                if (currentSong != null) {

                    int emotionId = group.getKey();

                    try {
//                        group.getValue().selectToggle(null);
                        songDAO.deleteSongEmotion(1, currentSong.id, emotionId);
                        songsController.displayProgress(songDAO);
                        displayRatings(songDAO);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }

            });

        }
    }

    void displayRatings(SongDAO songDAO) {
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


}