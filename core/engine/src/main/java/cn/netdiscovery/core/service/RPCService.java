package cn.netdiscovery.core.service;

import cn.netdiscovery.core.domain.bean.SpiderBean;
import cn.netdiscovery.core.domain.bean.SpiderJobBean;

import java.util.List;

/**
 * SpiderEngine 对外提供的 rpc 接口
 * @FileName: cn.netdiscovery.core.service.RPCService
 * @author: Tony Shen
 * @date: 2020-03-10 11:18
 * @version: V1.0 <描述当前版本功能>
 */
public interface RPCService {

    /**
     * 根据爬虫的名称获取爬虫的详情
     * @param spiderName
     * @return
     */
    SpiderBean detail(String  spiderName);

    /**
     * 获取容器下所有爬虫的信息
     * @return
     */
    List<SpiderBean> spiders();

    /**
     * 修改某个爬虫的状态
     * @param spiderName
     * @param status
     */
    void status(String spiderName,int status);

    /**
     * 添加url任务到爬虫引擎，用于构建某个爬虫的任务
     * @param spiderName
     * @param url
     */
    void push(String spiderName, String url);

    /**
     * 获取容器内所有爬虫的定时任务
     * @return
     */
    List<SpiderJobBean> jobs();
}
