package cn.netdiscovery.core.registry;

/**
 * Created by tony on 2019-06-08.
 */
public interface Registry {

    void register(String connectString,String path,int port) throws Exception;
}
