package gui.start;

import gui.connect.Connect;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tools.PARAMETER;

/**
 * Controller¿‡
 *
 * @author HanShuo
 * @Date 2020/4/22 22:38
 */
public class Controller {

    @FXML
    private Pane rootLayout;

    @FXML
    private Button enter;

    @FXML
    void enter(ActionEvent event) throws Exception {
        Stage stage = (Stage) rootLayout.getScene().getWindow();
        stage.close();
        new Connect();
    }

}
