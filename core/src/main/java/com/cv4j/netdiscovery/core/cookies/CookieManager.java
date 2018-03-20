package com.cv4j.netdiscovery.core.cookies;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Cookies的管理类，每一个域名对应一个CookieGroup
 *
 * Created by tony on 2018/2/1.
 */
public class CookieManager {

    private Map<String, CookieGroup> cookieGroups = new HashMap<>();

    private static class Holder {
        private static final CookieManager instance = new CookieManager();
    }

    private CookieManager() {
    }

    public static final CookieManager getInsatance() {
        return Holder.instance;
    }

    public void addCookieGroup(CookieGroup group) {

        if (group!=null) {

            cookieGroups.put(group.getDomain(), group);
        }
    }

    public CookieGroup getCookieGroup(String domain) {

        return cookieGroups.get(domain);
    }

    public void removeCookieGroup(String domain) {

        CookieGroup group = cookieGroups.remove(domain);
        if (group != null) {
            Set<Cookie> cookies = group.getCookies();
            if (cookies != null) {
                cookies.clear();
            }
        }
    }
}
