package common.interfaces;

import common.Emotion;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface EmotionDAO extends Remote {

    List<Emotion> getAllEmotions() throws RemoteException;
}
