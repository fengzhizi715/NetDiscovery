package com.cv4j.netdiscovery.core.cookies;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 一个键值对
 * Created by tony on 2018/3/20.
 */
@AllArgsConstructor
@Data
public class Cookie {

    private String name;
    private String value;
}
