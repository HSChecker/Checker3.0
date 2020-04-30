package gui.start;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Start��
 *
 * @author HanShuo
 * @Date 2020/4/22 22:38
 */
public class Start {

    public Start() throws IOException {
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("start.fxml"));
        primaryStage.setTitle("HS�������� ��������Ϸ");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

}
