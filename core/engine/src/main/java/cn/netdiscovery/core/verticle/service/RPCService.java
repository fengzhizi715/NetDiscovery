package cn.netdiscovery.core.verticle.service;

import cn.netdiscovery.core.domain.bean.SpiderBean;

/**
 * @FileName: cn.netdiscovery.core.verticle.service.RPCService
 * @author: Tony Shen
 * @date: 2020-03-10 11:18
 * @version: V1.0 <描述当前版本功能>
 */
public interface RPCService {

    SpiderBean detail(String  spiderName);
}
