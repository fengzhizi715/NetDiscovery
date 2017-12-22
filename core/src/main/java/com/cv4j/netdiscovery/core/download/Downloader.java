package com.cv4j.netdiscovery.core.download;

import com.cv4j.netdiscovery.core.domain.Page;
import com.cv4j.netdiscovery.core.http.Request;

/**
 * Created by tony on 2017/12/23.
 */
public interface Downloader {

    Page download(Request request);
}
