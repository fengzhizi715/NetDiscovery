package com.cv4j.netdiscovery.example

import com.cv4j.netdiscovery.coroutines.Spider

/**
 * Created by tony on 2018/8/14.
 */
fun main(args: Array<String>) {

    Spider.create()
            .name("tony")
            .url("https://www.baidu.com/","https://www.jianshu.com/u/4f2c483c12d8")
//                .repeatRequest(1000,"https://www.baidu.com/")
//                .initialDelay(1000)
            .run()
}