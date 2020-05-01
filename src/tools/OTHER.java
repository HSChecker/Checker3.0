package tools;

/**
 * OTHERÀà
 *
 * @author HanShuo
 * @Date 2020/5/1 20:15
 */
public class OTHER {

    public static boolean isNumeric(String str){
        for(int i=str.length();--i>=0;){
            int chr=str.charAt(i);
            if(chr<48 || chr>57)
                return false;
        }
        return true;
    }

}
