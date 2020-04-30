package gui.connect;

import gui.model.Model;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import tools.FILE;
import tools.LOCK;
import tools.PARAMETER;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.*;
import java.util.Optional;

import static tools.PARAMETER.PATH;
import static tools.PARAMETER.ls;

/**
 * ConnectController类
 *
 * @author HanShuo
 * @Date 2020/4/23 14:27
 */
public class Controller {

    private ObservableList dataList;

    @FXML
    private SplitPane pane;

    @FXML
    private TableView<?> Table;

    @FXML
    private TableColumn<?, ?> prot;

    @FXML
    private TableColumn<?, ?> inform;

    @FXML
    private TableColumn<?, ?> name;

    @FXML
    private TableColumn<?, ?> adress;

    @FXML
    private TableColumn<?, ?> id;

    @FXML
    private TableColumn<?, ?> state;

    @FXML
    private Button add;

    @FXML
    private Button refresh;

    @FXML
    private Button enter;

    @FXML
    private Button remove;

    @FXML
    void addAction(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("警告");
        alert.setHeaderText("输入警告：");
        alert.setContentText("输入有误！请检查输入");
        String name,ip,prot,id;
        for(int i=1;;i++) {
            boolean b = true;
            for (int j = 0; j < dataList.size(); j++) {
                ServerData a = (ServerData) dataList.get(j);
                if (i == Integer.parseInt(a.getId())){
                    b = false;
                    break;
                }
            }
            if(b == true){
                id = String.valueOf(i);
                break;
            }
        }
        while (true){
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("添加链接");
            dialog.setHeaderText("输入链接昵称");
            dialog.setContentText("创建步骤（1/3）");

            Optional result = dialog.showAndWait();
            if (result.isPresent()) {
                name = (String) result.get();
                if(name.equals("")||name.equals(""))
                    alert.showAndWait();
                else
                    break;
            } else {
                return;
            }
        }
        while (true){
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("添加链接");
            dialog.setHeaderText("输入目标IP地址");
            dialog.setContentText("创建步骤（2/3）");

            Optional result = dialog.showAndWait();
            if (result.isPresent()) {
                ip = (String) result.get();
                if(ip.equals("")||ip.equals(""))
                    alert.showAndWait();
                else
                    break;
            } else {
                return;
            }
        }
        while (true){
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("添加链接");
            dialog.setHeaderText("输入目标端口");
            dialog.setContentText("创建步骤（3/3）");

            Optional result = dialog.showAndWait();
            if (result.isPresent()) {
                prot = (String) result.get();
                if(prot.equals("")||prot.equals("")||!isNumeric(prot))
                    alert.showAndWait();
                else
                    break;
            } else {
                return;
            }
        }
        dataList.add(new ServerData(id,name,ip,prot,Table));

        BufferedWriter bw = new BufferedWriter(new FileWriter(PATH + "/data/connect.txt"));
        for(int i=0;i<dataList.size();i++){
            ServerData s = (ServerData) dataList.get(i);
            bw.write(s.getId()+" "+s.getName()+" "+s.getAdress()+" "+s.getProt());
            bw.newLine();
        }
        bw.flush();
        bw.close();
    }

    @FXML
    void removeAction(ActionEvent event) throws IOException {
        ServerData sheet = (ServerData) Table.getSelectionModel().getSelectedItem();
        dataList.remove(sheet);
        BufferedWriter bw = new BufferedWriter(new FileWriter(PATH + "/data/connect.txt"));
        for(int i=0;i<dataList.size();i++){
            ServerData s = (ServerData) dataList.get(i);
            bw.write(s.getId()+" "+s.getName()+" "+s.getAdress()+" "+s.getProt());
            bw.newLine();
        }
        bw.flush();
        bw.close();
    }

    @FXML
    void refreshAction(ActionEvent event) throws IOException {
        FILE.createFile(PATH + "/data/connect.txt");
        BufferedReader br = new BufferedReader(new FileReader(PATH + "/data/connect.txt"));

        dataList = FXCollections.observableArrayList();
        Table.setItems(dataList);

        String s;
        while((s=br.readLine())!=null){
            String[] a = s.split(" ");
            dataList.add(new ServerData(a[0],a[1],a[2],a[3],Table));
        }
        br.close();
    }

    @FXML
    void enterAction(ActionEvent event) throws Exception {
        Dialog dialog = new Dialog();
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());
        closeButton.setVisible(false);
        dialog.setTitle("连接中...");

        dialog.getDialogPane().setPrefWidth(300);
        dialog.getDialogPane().setContentText("正在连接到服务器......(请勿关闭，关闭即取消)");

        PARAMETER.ls = new LinkServer(dialog,(ServerData)Table.getSelectionModel().getSelectedItem(),pane);
        dialog.showAndWait();
        if(PARAMETER.ls.isLinkSuccect()){
            synchronized (LOCK.get("waitDialogClose")) {
                LOCK.get("waitDialogClose").notifyAll();
            }
            LOCK.remove("waitDialogClose");
        }
    }

    @FXML
    private void initialize() throws IOException {
        FILE.createFile(PATH + "/data/connect.txt");
        BufferedReader br = new BufferedReader(new FileReader(PATH + "/data/connect.txt"));

        dataList = FXCollections.observableArrayList();

        String s;
        while((s=br.readLine())!=null){
            String[] a = s.split(" ");
            dataList.add(new ServerData(a[0],a[1],a[2],a[3],Table));
        }
        br.close();

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        adress.setCellValueFactory(new PropertyValueFactory<>("adress"));
        state.setCellValueFactory(new PropertyValueFactory<>("state"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        inform.setCellValueFactory(new PropertyValueFactory<>("inform"));
        prot.setCellValueFactory(new PropertyValueFactory<>("prot"));

        Table.setItems(dataList);
    }

    public static boolean isNumeric(String str){
        for(int i=str.length();--i>=0;){
            int chr=str.charAt(i);
            if(chr<48 || chr>57)
                return false;
        }
        return true;
    }

}
