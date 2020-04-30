package tools;

import gui.connect.LinkServer;

/**
 * PARAMETER¿‡
 *
 * @author HanShuo
 * @Date 2020/4/23 13:51
 */
public class PARAMETER {

    private static PARAMETER This = new PARAMETER();

    private PARAMETER() {}

    public static String path_te = This.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();

    public static String PATH = path_te.substring(1,path_te.length()-(path_te.substring(path_te.length()-4,path_te.length()).equals(".jar")?16:10));

    public static LinkServer ls;

    public static String version = "BETE4.0.0C";
}
