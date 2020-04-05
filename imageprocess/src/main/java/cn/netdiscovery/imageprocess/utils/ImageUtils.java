package cn.netdiscovery.imageprocess.utils;

import java.io.*;

/**
 * @FileName: cn.netdiscovery.imageprocess.utils.ImageUtils
 * @author: Tony Shen
 * @date: 2020-02-22 23:49
 * @version: V1.0 <描述当前版本功能>
 */
public class ImageUtils {

    public static byte[] file2Byte (File file) {
        byte[] buffer = null;
        FileInputStream fis;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            fis = new FileInputStream(file);
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
            if(file.exists()) {
                file.delete();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }
}
