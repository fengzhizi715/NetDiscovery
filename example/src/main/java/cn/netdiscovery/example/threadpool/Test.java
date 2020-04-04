package cn.netdiscovery.example.threadpool;

import cn.netdiscovery.core.Spider;
import cn.netdiscovery.core.SpiderEngine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @FileName: cn.netdiscovery.example.threadpool.Test
 * @author: Tony Shen
 * @date: 2020-04-04 10:23
 * @version: V1.0 <描述当前版本功能>
 */
public class Test {

    private static ExecutorService pipelinePool = new ThreadPoolExecutor(5, 50,
            30L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), new PipelinePoolThreadFactory());

    public static void main(String[] args) {

        SpiderEngine spiderEngine = SpiderEngine.create();

        spiderEngine
                .addSpider(Spider.create().name("tony1").url("http://www.163.com").pipeLineThreadPool(pipelinePool))
                .addSpider(Spider.create().name("tony2").url("http://www.126.com").pipeLineThreadPool(pipelinePool))
                .addSpider(Spider.create().name("tony3").url("https://www.baidu.com").pipeLineThreadPool(pipelinePool))
                .httpd()
                .run();
    }
}
