package cn.netdiscovery.core.verticle;

import cn.netdiscovery.core.Spider;
import cn.netdiscovery.core.domain.bean.SpiderJobBean;
import cn.netdiscovery.core.service.impl.RPCServiceImpl;
import com.github.bdqfork.context.ContextManager;
import com.github.bdqfork.rpc.annotation.Application;
import io.vertx.core.Future;

import java.util.Map;

/**
 * 使用 Verticle 加载对外暴露的 RPC 接口
 * @FileName: cn.netdiscovery.core.verticle.RPCVerticle
 * @author: Tony Shen
 * @date: 2020-03-09 19:17
 * @version: V1.0 <描述当前版本功能>
 */
@Application(direct = true)
public class RPCVerticle extends AbstractRPCVerticle {

    public RPCVerticle(Map<String, Spider> spiders, Map<String, SpiderJobBean> jobs) {
        super(spiders, jobs);
    }

    @Override
    public void start(Future<Void> future) {
        ContextManager contextManager = ContextManager.build(RPCVerticle.class);
        contextManager.registerService(new RPCServiceImpl(spiders,jobs));
        contextManager.open();
    }
}
