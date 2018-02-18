package com.cv4j.netdiscovery.core.domain;

import com.cv4j.netdiscovery.core.utils.UserAgent;
import com.cv4j.proxy.domain.Proxy;
import com.safframework.tony.common.collection.NoEmptyHashMap;
import com.safframework.tony.common.utils.Preconditions;
import lombok.Getter;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by tony on 2017/12/23.
 */
@Getter
public class Request {

    private String url;

    private String userAgent;

    private Proxy proxy;

    private String spiderName;

    private HttpMethod httpMethod;

    private String charset;

    private boolean checkDuplicate = true;

    private long sleepTime = 0;// 每次请求url时先sleep一段时间

    private Map<String,String> header = new NoEmptyHashMap<>();

    private Map<String, String> cookies = new NoEmptyHashMap<>();

    private Map<String,Object> extras;

    private HttpRequestBody httpRequestBody;

    private BeforeRequest beforeRequest;

    private AfterRequest afterRequest;

    public Request() {
    }

    public Request(String url) {

        this.url = url;
        this.httpMethod = HttpMethod.GET;
        autoUA();
    }

    public Request(String url,String spiderName) {

        this.url = url;
        this.spiderName = spiderName;
        this.httpMethod = HttpMethod.GET;
        autoUA();
    }

    public Request(String url,String spiderName,HttpMethod httpMethod) {

        this.url = url;
        this.spiderName = spiderName;
        this.httpMethod = httpMethod;
        autoUA();
    }

    public Request ua(String userAgent) {

        this.userAgent = userAgent;
        header.put("User-Agent",userAgent);
        return this;
    }

    private void autoUA() {

        this.userAgent = UserAgent.getUserAgent();
        if (Preconditions.isNotBlank(userAgent)) {
            header.put("User-Agent",userAgent);
        }
    }

    public Request proxy(Proxy proxy) {

        this.proxy = proxy;
        return this;
    }

    public Request spiderName(String spiderName) {

        this.spiderName = spiderName;
        return this;
    }

    public Request httpMethod(HttpMethod httpMethod) {

        this.httpMethod = httpMethod;
        return this;
    }

    /**
     * 网页使用的的字符集
     * @param charset
     * @return
     */
    public Request charset(String charset) {

        this.charset = charset;
        return this;
    }

    /**
     * 检查url是否重复，默认情况位true表示需要检查，如果设置为false表示不需要检测url是否重复。
     * @param checkDuplicate
     * @return
     */
    public Request checkDuplicate(boolean checkDuplicate) {

        this.checkDuplicate = checkDuplicate;
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

    public Request header(String name,String value) {

        header.put(name,value);
        return this;
    }

    public Request referer(String referer) {

        this.header("Referer", referer);
        return this;
    }

    public Request addCookie(String name, String value) {

        cookies.put(name, value);
        return this;
    }

    public Request putExtra(String key,Object value) {

        if (extras==null) {

            extras = new NoEmptyHashMap<>();
        }

        extras.put(key,value);
        return this;
    }

    public Object getExtra(String key) {

        return extras != null?extras.get(key):null;
    }

    public void clearHeader() {
        Iterator<Map.Entry<String, String>> it = this.header.entrySet().iterator();
        while(it.hasNext()){
            it.next();
            it.remove();
        }
    }

    public void clearCookie() {
        Iterator<Map.Entry<String, String>> it = this.cookies.entrySet().iterator();
        while(it.hasNext()){
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

    /**
     * 在request之前做的事情
     */
    @FunctionalInterface
    public interface BeforeRequest{

        void process(Request request);
    }

    /**
     * 在request之前做的事情
     */
    @FunctionalInterface
    public interface AfterRequest{

        void process(Page page);
    }
}
