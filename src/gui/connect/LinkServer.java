package gui.connect;


import gui.model.Model;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;
import tools.LOCK;
import tools.NET;
import tools.PARAMETER;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Optional;

import static java.lang.Thread.sleep;
import static tools.PARAMETER.PATH;
import static tools.PARAMETER.ls;

/**
 * LinkServer类
 *
 * @author HanShuo
 * @Date 2020/4/24 18:43
 */
public class LinkServer implements Runnable{

    private Dialog dialog;
    private ServerData sd;
    private boolean linkSuccect = false;

    private BufferedReader br;
    private PrintWriter bw;

    private SplitPane pane;

    public LinkServer(Dialog dialog, ServerData sd, SplitPane pane) {
        this.dialog = dialog;
        this.sd = sd;
        this.pane = pane;
        new Thread(this).start();
    }

    @Override
    public void run() {
        Socket socket = null;
        try {
            try {
                socket = new Socket(sd.getAdress(),Integer.parseInt(sd.getProt()));
            } catch(ConnectException e){
                Platform.runLater(()-> {
                    dialog.getDialogPane().setContentText("连接失败，即将退出");
                });
                sleep(1000);
                Platform.runLater(()->{
                    ButtonType buttonTypeCancel = new ButtonType("确认", ButtonBar.ButtonData.CANCEL_CLOSE);
                    dialog.getDialogPane().getButtonTypes().addAll(buttonTypeCancel);
                    dialog.setHeight(dialog.getHeight()+50);
                });
                return;
            }
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bw = new PrintWriter(socket.getOutputStream());
            Platform.runLater(()->{
                dialog.getDialogPane().setContentText("链接成功，等待返回确认......");
            });
            String[] sure = NET.turnPacketData(br.readLine());
            if(sure[0].equals("sure")){
                Platform.runLater(()->{
                    dialog.getDialogPane().setContentText("已确认身份，链接成功，即将进入登录大厅");
                });
                bw.println("sure "+PARAMETER.version);
                bw.flush();
                linkSuccect = true;
                sleep(1000);
                Platform.runLater(()->{
                    ButtonType buttonTypeCancel = new ButtonType("确认", ButtonBar.ButtonData.CANCEL_CLOSE);
                    dialog.getDialogPane().getButtonTypes().addAll(buttonTypeCancel);
                    dialog.setHeight(dialog.getHeight()+50);
                });
            }else{
                dialog.getDialogPane().setContentText("确认失败，即将退出链接");
                sleep(1000);
                Platform.runLater(()->{
                    ButtonType buttonTypeCancel = new ButtonType("确认", ButtonBar.ButtonData.CANCEL_CLOSE);
                    dialog.getDialogPane().getButtonTypes().addAll(buttonTypeCancel);
                    dialog.setHeight(dialog.getHeight()+50);
                });
                return;
            }
            //窗口关闭等待
            synchronized (LOCK.add("waitDialogClose")){
                try{
                    LOCK.get("waitDialogClose").wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            String[] loginResult = null;
            Platform.runLater(()-> {
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
                        return new Pair<>("login " + name.getText(), password.getText());
                    }

                    if (dialogButton == registerButtonType) {
                        return new Pair<>("register " + name.getText(), password.getText());
                    }
                    return null;
                });

                Optional<Pair<String, String>> result = login.showAndWait();

                result.ifPresent(usernamePassword -> {
                    if(!Controller.isNumeric(NET.turnPacketData(usernamePassword.getKey())[1])){
                        Platform.runLater(()-> {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("登录失败");
                            alert.setContentText("在账号仅能为数字");
                            alert.showAndWait();
                            return;
                        });
                    }else {
                        ls.getBw().println(usernamePassword.getKey() + " " + usernamePassword.getValue());
                        ls.getBw().flush();
                    }
                });

            });

            try {
                loginResult = NET.turnPacketData(br.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(loginResult[0].equals("loginResult") && loginResult[1].equals("true")){
                Platform.runLater(()-> {
                    Stage stage = (Stage) pane.getScene().getWindow();
                    stage.close();
                    new Model();
                });
            }else{
                String[] finalLoginResult = loginResult;
                Platform.runLater(()-> {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("登录失败");
                    alert.setContentText(finalLoginResult[2]);
                    alert.showAndWait();
                });
                return;
            }


            while(true){
                String[] packet = NET.turnPacketData(br.readLine());
                if(packet[0].equals("toRoomID")){
                    LOCK.get("room").setInform(packet[1]);
                    LOCK.get("room").notifyAll();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public boolean isLinkSuccect() {
        return linkSuccect;
    }

    public BufferedReader getBr() {
        return br;
    }

    public PrintWriter getBw() {
        return bw;
    }
}
