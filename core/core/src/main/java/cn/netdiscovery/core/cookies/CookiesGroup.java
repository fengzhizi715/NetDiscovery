package cn.netdiscovery.core.cookies;

import java.io.Serializable;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tony on 2018/2/1.
 */
public class CookiesGroup implements Serializable {

    private static final long serialVersionUID = -4641596355459197507L;

    private String domain;
    private List<HttpCookie> cookies;

    public CookiesGroup(String domain) {
        this.domain = domain;
        this.cookies = new ArrayList<>();
    }

    public String getDomain() {
        return domain;
    }

    public List<HttpCookie> getCookies() {
        return cookies;
    }

    public void putCookie(HttpCookie cookie) {
        this.getCookies().add(cookie);
    }

    public void putAllCookies(List<HttpCookie>  cookies) {
        this.getCookies().addAll(cookies);
    }

    public void removeCookie(HttpCookie cookie) {
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
        return "CookiesGroup{" +
                "domain='" + domain + '\'' +
                ", cookies=" + cookies +
                '}';
    }
}
