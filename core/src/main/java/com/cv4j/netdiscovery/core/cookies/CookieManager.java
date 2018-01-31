package com.cv4j.netdiscovery.core.cookies;

import java.util.HashMap;
import java.util.Map;

/**
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
}
