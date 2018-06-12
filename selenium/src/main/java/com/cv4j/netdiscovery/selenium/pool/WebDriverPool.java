package com.cv4j.netdiscovery.selenium.pool;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.openqa.selenium.WebDriver;

/**
 * Created by tony on 2018/3/9.
 */
public class WebDriverPool {

    private static GenericObjectPool<WebDriver> webDriverPool = null;

    /**
     * 如果需要使用WebDriverPool，则必须先调用这个init()方法
     *
     * @param config
     */
    public static void init(WebDriverPoolConfig config) {

        webDriverPool = new GenericObjectPool<>(new WebDriverPooledFactory(config));
        webDriverPool.setMaxTotal(Integer.parseInt(System.getProperty(
                "webdriver.pool.max.total", "20"))); // 最多能放多少个对象
        webDriverPool.setMinIdle(Integer.parseInt(System.getProperty(
                "webdriver.pool.min.idle", "1")));   // 最少有几个闲置对象
        webDriverPool.setMaxIdle(Integer.parseInt(System.getProperty(
                "webdriver.pool.max.idle", "20"))); // 最多允许多少个闲置对象

        try {
            webDriverPool.preparePool();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static WebDriver borrowOne() {

        if (webDriverPool!=null) {

            try {
                return webDriverPool.borrowObject();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    public static void returnOne(WebDriver driver) {

        if (webDriverPool!=null) {

            webDriverPool.returnObject(driver);
        }
    }

    public static void destory() {

        if (webDriverPool!=null) {

            webDriverPool.clear();
            webDriverPool.close();
        }
    }

    public static boolean hasWebDriverPool() {

        return webDriverPool!=null;
    }
}
