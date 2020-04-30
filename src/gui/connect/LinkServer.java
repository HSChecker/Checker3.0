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
 * LinkServer��
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
                    dialog.getDialogPane().setContentText("����ʧ�ܣ������˳�");
                });
                sleep(1000);
                Platform.runLater(()->{
                    ButtonType buttonTypeCancel = new ButtonType("ȷ��", ButtonBar.ButtonData.CANCEL_CLOSE);
                    dialog.getDialogPane().getButtonTypes().addAll(buttonTypeCancel);
                    dialog.setHeight(dialog.getHeight()+50);
                });
                return;
            }
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bw = new PrintWriter(socket.getOutputStream());
            Platform.runLater(()->{
                dialog.getDialogPane().setContentText("���ӳɹ����ȴ�����ȷ��......");
            });
            String[] sure = NET.turnPacketData(br.readLine());
            if(sure[0].equals("sure")){
                Platform.runLater(()->{
                    dialog.getDialogPane().setContentText("��ȷ����ݣ����ӳɹ������������¼����");
                });
                bw.println("sure "+PARAMETER.version);
                bw.flush();
                linkSuccect = true;
                sleep(1000);
                Platform.runLater(()->{
                    ButtonType buttonTypeCancel = new ButtonType("ȷ��", ButtonBar.ButtonData.CANCEL_CLOSE);
                    dialog.getDialogPane().getButtonTypes().addAll(buttonTypeCancel);
                    dialog.setHeight(dialog.getHeight()+50);
                });
            }else{
                dialog.getDialogPane().setContentText("ȷ��ʧ�ܣ������˳�����");
                sleep(1000);
                Platform.runLater(()->{
                    ButtonType buttonTypeCancel = new ButtonType("ȷ��", ButtonBar.ButtonData.CANCEL_CLOSE);
                    dialog.getDialogPane().getButtonTypes().addAll(buttonTypeCancel);
                    dialog.setHeight(dialog.getHeight()+50);
                });
                return;
            }
            //���ڹرյȴ�
            synchronized (LOCK.add("waitDialogClose")){
                try{
                    LOCK.get("waitDialogClose").wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            String[] loginResult = null;
            Platform.runLater(()-> {
                //��½ע���û���
                Dialog<Pair<String, String>> login = new Dialog<>();
                login.setTitle("��¼/ע�� ����");

                // ����ͷ��ͼƬ
                ImageView iv = new ImageView("file:/" + PATH + "/image/ico/login.png");
                iv.setFitHeight(100);
                iv.setFitWidth(100);
                login.setGraphic(iv);

                ButtonType loginButtonType = new ButtonType("��¼", ButtonBar.ButtonData.OK_DONE);
                ButtonType registerButtonType = new ButtonType("ע��");
                login.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
                login.getDialogPane().getButtonTypes().addAll(registerButtonType);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                TextField name = new TextField();
                name.setPromptText("�û���");
                PasswordField password = new PasswordField();
                password.setPromptText("����");

                grid.add(new Label("�û���:"), 0, 0);
                grid.add(name, 1, 0);
                grid.add(new Label("����:"), 0, 1);
                grid.add(password, 1, 1);

                Node loginButton = login.getDialogPane().lookupButton(loginButtonType);
                Node regButton = login.getDialogPane().lookupButton(registerButtonType);
                loginButton.setDisable(true);
                regButton.setDisable(true);

                // ��Ӵ���� Java 8 lambda ���ʽ����У��
                name.textProperty().addListener((observable, oldValue, newValue) -> {
                    loginButton.setDisable(newValue.trim().isEmpty() || password.getText().trim().isEmpty());
                    regButton.setDisable(newValue.trim().isEmpty() || password.getText().trim().isEmpty());
                });

                password.textProperty().addListener((observable, oldValue, newValue) -> {
                    loginButton.setDisable(newValue.trim().isEmpty() || name.getText().trim().isEmpty());
                    regButton.setDisable(newValue.trim().isEmpty() || name.getText().trim().isEmpty());
                });

                login.getDialogPane().setContent(grid);

                // Ĭ�Ϲ�����û�����
                Platform.runLater(() -> name.requestFocus());

                // ��¼��ť�󣬽����תΪusername-password-pair
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
                            alert.setTitle("��¼ʧ��");
                            alert.setContentText("���˺Ž���Ϊ����");
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
                    alert.setTitle("��¼ʧ��");
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
