package common.interfaces;

import common.Song;
import common.SongEmotion;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

public interface SongDAO extends Remote {

    List<Song> getAlbums(String album) throws RemoteException;

    List<String> getAuthors(String author) throws RemoteException;

    HashMap<Integer, Integer> getSongEmotions(int songId) throws RemoteException;

    int getSongEmotionsCount(int songId) throws RemoteException;

    List<SongEmotion> getSongEmotionsRating(int userId, int songId) throws RemoteException;

    List<String> getSongEmotionNotes(int songId, int emotionId) throws RemoteException;

    int deleteSongEmotion(int userId, int songId, int emotionId) throws RemoteException;

    void setSongEmotion(int userId, int songId, int emotionId, int rating) throws RemoteException;

    void setSongEmotionNotes(int userId, int songId, int emotionId, String notes) throws RemoteException;

    List<Song> getSongsByAuthorYear(String authorText, Integer yearText) throws RemoteException;

    List<Song> getSongsByTitle(String searchString) throws RemoteException;

    List<Song> getSongsByAuthorAlbum(String authorText, String albumText) throws RemoteException;
}
