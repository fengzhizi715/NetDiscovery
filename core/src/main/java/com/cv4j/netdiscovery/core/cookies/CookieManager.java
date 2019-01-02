package com.cv4j.netdiscovery.core.cookies;

import com.cv4j.netdiscovery.core.domain.Request;
import com.safframework.rxcache.RxCache;
import com.safframework.rxcache.domain.Record;
import com.safframework.tony.common.utils.Preconditions;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

/**
 * Cookies的管理类，每一个域名对应一个CookieGroup
 *
 * Created by tony on 2018/2/1.
 */
public class CookieManager {

    private static RxCache cache;

    static {
        RxCache.config(new RxCache.Builder());
        cache = RxCache.getRxCache();
    }

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
            cache.save(group.getDomain(), group);
        }
    }

    public CookieGroup getCookieGroup(String domain) {

        if (cache.containsKey(domain)) {

            Record<CookieGroup> record = cache.get(domain,CookieGroup.class);
            return record!=null?record.getData():null;
        } else {

            return null;
        }
    }

    public void removeCookieGroup(String domain) {

        cache.remove(domain);
    }

    /**
     * 保存单个cookie字符串
     * @param request
     * @param cookie
     */
    public void saveCookie(Request request, String cookie) {

        if (Preconditions.isNotBlank(cookie)) {

            CookieGroup cookieGroup = CookieManager.getInsatance().getCookieGroup(request.getUrlParser().getHost());
            List<HttpCookie> httpCookieList = new ArrayList<>();

            if (cookieGroup==null) {

                cookieGroup = new CookieGroup(request.getUrlParser().getHost());

                httpCookieList.addAll(HttpCookie.parse(cookie));

                cookieGroup.putAllCookies(httpCookieList);

                CookieManager.getInsatance().addCookieGroup(cookieGroup);
            } else {

                httpCookieList.addAll(HttpCookie.parse(cookie));

                cookieGroup.putAllCookies(httpCookieList);
            }
        }
    }

    /**
     * 保存cookie字符串列表
     * @param request
     * @param cookies
     */
    public void saveCookie(Request request, List<String> cookies) {

        if (Preconditions.isNotBlank(cookies)) {

            CookieGroup cookieGroup = CookieManager.getInsatance().getCookieGroup(request.getUrlParser().getHost());
            List<HttpCookie> httpCookieList = new ArrayList<>();

            if (cookieGroup==null) {

                cookieGroup = new CookieGroup(request.getUrlParser().getHost());

                for (String cookieStr:cookies) {

                    httpCookieList.addAll(HttpCookie.parse(cookieStr));
                }

                cookieGroup.putAllCookies(httpCookieList);

                CookieManager.getInsatance().addCookieGroup(cookieGroup);
            } else {

                for (String cookieStr:cookies) {

                    httpCookieList.addAll(HttpCookie.parse(cookieStr));
                }

                cookieGroup.putAllCookies(httpCookieList);

            }
        }
    }
}
