package tools;

import java.io.File;
import java.io.IOException;

/**
 * FILE¿‡
 *
 * @author HanShuo
 * @Date 2020/4/23 23:07
 */
public class FILE {

    public static void createFile(String path) throws IOException {
        String strPath=path;
        File file = new File(strPath);
        File fileParent = file.getParentFile();
        if(!fileParent.exists()){
            fileParent.mkdirs();
        }
        file.createNewFile();
    }

}
