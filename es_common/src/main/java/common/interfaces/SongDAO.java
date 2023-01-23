package common.interfaces;

import common.Song;
import common.SongEmotion;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

public interface SongDAO extends Remote {

    HashMap<Integer, Integer> getSongEmotions(int songId) throws RemoteException;

    List<Song> searchByString(String searchString) throws RemoteException;

    int getSongEmotionsCount(int songId) throws RemoteException;

    List<SongEmotion> getSongEmotionsRating(int userId, int songId) throws RemoteException;

    List<String> getSongEmotionNotes(int userId, int songId, int emotionId) throws RemoteException;

    int deleteSongEmotion(int userId, int songId, int emotionId) throws RemoteException;

    void setSongEmotion(int userId, int songId, int emotionId, int rating) throws RemoteException;
}
