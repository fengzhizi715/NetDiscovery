package cn.netdiscovery.core.curator;

import cn.netdiscovery.core.config.Configuration;
import cn.netdiscovery.core.config.Constant;
import cn.netdiscovery.core.curator.domain.SpiderEngineState;
import cn.netdiscovery.core.curator.domain.bean.MonitorBean;
import cn.netdiscovery.core.curator.domain.response.MonitorResponse;
import cn.netdiscovery.core.utils.SerializableUtils;
import com.safframework.tony.common.utils.Preconditions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tony on 2019-05-21.
 */
@Slf4j
public class CuratorManager implements Watcher {

    private CuratorFramework client;

    private List<String> znodes; // 用于存储指定 zNode 下所有子 zNode 的名字
    private Map<String, SpiderEngineState> stateMap = new HashMap<>(); // 存储各个节点的状态
    private int defaultHttpdPort = 8316;
    private Vertx vertx;
    private HttpServer server;
    private ServerOfflineProcess serverOfflineProcess;

    public CuratorManager() {

        this(Configuration.getConfig("spiderEngine.config.zkStr"));
    }

    public CuratorManager(String zkStr) {

        if (Preconditions.isNotBlank(zkStr)) {
            log.info("zkStr: {}", zkStr);

            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
            client = CuratorFrameworkFactory.newClient(zkStr, retryPolicy);
            // 在start方法之后书写具体的操作
            client.start();

            try {
                znodes = client.getChildren().usingWatcher(this).forPath("/netdiscovery");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (Preconditions.isNotBlank(znodes)) {

                znodes.forEach(node->{

                    stateMap.put(node,SpiderEngineState.ONLINE);
                });
            }

            vertx = Vertx.vertx();
        }
    }

    public CuratorManager serverOfflineProcess(ServerOfflineProcess serverOfflineProcess) {
        this.serverOfflineProcess = serverOfflineProcess;
        return this;
    }

    /**
     * 当前所监控的父的 zNode 下若是子 zNode 发生了变化：新增，删除，修改
     * <p>
     * 下述方法都会触发执行
     *
     * @param event
     */
    @Override
    public void process(WatchedEvent event) {

        List<String> newZodeInfos = null;
        try {
            newZodeInfos = client.getChildren().usingWatcher(this).forPath("/netdiscovery");
            //根据初始化容器的长度与最新的容器的长度进行比对，就可以推导出当前 SpiderEngine 集群的状态：新增，宕机/下线，变更...
            //哪个容器中元素多，就循环遍历哪个容器。
            if (Preconditions.isNotBlank(newZodeInfos)) {
                if (newZodeInfos.size()>znodes.size()){
                    //明确显示新增了哪个 SpiderEngine 节点
                    for (String nowZNode:newZodeInfos) {
                        if (!znodes.contains(nowZNode)){
                            log.info("新增 SpiderEngine 节点{}", nowZNode);
                            stateMap.put(nowZNode,SpiderEngineState.ONLINE);
                        }
                    }
                }else if (newZodeInfos.size()<znodes.size()){
                    // 宕机/下线
                    // 明确显示哪个 SpiderEngine 节点宕机/下线了
                    for (String initZNode : znodes) {
                        if (!newZodeInfos.contains(initZNode)) {
                            log.info("SpiderEngine 节点【{}】下线了！", initZNode);
                            stateMap.put(initZNode,SpiderEngineState.OFFLINE);

                            // 如果有下线的处理，则处理(例如发邮件、短信、重启等)
                            if (serverOfflineProcess!=null) {
                                serverOfflineProcess.process();
                            }
                        }
                    }
                }else {
                    // SpiderEngine 集群正常运行;
                    // 宕机/下线了，当时马上重启了，总数未发生变化
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        znodes = newZodeInfos;
    }

    public void start(){
        while (true){

        }
    }

    public CuratorManager httpd() {

        return httpd(defaultHttpdPort);
    }

    public CuratorManager httpd(int port) {

        server = vertx.createHttpServer();

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.route("/netdiscovery/monitor").handler(routingContext -> {

            // 所有的请求都会调用这个处理器处理
            HttpServerResponse response = routingContext.response();
            response.putHeader(Constant.CONTENT_TYPE, Constant.CONTENT_TYPE_JSON);

            List<MonitorBean> list = new ArrayList<>();

            stateMap.forEach((str,state)->{

                String ipAddr = str.replace("/netdiscovery/","");
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

    /**
     * SpiderEngine 节点下线的处理
     */
    @FunctionalInterface
    public interface ServerOfflineProcess  {

        void process();
    }
}