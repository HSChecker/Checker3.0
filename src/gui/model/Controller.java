package gui.model;

import Link.ConnectListen;
import gui.connect.Connect;
import gui.game.Game;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import tools.LOCK;
import tools.OTHER;
import tools.PARAMETER;

import java.io.IOException;

import static tools.PARAMETER.ls;

/**
 * Controller类
 *
 * @author HanShuo
 * @Date 2020/4/25 19:18
 */
public class Controller {

    @FXML
    private GridPane pane;

    @FXML
    private TextField roomID;

    @FXML
    void createRoomOtherMe(ActionEvent event) {
        ls.getBw().println("createRoom Other Me");
        waitCreateRoot();
    }

    @FXML
    void createRoomMeAI(ActionEvent event) {
        ls.getBw().println("createRoom Me AI");
        waitCreateRoot();
    }

    @FXML
    void createRoomAIMe(ActionEvent event) {
        ls.getBw().println("createRoom AI Me");
        waitCreateRoot();
    }

    @FXML
    void createRoomMeOther(ActionEvent event) {
        ls.getBw().println("createRoom ME Other");
        waitCreateRoot();
    }

    @FXML
    void joinRoom(ActionEvent event) {
        ls.getBw().println("createRoom Other Me");
        waitCreateRoot();
    }

    private void waitCreateRoot(){
        Dialog dialog = new Dialog();
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());
        closeButton.setVisible(false);
        dialog.setTitle("创建房间中...");
        int[] id = new int[1];
        ls.addListen(new ConnectListen("roomId") {
            @Override
            public void run(String[] packet) {
                id[0] = Integer.parseInt(packet[1]);
                dialog.getDialogPane().setContentText("创建成功！房间号："+id[0]);
                ButtonType buttonTypeCancel = new ButtonType("确认", ButtonBar.ButtonData.CANCEL_CLOSE);
                dialog.getDialogPane().getButtonTypes().addAll(buttonTypeCancel);
                dialog.setHeight(dialog.getHeight()+50);
            }
        });

        dialog.getDialogPane().setPrefWidth(300);
        dialog.getDialogPane().setContentText("正在创建房间......");

        dialog.showAndWait();

        try {
            new Game(id[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
