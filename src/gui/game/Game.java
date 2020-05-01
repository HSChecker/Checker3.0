package gui.game;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * GameÀà
 *
 * @author HanShuo
 * @Date 2020/4/26 16:11
 */
public class Game {

    public Game(int id) throws IOException {
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("game.fxml"));
        primaryStage.setTitle("HS¹ú¼ÊÌøÆå ¡ª"+id);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

}
