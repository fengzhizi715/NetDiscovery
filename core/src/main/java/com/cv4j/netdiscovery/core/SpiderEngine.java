package com.cv4j.netdiscovery.core;

import com.alibaba.fastjson.JSON;
import com.cv4j.netdiscovery.core.domain.SpiderEntity;
import com.cv4j.netdiscovery.core.http.Request;
import com.cv4j.netdiscovery.core.queue.DefaultQueue;
import com.cv4j.netdiscovery.core.queue.Queue;
import com.cv4j.netdiscovery.core.queue.RedisQueue;
import com.cv4j.proxy.ProxyPool;
import com.cv4j.proxy.domain.Proxy;
import com.safframework.tony.common.collection.NoEmptyHashMap;
import com.safframework.tony.common.utils.Preconditions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Created by tony on 2018/1/2.
 */
@Slf4j
public class SpiderEngine {

    private Map<String,Spider> spiders = new NoEmptyHashMap<>();

    @Getter
    private Queue queue;

    private SpiderEngine() {

        this.queue = new DefaultQueue();
    }

    private SpiderEngine(Queue queue) {

        this.queue = queue;
    }

    public static SpiderEngine create() {

        return new SpiderEngine();
    }

    public static SpiderEngine create(Queue queue) {

        return new SpiderEngine(queue);
    }

    public SpiderEngine proxyList(List<Proxy> proxies) {

        ProxyPool.addProxyList(proxies);
        return this;
    }

    /**
     * 添加爬虫到SpiderEngine，由SpiderEngine来管理
     * @param spider
     * @return
     */
    public SpiderEngine addSpider(Spider spider) {

        if (spider!=null) {
            spiders.put(spider.getName(),spider);
        }
        return this;
    }

    /**
     * 在SpiderEngine中创建一个爬虫
     * @param name
     * @return Spider
     */
    public Spider createSpider(String name) {

        Spider spider = Spider.create(this.getQueue()).name(name);
        spiders.put(name,spider);
        return spider;
    }

    /**
     * 对各个爬虫的状态进行监测，并返回json格式
     * @param port
     */
    public void httpd(int port) {

        HttpServer server = Vertx.vertx().createHttpServer();

        Router router = Router.router(Vertx.vertx());

        if (Preconditions.isNotBlank(spiders)) {

            Spider spider;

            for (Map.Entry<String,Spider> entry:spiders.entrySet()) {

                spider = entry.getValue();

                router.route("/netdiscovery/spider/"+spider.getName()).handler(routingContext -> {

                    // 所有的请求都会调用这个处理器处理
                    HttpServerResponse response = routingContext.response();
                    response.putHeader("content-type", "application/json");

                    SpiderEntity entity = new SpiderEntity();
                    entity.setSpiderName(spider.getName());
                    entity.setSpiderStatus(spider.getSpiderStatus());
                    entity.setLeftRequestSize(spider.getQueue().getLeftRequests(spider.getName()));
                    entity.setTotalRequestSize(spider.getQueue().getTotalRequests(spider.getName()));
                    entity.setQueueType(spider.getQueue().type());

                    // 写入响应并结束处理
                    response.end(JSON.toJSONString(entity));
                });
            }
        }

        server.requestHandler(router::accept).listen(port);
    }

    public void run() {

        if (Preconditions.isNotBlank(spiders)) {

            spiders.forEach(new BiConsumer<String, Spider>() {
                @Override
                public void accept(String s, Spider spider) {
                    spider.run();
                }
            });
        }
    }

    public static void main(String[] args) {

        JedisPool pool = new JedisPool("127.0.0.1", 6379);

        SpiderEngine engine = new SpiderEngine(new RedisQueue(pool));

        Spider spider = Spider.create(engine.getQueue())
                .name("tony")
                .request(new Request("http://www.163.com/"))
                .request(new Request("https://www.baidu.com/"))
                .request(new Request("https://www.baidu.com/"));

        engine.addSpider(spider);
        engine.run();

        engine.httpd(8080);
    }

}
