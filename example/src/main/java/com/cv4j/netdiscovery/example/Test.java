package com.cv4j.netdiscovery.example;

import com.cv4j.netdiscovery.core.Spider;
import com.cv4j.netdiscovery.core.SpiderEngine;

/**
 * Created by tony on 2018/1/22.
 */
public class Test {

    public static void main(String[] args) {

        Spider.create()
                .name("tony")
                .url("http://www.163.com")
                .run();
    }
}
