package com.cv4j.netdiscovery.core;

import com.cv4j.proxy.domain.Proxy;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created by tony on 2018/1/2.
 */
@Slf4j
public class SpiderEngine {

    private List<Proxy> proxyList;

    private List<String> uaList;

    private List<Spider> spiders;

    private SpiderEngine() {
    }

    public static SpiderEngine create() {

        return new SpiderEngine();
    }
}
