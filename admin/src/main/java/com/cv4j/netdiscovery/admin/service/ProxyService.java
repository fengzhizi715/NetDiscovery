package com.cv4j.netdiscovery.admin.service;

import com.cv4j.proxy.domain.Proxy;

public interface ProxyService {

	boolean runAllJobs();

    boolean checkProxy(Proxy proxy);

}
