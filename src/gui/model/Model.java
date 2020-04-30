package gui.model;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Model��
 *
 * @author HanShuo
 * @Date 2020/4/25 2:37
 */
public class Model {

    public Model(){
        Platform.runLater(()-> {
            Stage primaryStage = new Stage();
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("model.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            primaryStage.setTitle("HS�������� ��ѡ��ģʽ");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        });
    }

}
