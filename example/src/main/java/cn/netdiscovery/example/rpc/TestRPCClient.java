package cn.netdiscovery.example.rpc;

import cn.netdiscovery.core.domain.bean.SpiderBean;
import cn.netdiscovery.core.service.RPCService;
import com.github.bdqfork.context.ContextManager;
import com.github.bdqfork.rpc.annotation.Application;
import com.github.bdqfork.rpc.config.ReferenceConfig;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @FileName: cn.netdiscovery.example.rpc.Main
 * @author: Tony Shen
 * @date: 2020-03-10 12:07
 * @version: V1.0 <描述当前版本功能>
 */
@Application(direct = true)
public class TestRPCClient {

    public static void main(String[] args) throws Exception {
        ContextManager contextManager = ContextManager.build(TestRPCClient.class);
        ReferenceConfig<?> referenceConfig = new ReferenceConfig<>(RPCService.class);
        referenceConfig.setConnections(2);

        RPCService rpcService = (RPCService) contextManager.getProxy(referenceConfig);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(40, 50, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(512),
                new ThreadPoolExecutor.DiscardPolicy());
        while (true) {
            executor.execute(() -> {
                SpiderBean bean = rpcService.detail("tony1");
                System.out.println(bean);
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
