package client_gui;

import client.ClientApp;
import common.interfaces.SongDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import static client_gui.SongsController.currentSong;

public class RatingController {
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
    private final HashMap<Integer, ToggleGroup> toggleGroups = new HashMap<>();

    private SongsController songsController;


    public void initialize() throws IOException {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client_gui/songsView.fxml"));
//        SplitPane songsView = loader.load();
//        songsController = loader.getController();

        SongDAO songDAO = ClientApp.getSongDAO();

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
                                songsController.displayProgress(songDAO);
                            } catch (RemoteException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
            );

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

    public void setSongsController(SongsController controller) {
        songsController = controller;
    }

}