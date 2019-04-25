package com.cv4j.netdiscovery.selenium.pool;

import com.cv4j.netdiscovery.selenium.Browser;
import com.cv4j.proxy.ProxyPool;
import com.cv4j.proxy.domain.Proxy;
import com.cv4j.proxy.http.HttpManager;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.openqa.selenium.WebDriver;

import java.util.Set;

/**
 * Created by tony on 2018/3/9.
 */
public final class WebDriverPooledFactory implements PooledObjectFactory<WebDriver> {

    private String path;
    private Browser browser;

    public WebDriverPooledFactory(WebDriverPoolConfig config) {

        this.path = config.getPath();
        this.browser = config.getBrowser();
        ProxyPool.addProxyList(config.getProxies());
    }

    /**
     * 创建一个对象
     * @return
     * @throws Exception
     */
    @Override
    public PooledObject<WebDriver> makeObject() throws Exception {

        Proxy proxy =  ProxyPool.getProxy();

        if (proxy!=null && HttpManager.get().checkProxy(proxy)) {

            return new DefaultPooledObject<>(WebDriverFactory.getWebDriver(path,browser,proxy));
        } else { // 重试第一次获取Proxy

            proxy = ProxyPool.getProxy();

            if (proxy!=null && HttpManager.get().checkProxy(proxy)) {

                return new DefaultPooledObject<>(WebDriverFactory.getWebDriver(path,browser,proxy));
            } else { // 重试第二次获取Proxy

                proxy = ProxyPool.getProxy();

                if (proxy!=null && HttpManager.get().checkProxy(proxy)) {

                    return new DefaultPooledObject<>(WebDriverFactory.getWebDriver(path, browser, proxy));
                }
            }
        }

        return new DefaultPooledObject<>(WebDriverFactory.getWebDriver(path,browser,null)); // 不使用Proxy
    }

    /**
     * 销毁一个对象
     * @param p
     * @throws Exception
     */
    @Override
    public void destroyObject(PooledObject<WebDriver> p) throws Exception {
        p.getObject().quit();
    }

    /**
     * 对象是否有效
     * @param p
     * @return
     */
    @Override
    public boolean validateObject(PooledObject<WebDriver> p) {
        return null != p.getObject();
    }

    /**
     * 激活一个对象
     * @param p
     * @throws Exception
     */
    @Override
    public void activateObject(PooledObject<WebDriver> p) throws Exception {
    }

    /**
     * 钝化一个对象，在归还前做一起清理动作。
     * @param p
     * @throws Exception
     */
    @Override
    public void passivateObject(PooledObject<WebDriver> p) throws Exception {
        WebDriver driver = p.getObject();
        Set<String> handles = driver.getWindowHandles();
        String[] handlesArray = handles.toArray(new String[handles.size()]);
        int size = handles.size();
        // 留一个窗口，否则driver会退出
        for (int i = 0; i < size - 1; i++) {
            driver.switchTo().window(handlesArray[size - i]);
            driver.close();
        }
    }

}
