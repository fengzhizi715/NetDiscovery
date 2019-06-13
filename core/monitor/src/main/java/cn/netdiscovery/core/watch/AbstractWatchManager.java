package cn.netdiscovery.core.watch;

import cn.netdiscovery.core.domain.SpiderEngineState;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tony on 2019-06-13.
 */
public class AbstractWatchManager {

    protected Map<String, SpiderEngineState> stateMap = new HashMap<>(); // 存储各个节点的状态
    protected int defaultHttpdPort = 8316;
    protected Vertx vertx;
    protected HttpServer server;
    protected ServerOfflineProcess serverOfflineProcess;
}
