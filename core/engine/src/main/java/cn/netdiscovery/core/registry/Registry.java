package cn.netdiscovery.core.registry;

import lombok.Getter;

/**
 * Created by tony on 2019-06-08.
 */
@Getter
public abstract class Registry {

    protected Provider provider;

    public abstract void register(String connectString,String path,int port);
}
