package com.cv4j.netdiscovery.admin.verticle;

import com.cv4j.netdiscovery.admin.common.Constant;
import com.cv4j.netdiscovery.admin.dao.MongoDao;
import com.cv4j.netdiscovery.admin.handler.FindHandler;
import com.cv4j.netdiscovery.admin.handler.InsertHandler;
import com.cv4j.netdiscovery.admin.handler.RemoveHandler;
import com.cv4j.netdiscovery.admin.handler.UpdateHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class ResourceVerticle extends AbstractVerticle {

    public final static String API_PROXY_RESOURCES = "/proxyresources";
    public final static String API_RESOURCE_PALNS = "/resourceplans";

    private MongoDao mongoDao = MongoDao.getInstance();

    private Router router;

    public ResourceVerticle(Router router) {
        this.router = router;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);

        // 获取所有能解析的网页
        router.get(API_PROXY_RESOURCES).handler(this::getProxyPages);
        // 获取所有能解析的网页
        router.get(API_RESOURCE_PALNS).handler(this::getResourcePlans);
        // 添加一个新的资源给job使用
        router.post(API_RESOURCE_PALNS).handler(this::addResourcePlan);
        // 更新资源
        router.put(API_RESOURCE_PALNS).handler(this::updateResourcePlan);
        // 删除资源
        router.delete(API_RESOURCE_PALNS).handler(this::deleteResourcePlan);

    }

    /**
     * 获取解析类与网页的对应信息
     * @param routingContext
     */
    private void getProxyPages(RoutingContext routingContext) {
        FindHandler findHandler = new FindHandler(routingContext);
        mongoDao.findAll(Constant.COL_NAME_PROXY_RESOURCE, findHandler);
    }

    /**
     * 获取运行job要用的
     * @param routingContext
     */
    private void getResourcePlans(RoutingContext routingContext) {
        FindHandler findHandler = new FindHandler(routingContext);
        mongoDao.findAll(Constant.COL_NAME_RESOURCE_PLAN, findHandler);
    }

    /**
     * 添加
     * @param routingContext
     */
    private void addResourcePlan(RoutingContext routingContext) {
        JsonObject addDocument = routingContext.getBodyAsJson();
        addDocument.put("addTime", new Date().getTime());
        addDocument.put("modTime", new Date().getTime());

        InsertHandler insertHandler = new InsertHandler(routingContext);
        mongoDao.insert(Constant.COL_NAME_RESOURCE_PLAN, addDocument, insertHandler);
    }

    /**
     * 更新
     * @param routingContext
     */
    private void updateResourcePlan(RoutingContext routingContext) {
        MultiMap paramMap = routingContext.queryParams();
        JsonObject query = new JsonObject();
        query.put("_id", paramMap.get("id"));

        JsonObject updateDocument = routingContext.getBodyAsJson();
        updateDocument.put("modTime", new Date().getTime());

        UpdateHandler updateHandler = new UpdateHandler(routingContext);
        mongoDao.update(Constant.COL_NAME_RESOURCE_PLAN, query, updateDocument, updateHandler);
    }

    /**
     * 删除
     * @param routingContext
     */
    private void deleteResourcePlan(RoutingContext routingContext) {
        MultiMap paramMap = routingContext.queryParams();
        JsonObject query = new JsonObject();
        query.put("_id", paramMap.get("id"));

        RemoveHandler removeHandler = new RemoveHandler(routingContext);
        mongoDao.remove(Constant.COL_NAME_RESOURCE_PLAN, query, removeHandler);
    }

}