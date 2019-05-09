package cn.netdiscovery.core;

import cn.netdiscovery.core.domain.Request;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tony on 2019-05-06.
 */
public class Throttle {

    private Map<String,Long> domains = new ConcurrentHashMap<String,Long>();

    private static class Holder {
        private static final Throttle instance = new Throttle();
    }

    private Throttle() {
    }

    public static final Throttle getInsatance() {
        return Throttle.Holder.instance;
    }

    public void wait(Request request) {

        String domain = request.getUrlParser().getHost();
        Long lastAccessed = domains.get(domain);

        if (lastAccessed!=null && lastAccessed>0) {
            long sleepSecs = request.getDomainDelay() - (System.currentTimeMillis() - lastAccessed);
            if (sleepSecs > 0) {
                try {
                    Thread.sleep(sleepSecs);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        domains.put(domain,System.currentTimeMillis());
    }
}
