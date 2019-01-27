![](images/logo.png)
# NetDiscovery

[![@Tony沈哲 on weibo](https://img.shields.io/badge/weibo-%40Tony%E6%B2%88%E5%93%B2-blue.svg)](http://www.weibo.com/fengzhizi715)
[![License](https://img.shields.io/badge/license-Apache%202-lightgrey.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

# 最新版本

模块名|最新版本|
---|:-------------:
netdiscovery-core|[ ![Download](https://api.bintray.com/packages/fengzhizi715/maven/netdiscovery-core/images/download.svg) ](https://bintray.com/fengzhizi715/maven/netdiscovery-core/_latestVersion)
netdiscovery-extra|[ ![Download](https://api.bintray.com/packages/fengzhizi715/maven/netdiscovery-extra/images/download.svg) ](https://bintray.com/fengzhizi715/maven/netdiscovery-extra/_latestVersion)
netdiscovery-selenium|[ ![Download](https://api.bintray.com/packages/fengzhizi715/maven/netdiscovery-selenium/images/download.svg) ](https://bintray.com/fengzhizi715/maven/netdiscovery-selenium/_latestVersion)
netdiscovery-dsl|[ ![Download](https://api.bintray.com/packages/fengzhizi715/maven/netdiscovery-dsl/images/download.svg) ](https://bintray.com/fengzhizi715/maven/netdiscovery-dsl/_latestVersion)
netdiscovery-coroutines| [ ![Download](https://api.bintray.com/packages/fengzhizi715/maven/netdiscovery-coroutines/images/download.svg) ](https://bintray.com/fengzhizi715/maven/netdiscovery-coroutines/_latestVersion)


NetDiscovery 是基于 Vert.x、RxJava 2 等框架实现的爬虫框架。目前还处于早期版本，很多细节正在不断地完善中。

对于 Java 工程，如果使用 gradle 构建，由于默认没有使用 jcenter()，需要在相应 module 的 build.gradle 中配置

```groovy
repositories {
    mavenCentral()
    jcenter()
}
```

# 功能特点：

* 轻量级爬虫
* 约定大于配置：能够从配置文件中获取爬虫的配置信息
* 模块化设计，便于扩展：支持多种消息队列、多种网络框架，也支持自己实现。
* 多线程、异步：底层使用 RxJava 的多线程机制
* 支持 Kotlin 协程
* 支持JS渲染
* 支持分布式
* Request 支持自定义header信息
* Request 支持 debug 功能：在调试时可以使用 RxCache，从而避免多次请求同一个网页。
* 支持失败重试的机制
* 代理池的整合
* 支持UA池
* 支持Cookies池
* 支持深度抓取：能够在 Pipeline 中发起深度抓取的事件。
* 支持URL去重：使用布隆过滤器
* 超时控制：支持设置爬虫请求的超时时间；
* 爬虫的监控
* agent模块能够对当前服务器的CPU和内存进行实时监控

# 下载:

netdiscovery-core

```groovy
implementation 'com.cv4j.netdiscovery:netdiscovery-core:0.3.3'

```

netdiscovery-extra

```groovy
implementation 'com.cv4j.netdiscovery:netdiscovery-extra:0.3.3'
```

netdiscovery-selenium

```groovy
implementation 'com.cv4j.netdiscovery:netdiscovery-selenium:0.3.3'
```

netdiscovery-dsl

```groovy
implementation 'com.cv4j.netdiscovery:netdiscovery-dsl:0.3.3'
```

netdiscovery-coroutines

```groovy
implementation 'com.cv4j.netdiscovery:netdiscovery-coroutines:0.3.3'
```


# 详细功能

请查看[wiki](https://github.com/fengzhizi715/NetDiscovery/wiki)

# 案例:

* [user-agent-list](https://github.com/fengzhizi715/user-agent-list):抓取常用浏览器的user agent
* 在“Java与Android技术栈”公众号回复数字货币的关键字，获取最新的价格
![](images/spider_case1.jpeg)

![](images/spider_case2.jpeg)


# TODO List:

1. 增加从配置文件中获取爬虫的配置
2. 增强 HtmlUnit 模块
3. 从五个纬度控制爬取速度（Pipeline、Request、Domain、IP、Download_Delay），后期可支持交叉
4. 增加可开启的 http 缓存配置
5. 在同一个 Spider 里的链式爬取，默认附带上 Cookies 或者其他 Header 信息
6. 整合[cv4j](https://github.com/imageprocessor/cv4j)以及 Tesseract，实现 OCR 识别的功能

# Contributors：

* [bdqfork](https://github.com/bdqfork)
* [homchou](https://github.com/homchou)
* [sinkinka](https://github.com/sinkinka)

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


