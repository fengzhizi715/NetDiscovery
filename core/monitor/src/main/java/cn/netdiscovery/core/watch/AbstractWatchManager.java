package cn.netdiscovery.core.watch;

import cn.netdiscovery.core.config.Constant;
import cn.netdiscovery.core.domain.SpiderEngineState;
import cn.netdiscovery.core.domain.bean.MonitorBean;
import cn.netdiscovery.core.domain.response.MonitorResponse;
import cn.netdiscovery.core.utils.SerializableUtils;
import com.safframework.tony.common.utils.Preconditions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

import java.util.*;

/**
 * Created by tony on 2019-06-13.
 */
public abstract class AbstractWatchManager {

    protected Map<String, SpiderEngineState> stateMap = new HashMap<>(); // 存储各个节点的状态
    protected int defaultHttpdPort = 8316;
    protected Vertx vertx;
    protected HttpServer server;
    protected ServerOfflineProcess serverOfflineProcess;
    protected String path;

    public AbstractWatchManager serverOfflineProcess(ServerOfflineProcess serverOfflineProcess) {
        this.serverOfflineProcess = serverOfflineProcess;
        return this;
    }

    public AbstractWatchManager httpd() {

        return httpd(defaultHttpdPort);
    }

    public AbstractWatchManager httpd(int port) {

        server = vertx.createHttpServer();

        Router router = Router.router(vertx);

        Set<String> allowedHeaders = new HashSet<>();
        allowedHeaders.add("x-requested-with");
        allowedHeaders.add("Access-Control-Allow-Origin");
        allowedHeaders.add("origin");
        allowedHeaders.add("Content-Type");
        allowedHeaders.add("accept");
        allowedHeaders.add("X-PINGARUNER");

        Set<HttpMethod> allowedMethods = new HashSet<>();
        router.route().handler(CorsHandler.create("*").allowedHeaders(allowedHeaders).allowedMethods(allowedMethods));

        router.route().handler(BodyHandler.create());

        router.route("/netdiscovery/monitor").handler(routingContext -> {

            // 所有的请求都会调用这个处理器处理
            HttpServerResponse response = routingContext.response();
            response.putHeader(Constant.CONTENT_TYPE, Constant.CONTENT_TYPE_JSON);

            List<MonitorBean> list = new ArrayList<>();

            stateMap.forEach((str,state)->{

                String ipAddr = str.replace(path +"/","");
                String[] addresses =ipAddr.split("-");
                if (Preconditions.isNotBlank(addresses) && addresses.length>=2) {

                    MonitorBean bean = new MonitorBean();
                    bean.setIp(addresses[0]);
                    bean.setPort(addresses[1]);
                    bean.setState(state.getState());
                    list.add(bean);
                }
            });

            MonitorResponse monitorResponse = new MonitorResponse();
            monitorResponse.setCode(Constant.OK_STATUS_CODE);
            monitorResponse.setMessage(Constant.SUCCESS);
            monitorResponse.setData(list);

            // 写入响应并结束处理
            response.end(SerializableUtils.toJson(monitorResponse));
        });

        server.requestHandler(router::accept).listen(port);
        return this;
    }

    public abstract void start();
}
