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
public class CookiesPool {

    private static RxCache cache;

    private static class Holder {
        private static final CookiesPool instance = new CookiesPool();
    }

    private CookiesPool() {
    }

    public static final CookiesPool getInsatance() {
        return Holder.instance;
    }

    /**
     * 爬虫在使用之前，可以先配置RxCache.Builder
     * @param builder
     */
    public static void config(RxCache.Builder builder) {

        RxCache.config(builder);
        cache = RxCache.getRxCache();
    }

    public void addCookieGroup(CookiesGroup group) {

        checkCache();

        if (group!=null) {
            cache.save(group.getDomain(), group);
        }
    }

    public CookiesGroup getCookieGroup(String domain) {

        checkCache();

        if (cache.containsKey(domain)) {

            Record<CookiesGroup> record = cache.get(domain, CookiesGroup.class);
            return record!=null?record.getData():null;
        } else {

            return null;
        }
    }

    public void removeCookieGroup(String domain) {

        checkCache();

        cache.remove(domain);
    }

    /**
     * 检查cache是否可以
     */
    private void checkCache() {

        if (cache==null || !cache.test()) { // 如果cache为空或者cache不可用，则使用默认的配置

            RxCache.config(new RxCache.Builder());
            cache = RxCache.getRxCache();
        }
    }

    /**
     * 保存单个cookie字符串
     * @param request
     * @param cookie
     */
    public void saveCookie(Request request, String cookie) {

        if (Preconditions.isNotBlank(cookie)) {

            CookiesGroup cookiesGroup = CookiesPool.getInsatance().getCookieGroup(request.getUrlParser().getHost());
            List<HttpCookie> httpCookieList = new ArrayList<>();

            if (cookiesGroup ==null) {

                cookiesGroup = new CookiesGroup(request.getUrlParser().getHost());

                httpCookieList.addAll(HttpCookie.parse(cookie));

                cookiesGroup.putAllCookies(httpCookieList);

                CookiesPool.getInsatance().addCookieGroup(cookiesGroup);
            } else {

                httpCookieList.addAll(HttpCookie.parse(cookie));

                cookiesGroup.putAllCookies(httpCookieList);
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

            CookiesGroup cookiesGroup = CookiesPool.getInsatance().getCookieGroup(request.getUrlParser().getHost());
            List<HttpCookie> httpCookieList = new ArrayList<>();

            if (cookiesGroup ==null) {

                cookiesGroup = new CookiesGroup(request.getUrlParser().getHost());

                for (String cookieStr:cookies) {

                    httpCookieList.addAll(HttpCookie.parse(cookieStr));
                }

                cookiesGroup.putAllCookies(httpCookieList);

                CookiesPool.getInsatance().addCookieGroup(cookiesGroup);
            } else {

                for (String cookieStr:cookies) {

                    httpCookieList.addAll(HttpCookie.parse(cookieStr));
                }

                cookiesGroup.putAllCookies(httpCookieList);

            }
        }
    }
}
