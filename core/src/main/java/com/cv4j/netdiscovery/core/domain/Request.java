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

    private boolean checkDuplicate = true;

    private long sleepTime = 0;// 每次请求url时先sleep一段时间

    private Map<String,String> header = new NoEmptyHashMap<>();

    private Map<String, String> cookies = new NoEmptyHashMap<>();

    private Map<String,Object> extras;

    private BeforeRequest beforeRequest;

    private AfterRequest afterRequest;

    public Request() {
    }

    public Request(String url) {

        this.url = url;
    }

    public Request(String url,String spiderName) {

        this.url = url;
        this.spiderName = spiderName;
    }

    public Request ua(String userAgent) {

        this.userAgent = userAgent;
        header.put("User-Agent",userAgent);
        return this;
    }

    public Request autoUA() {

        this.userAgent = UserAgent.getUserAgent();
        if (Preconditions.isNotBlank(userAgent)) {
            header.put("User-Agent",userAgent);
        }
        return this;
    }

    public Request proxy(Proxy proxy) {

        this.proxy = proxy;
        return this;
    }

    public Request spiderName(String spiderName) {

        this.spiderName = spiderName;
        return this;
    }

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
