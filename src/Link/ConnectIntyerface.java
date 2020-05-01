package Link;

/**
 * ConnectIntyerface½Ó¿Ú
 *
 * @author HanShuo
 * @Date 2020/5/1 17:16
 */
public interface ConnectIntyerface{

    boolean whetherRun(String connectName);
    void run(String[] packet);
    boolean isPermanent();

}
