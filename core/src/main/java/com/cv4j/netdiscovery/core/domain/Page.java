package com.cv4j.netdiscovery.core.domain;

import lombok.*;

/**
 * Created by tony on 2017/12/23.
 */
@Getter
@Setter
@NoArgsConstructor
public class Page {

    private String url;

    private int statusCode;//响应状态码

    private String html;//response content

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
