package com.cv4j.netdiscovery.admin.verticle;

import com.cv4j.netdiscovery.admin.common.CommonUtil;
import com.cv4j.netdiscovery.admin.common.Constant;
import com.cv4j.netdiscovery.admin.dao.MongoDao;
import com.cv4j.netdiscovery.admin.handler.FindHandler;
import com.cv4j.proxy.http.HttpManager;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;

@Slf4j
public class ProxyVerticle extends AbstractVerticle {

    /** 列出job完成后获取的代理IP数据 */
    public final static String API_PROXYS = "/proxys";
    /** 检验一条代理数据是否有效 */
    public final static String API_CHECK_PROXY = "/checkproxy";


    private MongoDao mongoDao = MongoDao.getInstance();
    private Router router;

    public ProxyVerticle(Router router) {
        this.router = router;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);

        //提供接口
        router.get(API_PROXYS).handler(this::getProxys);
        router.post(API_CHECK_PROXY).handler(this::checkProxy);
    }


    /**
     * 获取所有的代理IP数据
     * @param routingContext
     */
    private void getProxys(RoutingContext routingContext) {

        FindHandler findHandler = new FindHandler(routingContext);
        mongoDao.findAll(Constant.COL_NAME_PROXY, findHandler);

    }

    /**
     * 检查代理IP数据还是否有效
     * @param routingContext
     */
    private void checkProxy(RoutingContext routingContext) {
        JsonObject postParam = routingContext.getBodyAsJson();
        String type = postParam.getString("proxyType");
        String ip = postParam.getString("proxyAddress");
        Integer port = postParam.getInteger("proxyPort");

        HttpHost httpHost = new HttpHost(ip, port, type);
        if(HttpManager.get().checkProxy(httpHost)) {
            CommonUtil.sendJsonToResponse(routingContext, "success");
        } else {
            CommonUtil.sendJsonToResponse(routingContext, "failure");
        }

    }
}
