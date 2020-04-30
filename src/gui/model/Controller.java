package gui.model;

import gui.connect.Connect;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import tools.LOCK;
import tools.PARAMETER;

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
    void createRoomOtherMe(ActionEvent event) throws InterruptedException {
        PARAMETER.ls.getBw().println("createRoom Other Me");
        int id = waitBack();
        //新建房间！
    }

    @FXML
    void createRoomMeAI(ActionEvent event) {

    }

    @FXML
    void createRoomAIMe(ActionEvent event) {

    }

    @FXML
    void createRoomMeOther(ActionEvent event) {

    }

    @FXML
    void joinRoom(ActionEvent event) {

    }

    private int waitBack() throws InterruptedException {
        synchronized (LOCK.add("room")){
            LOCK.get("room").wait();
        }
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.close();
        return Integer.parseInt(LOCK.get("room").getInform());
    }

}
