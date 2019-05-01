package com.cv4j.netdiscovery.core.utils;

import com.cv4j.netdiscovery.core.exception.SpiderException;
import com.safframework.tony.common.utils.Preconditions;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by tony on 2018/2/3.
 */
public class URLParser implements Serializable {

    private String host;

    private Integer port;

    private String protocol;

    // use LinkedHashMap to keep the order of items
    private LinkedHashMap<String, List<String>> params = new LinkedHashMap<String, List<String>>();

    private String path;

    private String userInfo;

    private String query;

    private String charset;

    private boolean hasDomain = true;

    public URLParser() {
    }

    public URLParser(String url) throws MalformedURLException {
        this(url, "utf-8");
    }

    /**
     * http://user:password@host:port/aaa/bbb;xxx=xxx?eee=ggg&fff=ddd&fff=lll
     *
     * @throws MalformedURLException
     */
    public URLParser(final String url, final String charset) throws MalformedURLException {
        Preconditions.checkNotNull(url);
        if (charset != null && !Charset.isSupported(charset)) {
            throw new IllegalArgumentException("charset is not supported: " + charset);
        }
        final URL u;
        if (url.matches("\\w+[:][/][/].*")) {
            hasDomain = true;
            u = new URL(url);
        } else {
            hasDomain = false;
            u = new URL("http://dummy" + (url.startsWith("/") ? url : ("/" + url)));
        }

        this.charset = charset;
        if (hasDomain) {
            this.protocol = u.getProtocol();
            this.host = u.getHost();
            this.port = u.getPort();
            if (this.port != null && this.port == -1) {
                this.port = null;
            }
            this.path = u.getPath();
            this.userInfo = u.getUserInfo();
        } else {
            this.path = url.startsWith("/") ? u.getPath() : u.getPath().substring(1);
        }
        this.query = u.getQuery();
        this.params = parseQueryString(substringAfter(url, "?"));
    }

    public void addParam(String name, String value) {
        addParams(name, Arrays.asList(value));
    }

    public void addParams(String name, List<String> values) {
        List<String> list = getOrCreate(params, name);
        for (String value : values) {

            list.add(encode(value));
        }
    }

    public void removeParams(String name) {
        if (name == null) {
            return;
        }
        this.params.remove(name);
    }

    public void updateParams(String name, String... values) {
        Preconditions.checkNotNull(name);
        if (values.length == 0) {
            throw new SpiderException("values should not be empty");
        }
        List<String> list = getOrCreate(params, name);
        list.clear();
        for (String value : values) {
            list.add(encode(value));
        }
    }

    public List<String> getRawParams(String name) {
        Preconditions.checkNotNull(name);
        return this.params.get(name);
    }

    public String getRawParam(String name) {
        List<String> params = getRawParams(name);
        return params == null ? null : params.get(0);
    }

    public String getParam(String name) throws UnsupportedEncodingException {
        String value = getRawParam(name);
        return value == null ? null : decode(value);
    }

    public List<String> getParams(String name) {
        List<String> rawParams = getRawParams(name);
        if (rawParams == null) {
            return null;
        }
        List<String> params = new ArrayList<String>();
        for (String value : rawParams) {
            params.add(decode(value));
        }
        return params;
    }

    public Map<String, String> getSimple() {
        Map<String, String> map = new HashMap<String, String>();
        for (String name : this.params.keySet()) {
            String value = this.params.get(name).get(0);
            map.put(name, encode(value));
        }
        return map;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getPath() {
        return path;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public String getCharset() {
        return charset;
    }

    public String getQuery() {
        return query;
    }

    public String createQueryString() {
        if (this.params.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String name : this.params.keySet()) {
            List<String> values = this.params.get(name);
            for (String value : values) {
                if (sb.length() > 0) {
                    sb.append("&");
                }
                sb.append(name).append("=").append(value);
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.protocol != null) {
            sb.append(this.protocol).append("://");
        }
        if (this.userInfo != null) {
            sb.append(this.userInfo).append("@");
        }
        if (this.host != null) {
            sb.append(host);
        }
        if (this.port != null) {
            sb.append(":").append(this.port);
        }
        sb.append(this.path);
        String query = createQueryString();
        if (query.trim().length() > 0) {
            sb.append("?").append(query);
        }

        return sb.toString();
    }

    private String decode(String value) {
        Preconditions.checkNotNull(value);
        try {
            return charset == null ? value : URLDecoder.decode(value, charset);
        } catch (UnsupportedEncodingException e) {
            throw new SpiderException(e);
        }
    }

    private String encode(String value) {
        Preconditions.checkNotNull(value);
        try {
            return charset == null ? value : URLEncoder.encode(value, charset);
        } catch (UnsupportedEncodingException e) {
            throw new SpiderException(e);
        }
    }

    private static List<String> getOrCreate(Map<String, List<String>> map, String name) {
        Preconditions.checkNotNull(name);
        List<String> list = map.get(name);
        if (list == null) {
            list = new ArrayList<String>();
            map.put(name, list);
        }
        return list;
    }

    private static LinkedHashMap<String, List<String>> parseQueryString(String query) {
        LinkedHashMap<String, List<String>> params = new LinkedHashMap<String, List<String>>();
        if (Preconditions.isBlank(query)) {
            return params;
        }
        String[] items = query.split("&");
        for (String item : items) {
            String name = substringBefore(item, "=");
            String value = substringAfter(item, "=");
            List<String> values = getOrCreate(params, name);
            values.add(value);
        }
        return params;
    }

    private static String substringBefore(String str, String sep) {
        int index = str.indexOf(sep);
        return index == -1 ? "" : str.substring(0, index);
    }

    private static String substringAfter(String str, String sep) {
        int index = str.indexOf(sep);
        return index == -1 ? "" : str.substring(index + 1);
    }
}
