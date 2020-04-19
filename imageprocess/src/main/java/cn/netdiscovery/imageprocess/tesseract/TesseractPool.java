package cn.netdiscovery.imageprocess.tesseract;

import cn.netdiscovery.core.exception.SpiderException;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * @FileName: cn.netdiscovery.imageprocess.tesseract.TesseractPool
 * @author: Tony Shen
 * @date: 2020-04-19 13:12
 * @version: V1.0 <描述当前版本功能>
 */
public class TesseractPool {

    private static GenericObjectPool<TesseractClient> tesseractPool = null;

    static {
        tesseractPool = new GenericObjectPool<>(new TesseractPooledFactory());
        tesseractPool.setMaxTotal(Integer.parseInt(System.getProperty(
                "tesseract.pool.max.total", "20"))); // 最多能放多少个对象
        tesseractPool.setMinIdle(Integer.parseInt(System.getProperty(
                "tesseract.pool.min.idle", "1")));   // 最少有几个闲置对象
        tesseractPool.setMaxIdle(Integer.parseInt(System.getProperty(
                "tesseract.pool.max.idle", "20"))); // 最多允许多少个闲置对象

        try {
            tesseractPool.preparePool();
        } catch (Exception e) {
            throw new SpiderException(e);
        }
    }

    public static TesseractClient borrowOne() {

        if (tesseractPool!=null) {

            try {
                return tesseractPool.borrowObject();
            } catch (Exception e) {
                throw new SpiderException(e);
            }
        }

        return null;
    }

    public static void returnOne(TesseractClient tesseractClient) {

        if (tesseractPool!=null) {

            tesseractPool.returnObject(tesseractClient);
        }
    }

    public static void destory() {

        if (tesseractPool!=null) {

            tesseractPool.clear();
            tesseractPool.close();
        }
    }

    public static boolean hasTesseractPool() {

        return tesseractPool!=null;
    }
}
