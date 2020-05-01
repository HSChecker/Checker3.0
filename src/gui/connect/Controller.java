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
 * ConnectController��
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
        alert.setTitle("����");
        alert.setHeaderText("���뾯�棺");
        alert.setContentText("����������������");
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
            dialog.setTitle("�������");
            dialog.setHeaderText("���������ǳ�");
            dialog.setContentText("�������裨1/3��");

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
            dialog.setTitle("�������");
            dialog.setHeaderText("����Ŀ��IP��ַ");
            dialog.setContentText("�������裨2/3��");

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
            dialog.setTitle("�������");
            dialog.setHeaderText("����Ŀ��˿�");
            dialog.setContentText("�������裨3/3��");

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
        dialog.setTitle("������...");

        dialog.getDialogPane().setPrefWidth(300);
        dialog.getDialogPane().setContentText("�������ӵ�������......(����رգ��رռ�ȡ��)");

        ls = new LinkServer((ServerData)Table.getSelectionModel().getSelectedItem());
        synchronized (LOCK.add("lsRUN")){
            LOCK.get("lsRUN").wait();
        }
        LOCK.remove("lsRUN");

        ls.addListen(new ConnectListen("connect"){
            @Override
            public void run(String[] packet) {
                if(packet[1].equals("true")){
                    dialog.getDialogPane().setContentText("��ȷ����ݣ����ӳɹ������������¼����");
                    ButtonType buttonTypeCancel = new ButtonType("ȷ��", ButtonBar.ButtonData.CANCEL_CLOSE);
                    dialog.getDialogPane().getButtonTypes().addAll(buttonTypeCancel);
                    dialog.setHeight(dialog.getHeight()+50);
                } else {
                    dialog.getDialogPane().setContentText("����ʧ�ܣ�����������");
                    ButtonType buttonTypeCancel = new ButtonType("ȷ��", ButtonBar.ButtonData.CANCEL_CLOSE);
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
                            alert.setTitle("����ʧ��");
                            alert.setContentText("Ŀ����������ӳ�ʱ��");
                            alert.showAndWait();
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        dialog.showAndWait();

        if(!dialog.getDialogPane().getContentText().equals("��ȷ����ݣ����ӳɹ������������¼����"))
            return;

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
                        alert.setTitle("��¼ʧ��");
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
                    alert.setTitle("��¼ʧ��");
                    alert.setContentText("���˺Ž���Ϊ����");
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
