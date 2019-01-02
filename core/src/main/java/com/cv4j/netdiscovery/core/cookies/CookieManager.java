package com.cv4j.netdiscovery.core.cookies;

import com.cv4j.netdiscovery.core.domain.Request;
import com.safframework.tony.common.utils.Preconditions;

import java.net.HttpCookie;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cookies的管理类，每一个域名对应一个CookieGroup
 *
 * Created by tony on 2018/2/1.
 */
public class CookieManager {

    private Map<String, CookieGroup> cookieGroups = new ConcurrentHashMap<>();

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
            List<HttpCookie> cookies = group.getCookies();
            if (cookies != null) {
                cookies.clear();
            }
        }
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
