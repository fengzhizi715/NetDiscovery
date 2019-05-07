package cn.netdiscovery.core;

import cn.netdiscovery.core.domain.Request;
import cn.netdiscovery.core.utils.URLParser;

import java.net.MalformedURLException;
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

        String domain = null;
        try {
            URLParser urlParser = new URLParser(request.getUrl());
            domain = urlParser.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

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

            domains.put(domain,System.currentTimeMillis());
        }
    }
}
