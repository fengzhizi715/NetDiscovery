package com.cv4j.netdiscovery.admin.verticle;

import com.cv4j.netdiscovery.admin.LaunchAdmin;
import com.cv4j.netdiscovery.admin.common.CommonUtil;
import com.cv4j.netdiscovery.admin.common.Constant;
import com.cv4j.netdiscovery.admin.dao.MongoDao;
import com.cv4j.netdiscovery.admin.handler.HttpGetHandler;
import com.cv4j.netdiscovery.admin.handler.HttpPostHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;


import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SpiderVerticle extends AbstractVerticle {

    public final static String API_SPIDERS = "/spiders";
    public final static String API_SPIDER_BY_NAME = "/spider";
    public final static String API_SPIDER_STATUS = "/spiderstatus";

    private MongoDao mongoDao = MongoDao.getInstance();
    private Router router;
    private static WebClient webClient = WebClient.create(LaunchAdmin.vertx);

    public SpiderVerticle(Router router) {
        this.router = router;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);

        // 获取爬虫引擎里所有的爬虫信息
        router.get(API_SPIDERS).handler(this::getSpiders);
        // 根据名称获取某个爬虫的信息
        router.get(API_SPIDER_BY_NAME).handler(this::getSpider);
        // 设置某个爬虫的状态
        router.post(API_SPIDER_STATUS).handler(this::setSpiderStatus);

    }


    /**
     * 获取爬虫引擎里所有的爬虫信息
     * @param routingContext
     */
    private void getSpiders(RoutingContext routingContext) {
//        MultiMap paramMap = routingContext.queryParams();
//        String engineUri = paramMap.get("uri");
        String engineUri = CommonUtil.ENGINE_URI;
        HttpGetHandler httpGetHandler = new HttpGetHandler(routingContext);
        webClient.getAbs(engineUri + Constant.API_GET_SPIDERS).send(httpGetHandler);
    }

    /**
     * 根据名称获取某个爬虫的信息
     * @param routingContext
     */
    private void getSpider(RoutingContext routingContext) {
        MultiMap paramMap = routingContext.queryParams();
//        String engineUri = paramMap.get("uri");
        String engineUri = CommonUtil.ENGINE_URI;
        String spiderName = paramMap.get("spidername");
        HttpGetHandler httpGetHandler = new HttpGetHandler(routingContext);
        webClient.getAbs(engineUri + Constant.API_GET_SPIDER_BY_NAME + spiderName.toUpperCase()).send(httpGetHandler);
    }

    /**
     * 设置某个爬虫的状态
     * @param routingContext
     */
    private void setSpiderStatus(RoutingContext routingContext) {
//        MultiMap paramMap = routingContext.queryParams();
//        String engineUri = paramMap.get("uri");
        String engineUri = CommonUtil.ENGINE_URI;

        JsonObject postParam = routingContext.getBodyAsJson();
        Map<String, Integer> postData = new HashMap<>();
        postData.put("status", postParam.getInteger("toStatus"));

        HttpPostHandler httpPostHandler = new HttpPostHandler(routingContext);
        webClient.postAbs(engineUri + Constant.API_GET_SPIDER_BY_NAME + postParam.getString("spiderName").toUpperCase() + "/status")
                .sendJson(postData, httpPostHandler);

    }
}
