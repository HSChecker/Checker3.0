package gui.connect;

import Link.ConnectIntyerface;
import Link.ConnectListen;
import Link.LinkServer;
import gui.model.Model;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import tools.FILE;
import tools.LOCK;
import tools.NET;
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

import static tools.OTHER.isNumeric;
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

        ls = new LinkServer((ServerData)Table.getSelectionModel().getSelectedItem());
        synchronized (LOCK.add("lsRUN")){
            LOCK.get("lsRUN").wait();
        }
        LOCK.remove("lsRUN");

        ls.addListen(new ConnectListen("connect"){
            @Override
            public void run(String[] packet) {
                if(packet[1].equals("true")){
                    dialog.getDialogPane().setContentText("已确认身份，链接成功，即将进入登录大厅");
                    ButtonType buttonTypeCancel = new ButtonType("确认", ButtonBar.ButtonData.CANCEL_CLOSE);
                    dialog.getDialogPane().getButtonTypes().addAll(buttonTypeCancel);
                    dialog.setHeight(dialog.getHeight()+50);
                } else {
                    dialog.getDialogPane().setContentText("连接失败，请重新连接");
                    ButtonType buttonTypeCancel = new ButtonType("确认", ButtonBar.ButtonData.CANCEL_CLOSE);
                    dialog.getDialogPane().getButtonTypes().addAll(buttonTypeCancel);
                    dialog.setHeight(dialog.getHeight()+50);
                    ls.closeLink();
                }
            }
        });

        ls.getBw().println("requseConnect");
        ls.getBw().flush();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    if(!ls.isLinkSuccect()){
                        ls.closeLink();
                        Platform.runLater(()-> {
                            dialog.close();
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("链接失败");
                            alert.setContentText("目标服务器连接超时！");
                            alert.showAndWait();
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        dialog.showAndWait();

        if(!dialog.getDialogPane().getContentText().equals("已确认身份，链接成功，即将进入登录大厅"))
            return;

        //登陆注册用户框
        Dialog<Pair<String, String>> login = new Dialog<>();
        login.setTitle("登录/注册 界面");

        // 设置头部图片
        ImageView iv = new ImageView("file:/" + PATH + "/image/ico/login.png");
        iv.setFitHeight(100);
        iv.setFitWidth(100);
        login.setGraphic(iv);

        ButtonType loginButtonType = new ButtonType("登录", ButtonBar.ButtonData.OK_DONE);
        ButtonType registerButtonType = new ButtonType("注册");
        login.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        login.getDialogPane().getButtonTypes().addAll(registerButtonType);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField name = new TextField();
        name.setPromptText("用户名");
        PasswordField password = new PasswordField();
        password.setPromptText("密码");

        grid.add(new Label("用户名:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label("密码:"), 0, 1);
        grid.add(password, 1, 1);

        Node loginButton = login.getDialogPane().lookupButton(loginButtonType);
        Node regButton = login.getDialogPane().lookupButton(registerButtonType);
        loginButton.setDisable(true);
        regButton.setDisable(true);

        // 是哟功能 Java 8 lambda 表达式进行校验
        name.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty() || password.getText().trim().isEmpty());
            regButton.setDisable(newValue.trim().isEmpty() || password.getText().trim().isEmpty());
        });

        password.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty() || name.getText().trim().isEmpty());
            regButton.setDisable(newValue.trim().isEmpty() || name.getText().trim().isEmpty());
        });

        login.getDialogPane().setContent(grid);

        // 默认光标在用户名上
        Platform.runLater(() -> name.requestFocus());

        // 登录按钮后，将结果转为username-password-pair
        login.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>("login login " + name.getText(), password.getText());
            }

            if (dialogButton == registerButtonType) {
                return new Pair<>("login register " + name.getText(), password.getText());
            }
            return null;
        });

        ls.addListen(new ConnectListen("loginResult") {
            @Override
            public void run(String[] packet) {
                if(packet[1].equals("true")) {
                    Platform.runLater(()-> {
                        Stage stage = (Stage) pane.getScene().getWindow();
                        stage.close();
                    });
                    new Model();
                }else{
                    Platform.runLater(()-> {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("登录失败");
                        alert.setContentText(packet[2]);
                        alert.showAndWait();
                    });
                    ls.closeLink();
                }
            }
        });

        Optional<Pair<String, String>> result = login.showAndWait();

        result.ifPresent(usernamePassword -> {
            if(!isNumeric(NET.turnPacketData(usernamePassword.getKey())[2])){
                Platform.runLater(()-> {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("登录失败");
                    alert.setContentText("在账号仅能为数字");
                    alert.showAndWait();
                    ls.closeLink();
                    return;
                });
            }else {
                ls.getBw().println(usernamePassword.getKey() + " " + usernamePassword.getValue());
                ls.getBw().flush();
            }
        });
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

}
