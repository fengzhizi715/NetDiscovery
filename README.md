![](images/logo.png)
# NetDiscovery

[![@Tony沈哲 on weibo](https://img.shields.io/badge/weibo-%40Tony%E6%B2%88%E5%93%B2-blue.svg)](http://www.weibo.com/fengzhizi715)
[![License](https://img.shields.io/badge/license-Apache%202-lightgrey.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/703e0ba9760b4affaf39188dbbdd2811)](https://app.codacy.com/app/fengzhizi715/NetDiscovery?utm_source=github.com&utm_medium=referral&utm_content=fengzhizi715/NetDiscovery&utm_campaign=Badge_Grade_Dashboard)


# 功能特点：

* 轻量级爬虫
* 模块化设计，便于扩展：支持多种消息队列、多种网络框架，也支持自己实现。
* 支持分布式
* 多线程、异步化：底层使用 RxJava 的多线程机制
* 支持 Request 添加到正在运行爬虫的Queue中
* 支持 Kotlin 协程
* 支持 JS 渲染
* Request 支持自定义header信息
* Request 支持 debug 功能：在调试时可以使用 RxCache，从而避免多次请求同一个网页。
* 支持失败重试的机制
* 多纬度控制爬取速度（Pipeline、Request、Download、Domain）等等
* 代理池的整合
* 支持 User Agent 池、Cookies 池
* 支持爬虫的深度抓取：能够在 Pipeline 中发起深度抓取的事件。
* 支持 URL 去重：使用布隆过滤器
* 超时控制：支持设置爬虫请求的超时时间；
* 爬虫的监控
* agent 模块能够对当前服务器的 CPU 和内存进行实时监控


# 最新版本

模块名|最新版本|
---|:-------------:
netdiscovery-core|[ ![Download](https://api.bintray.com/packages/fengzhizi715/maven/netdiscovery-core/images/download.svg) ](https://bintray.com/fengzhizi715/maven/netdiscovery-core/_latestVersion)
netdiscovery-downloader-htmlunit|[ ![Download](https://api.bintray.com/packages/fengzhizi715/maven/netdiscovery-downloader-htmlunit/images/download.svg) ](https://bintray.com/fengzhizi715/maven/netdiscovery-downloader-htmlunit/_latestVersion)
netdiscovery-downloader-httpclient|[ ![Download](https://api.bintray.com/packages/fengzhizi715/maven/netdiscovery-downloader-httpclient/images/download.svg) ](https://bintray.com/fengzhizi715/maven/netdiscovery-downloader-httpclient/_latestVersion)
netdiscovery-downloader-okhttp|[ ![Download](https://api.bintray.com/packages/fengzhizi715/maven/netdiscovery-downloader-okhttp/images/download.svg) ](https://bintray.com/fengzhizi715/maven/netdiscovery-downloader-okhttp/_latestVersion)
netdiscovery-downloader-selenium|[ ![Download](https://api.bintray.com/packages/fengzhizi715/maven/netdiscovery-downloader-selenium/images/download.svg) ](https://bintray.com/fengzhizi715/maven/netdiscovery-downloader-selenium/_latestVersion)
netdiscovery-pipeline-couchbase|[ ![Download](https://api.bintray.com/packages/fengzhizi715/maven/netdiscovery-pipeline-couchbase/images/download.svg) ](https://bintray.com/fengzhizi715/maven/netdiscovery-pipeline-couchbase/_latestVersion)
netdiscovery-pipeline-elasticsearch|[ ![Download](https://api.bintray.com/packages/fengzhizi715/maven/netdiscovery-pipeline-elasticsearch/images/download.svg) ](https://bintray.com/fengzhizi715/maven/netdiscovery-pipeline-elasticsearch/_latestVersion)
netdiscovery-pipeline-mongo|[ ![Download](https://api.bintray.com/packages/fengzhizi715/maven/netdiscovery-pipeline-mongo/images/download.svg) ](https://bintray.com/fengzhizi715/maven/netdiscovery-pipeline-mongo/_latestVersion)
netdiscovery-pipeline-redis|[ ![Download](https://api.bintray.com/packages/fengzhizi715/maven/netdiscovery-pipeline-redis/images/download.svg) ](https://bintray.com/fengzhizi715/maven/netdiscovery-pipeline-redis/_latestVersion)
netdiscovery-queue-kafka|[ ![Download](https://api.bintray.com/packages/fengzhizi715/maven/netdiscovery-queue-kafka/images/download.svg) ](https://bintray.com/fengzhizi715/maven/netdiscovery-queue-kafka/_latestVersion)
netdiscovery-queue-rabbitmq|[ ![Download](https://api.bintray.com/packages/fengzhizi715/maven/netdiscovery-queue-rabbitmq/images/download.svg) ](https://bintray.com/fengzhizi715/maven/netdiscovery-queue-rabbitmq/_latestVersion)
netdiscovery-queue-redis|[ ![Download](https://api.bintray.com/packages/fengzhizi715/maven/netdiscovery-queue-redis/images/download.svg) ](https://bintray.com/fengzhizi715/maven/netdiscovery-queue-redis/_latestVersion)
netdiscovery-kotlin-coroutines|[ ![Download](https://api.bintray.com/packages/fengzhizi715/maven/netdiscovery-kotlin-coroutines/images/download.svg) ](https://bintray.com/fengzhizi715/maven/netdiscovery-kotlin-coroutines/_latestVersion)
netdiscovery-kotlin-dsl|[ ![Download](https://api.bintray.com/packages/fengzhizi715/maven/netdiscovery-kotlin-coroutines/images/download.svg) ](https://bintray.com/fengzhizi715/maven/netdiscovery-kotlin-coroutines/_latestVersion)

NetDiscovery 是基于 Vert.x、RxJava 2 等框架实现的爬虫框架。目前很多细节在不断地完善中，未来会考虑做成一个更通用的爬虫框架。

对于 Java 工程，如果使用 gradle 构建，由于默认没有使用 jcenter()，需要在相应 module 的 build.gradle 中配置

```groovy
repositories {
    mavenCentral()
    jcenter()
}
```


# 下载:

## core

netdiscovery-core

```groovy
implementation 'cn.netdiscovery:netdiscovery-core:1.0.2'

```

## downloader

netdiscovery-downloader-htmlunit

```groovy
implementation 'cn.netdiscovery:netdiscovery-downloader-htmlunit:1.0.2'
```

netdiscovery-downloader-httpclient

```groovy
implementation 'cn.netdiscovery:netdiscovery-downloader-httpclient:1.0.2'
```

netdiscovery-downloader-okhttp

```groovy
implementation 'cn.netdiscovery:netdiscovery-downloader-okhttp:1.0.2'
```

netdiscovery-downloader-selenium

```groovy
implementation 'cn.netdiscovery:netdiscovery-downloader-selenium:1.0.2'
```

## pipeline

netdiscovery-pipeline-couchbase

```groovy
implementation 'cn.netdiscovery:netdiscovery-pipeline-couchbase:1.0.2'
```

netdiscovery-pipeline-elasticsearch

```groovy
implementation 'cn.netdiscovery:netdiscovery-pipeline-elasticsearch:1.0.2'
```

netdiscovery-pipeline-mongo

```groovy
implementation 'cn.netdiscovery:netdiscovery-pipeline-mongo:1.0.2'
```

netdiscovery-pipeline-redis

```groovy
implementation 'cn.netdiscovery:netdiscovery-pipeline-redis:1.0.2'
```

## queue

netdiscovery-queue-kafka

```groovy
implementation 'cn.netdiscovery:netdiscovery-queue-kafka:1.0.2'
```

netdiscovery-queue-rabbitmq

```groovy
implementation 'cn.netdiscovery:netdiscovery-queue-rabbitmq:1.0.2'
```
netdiscovery-queue-redis

```groovy
implementation 'cn.netdiscovery:netdiscovery-queue-redis:1.0.2'
```

## kotlin

netdiscovery-kotlin-coroutines

```groovy
implementation 'cn.netdiscovery:netdiscovery-kotlin-coroutines:1.0.2'
```

netdiscovery-kotlin-dsl

```groovy
implementation 'cn.netdiscovery:netdiscovery-kotlin-dsl:1.0.2'
```


# 详细功能查看[wiki](https://github.com/fengzhizi715/NetDiscovery/wiki)


# 案例:

* [user-agent-list](https://github.com/fengzhizi715/user-agent-list):抓取常用浏览器的user agent

# TODO List:

1. 完善 wiki，增加各个模式的使用说明
2. 各个模块能够从配置文件中获取爬虫相应的配置
3. 增强 HtmlUnit 模块
4. 增加 chromium 的支持
5. 整合[cv4j](https://github.com/imageprocessor/cv4j)以及 Tesseract，实现 OCR 识别的功能

# Contributors:

* [bdqfork](https://github.com/bdqfork)
* [homchou](https://github.com/homchou)
* [sinkinka](https://github.com/sinkinka)

# Contributing:

[Pull requests](https://help.github.com/categories/collaborating-with-issues-and-pull-requests/) are welcome



# 联系方式:

Wechat：fengzhizi715

> Java与Android技术栈：每周更新推送原创技术文章，欢迎扫描下方的公众号二维码并关注，期待与您的共同成长和进步。

![](https://user-gold-cdn.xitu.io/2018/7/24/164cc729c7c69ac1?w=344&h=344&f=jpeg&s=9082)


License
-------

    Copyright (C) 2017 - present, Tony Shen.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


