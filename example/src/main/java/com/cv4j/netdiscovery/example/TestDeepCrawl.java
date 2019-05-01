package com.cv4j.netdiscovery.example;

import cn.netdiscovery.core.Spider;
import cn.netdiscovery.core.domain.ResultItems;
import cn.netdiscovery.core.pipeline.Pipeline;

/**
 * Created by tony on 2019-02-24.
 */
public class TestDeepCrawl {

    public static void main(String[] args) {


        Spider spider = Spider.create()
                .name("tony")
                .url("https://www.baidu.com/");

        spider.pipeline(new Pipeline() {
                    @Override
                    public void process(ResultItems resultItems) {

                        this.push(spider,"https://www.163.com/");
                    }
                })
                .run();
    }
}
