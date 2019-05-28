package cn.netdiscovery.example;

import cn.netdiscovery.core.Spider;
import cn.netdiscovery.core.SpiderEngine;
import com.cv4j.proxy.ProxyPool;
import com.cv4j.proxy.site.ProxyDbProxyListPageParser;
import com.cv4j.proxy.site.XiaoHeXiaProxyListPageParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tony on 2019-05-28.
 */
public class TestProxyPool {

    public static void main(String[] args) {

        SpiderEngine spiderEngine = SpiderEngine.create();

        Map<String, Class> proxyMap = new HashMap<>();
        proxyMap.put("http://www.xiaohexia.cn/", XiaoHeXiaProxyListPageParser.class);

        spiderEngine.startProxyPool(proxyMap);
    }
}
