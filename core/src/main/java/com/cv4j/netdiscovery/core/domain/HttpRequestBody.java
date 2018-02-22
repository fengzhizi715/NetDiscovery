package com.cv4j.netdiscovery.core.domain;

import lombok.Data;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by tony on 2018/2/19.
 */
@Data
public class HttpRequestBody implements Serializable{

    public static abstract class ContentType {

        public static final String JSON = "application/json";

        public static final String XML = "text/xml";

        public static final String FORM = "application/x-www-form-urlencoded";

        public static final String MULTIPART = "multipart/form-data";
    }

    private byte[] body;

    private String contentType;

    private String encoding;

    public HttpRequestBody(byte[] body, String contentType, String encoding) {
        this.body = body;
        this.contentType = contentType;
        this.encoding = encoding;
    }

    public static HttpRequestBody json(String json) {

        return json(json,"UTF-8");
    }

    public static HttpRequestBody json(String json, String encoding) {
        try {
            return new HttpRequestBody(json.getBytes(encoding), ContentType.JSON, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("illegal encoding " + encoding, e);
        }
    }

    public static HttpRequestBody xml(String xml, String encoding) {
        try {
            return new HttpRequestBody(xml.getBytes(encoding), ContentType.XML, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("illegal encoding " + encoding, e);
        }
    }

    public static HttpRequestBody custom(byte[] body, String contentType, String encoding) {
        return new HttpRequestBody(body, contentType, encoding);
    }

    public static HttpRequestBody form(Map<String,Object> params, String encoding){

        List<NameValuePair> nameValuePairs = new ArrayList<>(params.size());
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            nameValuePairs.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
        }
        try {
            return new HttpRequestBody(URLEncodedUtils.format(nameValuePairs, encoding).getBytes(encoding), ContentType.FORM, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("illegal encoding " + encoding, e);
        }
    }

}
