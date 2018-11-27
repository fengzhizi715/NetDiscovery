package com.cv4j.netdiscovery.core.domain;

import com.cv4j.netdiscovery.core.utils.URLParser;
import com.cv4j.netdiscovery.core.utils.UserAgent;
import com.cv4j.proxy.domain.Proxy;
import com.safframework.tony.common.collection.NoEmptyHashMap;
import com.safframework.tony.common.utils.Preconditions;
import io.vertx.core.http.HttpMethod;
import lombok.Getter;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by tony on 2017/12/23.
 */
@Getter
public class Request implements Serializable {

    private String url;

    private URLParser urlParser;

    private String userAgent;

    private Proxy proxy;

    private String spiderName;

    private HttpMethod httpMethod;

    private String charset;

    private boolean checkDuplicate = true;

    private boolean saveCookie = false;

    private long sleepTime = 0;// 每次请求url之前先sleep一段时间

    private Map<String, String> header = new NoEmptyHashMap<>();

    private Map<String, Object> extras; // extras 中的数据可以在pipeline中使用

    private long priority = 0; // request的优先级，数字越大优先级越高

    private HttpRequestBody httpRequestBody;

    private BeforeRequest beforeRequest;

    private AfterRequest afterRequest;

    private OnErrorRequest onErrorRequest;

    public Request() {
    }

    public Request(String url) {

        this.url = url;
        try {
            this.urlParser = new URLParser(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        this.httpMethod = HttpMethod.GET;
        autoUA();
    }

    public Request(String url, String spiderName) {

        this.url = url;
        try {
            this.urlParser = new URLParser(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        this.spiderName = spiderName;
        this.httpMethod = HttpMethod.GET;
        autoUA();
    }

    public Request(String url, String spiderName, HttpMethod httpMethod) {

        this.url = url;
        try {
            this.urlParser = new URLParser(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        this.spiderName = spiderName;
        this.httpMethod = httpMethod;
        autoUA();
    }

    public Request ua(String userAgent) {

        this.userAgent = userAgent;
        header.put("User-Agent", userAgent);
        return this;
    }

    private void autoUA() {

        this.userAgent = UserAgent.getUserAgent();
        if (Preconditions.isNotBlank(userAgent)) {
            header.put("User-Agent", userAgent);
        }
    }

    /**
     * request使用的代理，其优先级高于Spider所设置的autoProxy
     *
     * @param proxy
     * @return
     */
    public Request proxy(Proxy proxy) {

        this.proxy = proxy;
        return this;
    }

    /**
     * 爬虫的名字
     *
     * @param spiderName
     * @return
     */
    public Request spiderName(String spiderName) {

        this.spiderName = spiderName;
        return this;
    }

    /**
     * http method
     *
     * @param httpMethod
     * @return
     */
    public Request httpMethod(HttpMethod httpMethod) {

        this.httpMethod = httpMethod;
        return this;
    }

    /**
     * 网页使用的的字符集
     *
     * @param charset
     * @return
     */
    public Request charset(String charset) {

        this.charset = charset;
        return this;
    }

    /**
     * 检查url是否重复，默认情况下为true表示需要检查。
     * 如果设置为false表示不需要检测url是否重复，此时可以多次请求该url。
     *
     * @param checkDuplicate
     * @return
     */
    public Request checkDuplicate(boolean checkDuplicate) {

        this.checkDuplicate = checkDuplicate;
        return this;
    }

    /**
     * 是否保存cookie，默认情况下为false表示不保存cookie
     *
     * @param saveCookie
     * @return
     */
    public Request saveCookie(boolean saveCookie) {

        this.saveCookie = saveCookie;
        return this;
    }

    /**
     * @param sleepTime 每次请求url时先sleep一段时间，单位是milliseconds
     * @return
     */
    public Request sleep(long sleepTime) {

        if (sleepTime > 0) {
            this.sleepTime = sleepTime;
        }
        return this;
    }

    public Request header(String name, String value) {

        header.put(name, value);
        return this;
    }

    public Request referer(String referer) {

        this.header("Referer", referer);
        return this;
    }

    public Request addCookie(String cookie) {

        this.header("Cookie", cookie);
        return this;
    }

    public Request putExtra(String key, Object value) {

        if (extras == null) {

            extras = new NoEmptyHashMap<>();
        }

        extras.put(key, value);
        return this;
    }

    public Object getExtra(String key) {

        return extras != null ? extras.get(key) : null;
    }

    /**
     * 设置Request的优先级
     *
     * @param priority
     * @return
     */
    public Request priority(long priority) {

        if (priority > 0) {

            this.priority = priority;
        }

        return this;
    }

    public void clearHeader() {
        Iterator<Map.Entry<String, String>> it = this.header.entrySet().iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
    }

    public Request httpRequestBody(HttpRequestBody httpRequestBody) {

        this.httpRequestBody = httpRequestBody;
        return this;
    }

    public Request beforeRequest(BeforeRequest beforeRequest) {

        this.beforeRequest = beforeRequest;
        return this;
    }

    public Request afterRequest(AfterRequest afterRequest) {

        this.afterRequest = afterRequest;
        return this;
    }

    public Request onErrorRequest(OnErrorRequest onErrorRequest) {

        this.onErrorRequest = onErrorRequest;
        return this;
    }

    /**
     * 在request之前做的事情
     */
    @FunctionalInterface
    public interface BeforeRequest {

        void process(Request request);
    }

    /**
     * 在request之后做的事情
     */
    @FunctionalInterface
    public interface AfterRequest {

        void process(Page page);
    }

    /**
     * 在request发生异常做的事情
     */
    @FunctionalInterface
    public interface OnErrorRequest {

        void process(Request request);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        return sb.append("Request{")
                .append("spiderName=").append(spiderName)
                .append(", url='").append(url).append('\'')
                .append(", method='").append(httpMethod).append('\'')
                .append(", userAgent=").append(userAgent)
                .append(", extras=").append(extras)
                .append(", priority=").append(priority)
                .append(", headers=").append(header.toString())
                .append('}')
                .toString();
    }
}
