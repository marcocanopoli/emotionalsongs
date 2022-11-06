package client_gui;

import client.EsClientMain;
import common.interfaces.SongService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.rmi.RemoteException;

public class EsClientController {
    @FXML
    public Button add;

    @FXML
    protected void add() throws RemoteException {
//        UserService userService = EsClientMain.getUserService();
//        userService.addUser();
        SongService songService = EsClientMain.getSongService();
        songService.searchByString("Jimmy");
    }
}