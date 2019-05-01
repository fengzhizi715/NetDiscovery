package cn.netdiscovery.downloader.selenium;

import com.cv4j.proxy.domain.Proxy;
import org.openqa.selenium.WebDriver;

/**
 * Created by tony on 2018/1/28.
 */
public interface WebDriverInitializer {

    /**
     *
     * @param driverPath
     * @param proxy
     * @return
     */
    WebDriver init(String driverPath, Proxy proxy);
}
