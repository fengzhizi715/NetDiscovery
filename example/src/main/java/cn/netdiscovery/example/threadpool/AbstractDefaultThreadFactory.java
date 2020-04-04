package cn.netdiscovery.example.threadpool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @FileName: cn.netdiscovery.example.threadpool.AbstractDefaultThreadFactory
 * @author: Tony Shen
 * @date: 2020-04-04 10:15
 * @version: V1.0 <描述当前版本功能>
 */
public abstract class AbstractDefaultThreadFactory implements ThreadFactory {

    protected final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    AbstractDefaultThreadFactory() {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r,
                getNamePrefix() + threadNumber.getAndIncrement(),
                0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }

    /**
     * 线程名称前缀
     * @return
     */
    abstract String getNamePrefix();
}
