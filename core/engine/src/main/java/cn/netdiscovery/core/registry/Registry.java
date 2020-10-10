package cn.netdiscovery.core.registry;

/**
 * Created by tony on 2019-06-08.
 */
public abstract class Registry {

    protected Provider provider;

    public Provider getProvider() {
        return provider;
    }

    public abstract void register(Provider provider, int port);
}
