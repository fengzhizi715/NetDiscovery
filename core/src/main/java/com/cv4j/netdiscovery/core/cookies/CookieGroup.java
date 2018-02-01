package com.cv4j.netdiscovery.core.cookies;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tony on 2018/2/1.
 */
@Getter
public class CookieGroup {

    private String domain;
    private Map<String, String> cookies;

    public CookieGroup(String domain) {
        this.domain = domain;
        this.cookies = new HashMap<>();
    }

    public void putCookie(String key, String value) {
        this.getCookies().put(key.trim(), value.trim());
    }

    public void removeCookie(String key) {
        this.getCookies().remove(key);
    }

    public String getCookieString() {

        StringBuilder buffer = new StringBuilder();

        cookies.forEach((key, value) -> buffer.append(key).append("=").append(value).append("; "));

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
