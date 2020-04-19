package cn.netdiscovery.imageprocess.tesseract;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * @FileName: cn.netdiscovery.imageprocess.tesseract.TesseractPooledFactory
 * @author: Tony Shen
 * @date: 2020-04-19 13:30
 * @version: V1.0 <描述当前版本功能>
 */
public class TesseractPooledFactory implements PooledObjectFactory<TesseractClient> {


    @Override
    public PooledObject<TesseractClient> makeObject() throws Exception {

        return new DefaultPooledObject<>(new TesseractClient());
    }

    @Override
    public void destroyObject(PooledObject<TesseractClient> p) throws Exception {
        p.getObject().deallocate();
    }

    @Override
    public boolean validateObject(PooledObject<TesseractClient> p) {
        return null != p.getObject();
    }

    @Override
    public void activateObject(PooledObject<TesseractClient> p) throws Exception {

    }

    @Override
    public void passivateObject(PooledObject<TesseractClient> p) throws Exception {
        TesseractClient tesseractClient = p.getObject();
        tesseractClient.release();
    }
}
