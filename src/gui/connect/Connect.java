package gui.connect;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Game类
 *
 * @author HanShuo
 * @Date 2020/4/23 14:26
 */
public class Connect {

    public Connect() throws Exception{
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("connect.fxml"));
        primaryStage.setTitle("HS国际跳棋 —链接服务器");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }


}
