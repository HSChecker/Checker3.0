import gui.connect.LinkServer;
import gui.start.Start;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * RUN¿‡
 *
 * @author HanShuo
 * @Date 2020/4/22 22:38
 */
public class RUN extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        new Start();
    }

    public static void main(String[] ar) {

        launch(new String[]{});

    }

}
