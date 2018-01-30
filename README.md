# NetDiscovery

## NetDiscovery 功能点：
### 1.Spider功能

![](Spider.png)

### 2.SpiderEngine功能
![](SpiderEngine.png)


#### 2.1 获取某个爬虫的状态
http://localhost:{port}/netdiscovery/spider/{spiderName}

类型：GET

#### 2.2 获取SpiderEngine中所有爬虫的状态
http://localhost:{port}/netdiscovery/spiders/

类型：GET

#### 2.3 修改某个爬虫的状态
http://localhost:{port}/netdiscovery/spider/{spiderName}/status

类型：POST

参数说明：

```java
{
    "status":2   //让爬虫暂停
}
```

## NetDiscovery 基本原理：
### 1.基本原理
![](basic_principle.png)

### 2.集群原理
![](cluster_principle.png)

# TODO:
1. 增加对登录验证码的识别
2. 增加elasticsearch的支持
