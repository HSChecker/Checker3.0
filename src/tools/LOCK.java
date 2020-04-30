package tools;

import java.util.ArrayList;
import java.util.List;

/**
 * LOCK¿‡
 *
 * @author HanShuo
 * @Date 2020/4/25 23:00
 */
public class LOCK {

    private static List<Str> lock = new ArrayList<Str>();

    public synchronized static Str add(String s){
        Str a = new Str(s);
        lock.add(a);
        return a;
    }

    public synchronized static void remove(String s){
        for(Str a:lock){
            if(a.getLockName().equals(s)){
                lock.remove(a);
                return;
            }
        }
    }

    public static Str get(String s){
        for(Str a:lock){
            if(a.getLockName().equals(s)){
                return a;
            }
        }
        return add(s);
    }

    public static class Str{
        private String lockName;
        private String inform = "";

        public Str(String s){
            lockName = s;
        }

        public String getInform() {
            return inform;
        }

        public void setInform(String inform) {
            this.inform = inform;
        }

        public String getLockName() {
            return lockName;
        }
    }
}
