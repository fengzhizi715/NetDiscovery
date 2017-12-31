package com.cv4j.netdiscovery.core.domain;

import lombok.*;
import com.cv4j.netdiscovery.core.http.Request;

/**
 * Created by tony on 2017/12/23.
 */
public class Page {

    @Getter
    @Setter
    private String url;

    @Getter
    @Setter
    private int statusCode;//响应状态码

    @Getter
    @Setter
    private String html;//response content

    @Getter
    private Request request;

    @Getter
    private ResultItems resultItems = new ResultItems();

    public void setRequest(Request request) {
        this.request = request;
        this.resultItems.setRequest(request);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Page page = (Page) o;

        return url.equals(page.url);
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

}
