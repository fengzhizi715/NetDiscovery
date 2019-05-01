package cn.netdiscovery.downloader.selenium;

import org.openqa.selenium.Proxy;

/**
 * Created by tony on 2018/6/18.
 */
public class ProxyUtils {

    public static Proxy toSeleniumProxy(com.cv4j.proxy.domain.Proxy proxy) {

        return new Proxy().setHttpProxy(proxy.getId()+":"+proxy.getPort());
    }
}
