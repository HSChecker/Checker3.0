package gui.connect;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableView;

import java.io.*;

/**
 * ServerData类
 *
 * @author HanShuo
 * @Date 2020/4/23 18:09
 */
public class ServerData implements Serializable {

    public SimpleStringProperty id = new SimpleStringProperty();
    public SimpleStringProperty name = new SimpleStringProperty();
    public SimpleStringProperty adress = new SimpleStringProperty();
    public SimpleStringProperty prot = new SimpleStringProperty();
    public SimpleStringProperty state = new SimpleStringProperty();
    public SimpleStringProperty inform = new SimpleStringProperty();

    public ServerData(String id, String name, String adress, String prot, TableView Table) throws IOException {
        this.id.set(id);
        this.name.set(name);
        this.adress.set(adress);
        this.prot.set(prot);
        this.state.set( "连接中......" );
        this.inform.set("");
        new LinkTest(adress,Integer.parseInt(prot),state,inform,Table);
    }


    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getAdress() {
        return adress.get();
    }

    public void setAdress(String adress) {
        this.adress.set(adress);
    }

    public String getProt() {
        return prot.get();
    }

    public void setProt(String prot) {
        this.prot.set(prot);
    }

    public String getState() {
        return state.get();
    }

    public void setState(String state) {
        this.state.set(state);
    }

    public String getInform() {
        return inform.get();
    }

    public void setInform(String inform) {
        this.inform.set(inform);
    }
}
