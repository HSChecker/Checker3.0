package gui.connect;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableView;

import java.io.*;
import java.net.*;

/**
 * LinkTest��
 *
 * @author HanShuo
 * @Date 2020/4/23 21:01
 */
public class LinkTest implements Runnable{

    private String ip;
    private int prot;

    private SimpleStringProperty state;
    private SimpleStringProperty inform;
    private TableView Table;

    public LinkTest(String ip, int prot, SimpleStringProperty state, SimpleStringProperty inform, TableView Table){
        this.ip = ip;
        this.prot = prot;
        this.state = state;
        this.inform = inform;
        this.Table = Table;
        new Thread(this).start();
    }

    @Override
    public void run() {
        if(isHostConnectable(ip,prot))
            state.set("����");
        else
            state.set("��ʱ");
        inform.set(ipSite(ip));
        Table.refresh();
    }

    private static String ipSite(String ip) {
        //����get����
        String str = getHtmlContent("www.ip138.com/iplookup.asp?ip="+ip+"&action=2","GBK");
        //����ȥ������html��ʽ�ַ������пհ��ַ�
        //str = str.replaceAll("\\s","");
        //ͨ��ƥ��ؼ��ֽ��в��
        String[] str1 = str.split("ASN�����أ�");
        String[] str2 = str1[1].split("</font></li><li>");
        //���ؽ��
        return str2[0];
    }

    private static boolean isHostConnectable(String host, int port) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port));
        } catch (IOException e) {
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private static String getHtmlContent(URL url, String encode) {
        StringBuffer contentBuffer = new StringBuffer();

        int responseCode = -1;
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");// IE�����������
            con.setConnectTimeout(60000);
            con.setReadTimeout(60000);
            // �����ҳ������Ϣ��
            responseCode = con.getResponseCode();
            if (responseCode == -1) {
                System.out.println(url.toString() + " : connection is failure...");
                con.disconnect();
                return null;
            }
            if (responseCode >= 400) // ����ʧ��
            {
                System.out.println("����ʧ��:get response code: " + responseCode);
                con.disconnect();
                return null;
            }

            InputStream inStr = con.getInputStream();
            InputStreamReader istreamReader = new InputStreamReader(inStr, encode);
            BufferedReader buffStr = new BufferedReader(istreamReader);

            String str = null;
            while ((str = buffStr.readLine()) != null)
                contentBuffer.append(str);
            inStr.close();
        } catch (IOException e) {
            e.printStackTrace();
            contentBuffer = null;
            System.out.println("error: " + url.toString());
        } finally {
            con.disconnect();
        }
        return contentBuffer.toString();
    }

    private static String getHtmlContent(String url, String encode) {
        if (!url.toLowerCase().startsWith("https://")) {
            url = "https://" + url;
        }
        try {
            URL rUrl = new URL(url);
            return getHtmlContent(rUrl, encode);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
