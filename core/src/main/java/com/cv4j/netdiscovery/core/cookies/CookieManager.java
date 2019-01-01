package com.cv4j.netdiscovery.core.cookies;

import com.cv4j.netdiscovery.core.domain.Request;
import com.safframework.tony.common.utils.Preconditions;

import java.util.List;
import java.util.Map;
import java.util.Set;
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
            Set<Pair> cookies = group.getCookies();
            if (cookies != null) {
                cookies.clear();
            }
        }
    }

    public void saveCookie(Request request, Set<Pair> cookieSet, String cookieStr) {

        if (Preconditions.isNotBlank(cookieStr)) {

            CookieGroup cookieGroup = CookieManager.getInsatance().getCookieGroup(request.getUrlParser().getHost());

            if (cookieGroup == null) {

                cookieGroup = new CookieGroup(request.getUrlParser().getHost());

                String[] segs = cookieStr.split(";");
                if (Preconditions.isNotBlank(segs)) {

                    for (String seg : segs) {

                        String[] pairs = seg.trim().split("\\=");
                        if (pairs.length == 2) {

                            cookieSet.add(new Pair(pairs[0], pairs[1]));
                        }
                    }
                }

                cookieGroup.putAllCookies(cookieSet);

                CookieManager.getInsatance().addCookieGroup(cookieGroup);
            } else {

                String[] segs = cookieStr.split(";");
                if (Preconditions.isNotBlank(segs)) {

                    for (String seg : segs) {

                        String[] pairs = seg.trim().split("\\=");
                        if (pairs.length == 2) {

                            cookieSet.add(new Pair(pairs[0], pairs[1]));
                        }
                    }
                }

                cookieGroup.putAllCookies(cookieSet); // 添加额外的cookie
            }
        }
    }

    public void saveCookie(Request request, Set<Pair> cookieSet, List<String> cookies) {

        if (Preconditions.isNotBlank(cookies)) {

            CookieGroup cookieGroup = CookieManager.getInsatance().getCookieGroup(request.getUrlParser().getHost());

            if (cookieGroup==null) {

                cookieGroup = new CookieGroup(request.getUrlParser().getHost());

                for (String cookieStr:cookies) {

                    String[] segs = cookieStr.split(";");
                    if (Preconditions.isNotBlank(segs)) {

                        for (String seg:segs) {

                            String[] pairs = seg.trim().split("\\=");
                            if (pairs.length==2) {

                                cookieSet.add(new Pair(pairs[0],pairs[1]));
                            }
                        }
                    }
                }

                cookieGroup.putAllCookies(cookieSet);

                CookieManager.getInsatance().addCookieGroup(cookieGroup);
            } else {

                for (String cookieStr:cookies) {

                    String[] segs = cookieStr.split(";");
                    if (Preconditions.isNotBlank(segs)) {

                        for (String seg:segs) {

                            String[] pairs = seg.trim().split("\\=");
                            if (pairs.length==2) {

                                cookieSet.add(new Pair(pairs[0],pairs[1]));
                            }
                        }
                    }
                }

                cookieGroup.putAllCookies(cookieSet);
            }
        }
    }
}
