package gui.game;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller¿‡
 *
 * @author HanShuo
 * @Date 2020/4/26 16:13
 */
public class Controller {

    @FXML
    void Click(ActionEvent event) {
        Button btn = (Button)event.getSource();
        System.out.println(Integer.parseInt(btn.getId()));
    }

}
