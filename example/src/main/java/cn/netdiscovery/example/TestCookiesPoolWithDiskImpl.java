package cn.netdiscovery.example;

import cn.netdiscovery.core.Spider;
import cn.netdiscovery.core.SpiderEngine;
import cn.netdiscovery.core.cache.RxCacheManager;
import cn.netdiscovery.core.cookies.CookiesPool;
import cn.netdiscovery.core.domain.Request;
import cn.netdiscovery.core.downloader.urlconnection.UrlConnectionDownloader;
import com.safframework.rxcache.RxCache;
import com.safframework.rxcache.persistence.disk.impl.DiskImpl;

import java.io.File;

/**
 * Created by tony on 2019-01-03.
 */
public class TestCookiesPoolWithDiskImpl {

    public static void main(String[] args) {

        File cacheDirectory = new File("temp");

        if (!cacheDirectory.exists()) {

            cacheDirectory.mkdir();
        }

        DiskImpl diskImpl = new DiskImpl(cacheDirectory); // 创建 RxCache 的 DiskImpl

        RxCacheManager.getInstance().config(new RxCache.Builder().persistence(diskImpl)); // 爬虫在使用之前，先配置 RxCache

        SpiderEngine spiderEngine = SpiderEngine.create();

        Request request = new Request("https://www.facebook.com").saveCookie(true);

        Spider spider = Spider.create().name("tony").downloader(new UrlConnectionDownloader()).request(request);

        spiderEngine.addSpider(spider).run();

        CookiesPool.getInsatance()
                .getCookieGroup(request.getUrlParser().getHost())
                .getCookies()
                .forEach(httpCookie -> {
                    System.out.println(httpCookie.getDomain());
                    System.out.println(httpCookie.getName());
                    System.out.println(httpCookie.getMaxAge());
                    System.out.println(httpCookie.getPath());
                    System.out.println(httpCookie.getSecure());
                    System.out.println(httpCookie.getVersion());
                    System.out.println(httpCookie.getValue());
                    System.out.println("-------------------------");
                });
    }
}
