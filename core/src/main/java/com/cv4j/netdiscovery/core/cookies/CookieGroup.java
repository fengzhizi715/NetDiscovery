package com.cv4j.netdiscovery.core.cookies;

import lombok.Getter;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by tony on 2018/2/1.
 */
@Getter
public class CookieGroup {

    private String domain;
    private Set<Cookie> cookies;

    public CookieGroup(String domain) {
        this.domain = domain;
        this.cookies = new LinkedHashSet<>();
    }

    public void putCookie(String key, String value) {
        this.getCookies().add(new Cookie(key.trim(), value.trim()));
    }

    public void putCookie(Cookie cookie) {
        this.getCookies().add(cookie);
    }

    public void putAllCookies(Set<Cookie> cookieSet) {
        this.cookies.addAll(cookieSet);
    }

    public void removeCookie(Cookie cookie) {
        this.getCookies().remove(cookie);
    }

    /**
     * 将cookieGroup转换成String
     * @return
     */
    public String getCookieString() {

        StringBuilder buffer = new StringBuilder();

        cookies.forEach(cookie -> buffer.append(cookie.getName()).append("=").append(cookie.getValue()).append("; "));

        return buffer.length() > 2 ? buffer.substring(0, buffer.length() - 2) : buffer.toString();
    }

    @Override
    public String toString() {
        return "CookieGroup{" +
                "domain='" + domain + '\'' +
                ", cookies=" + cookies +
                '}';
    }
}
