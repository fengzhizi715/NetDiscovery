package com.cv4j.netdiscovery.core.download.http;

import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.domain.Response;
import com.cv4j.netdiscovery.core.download.Downloader;
import io.reactivex.Maybe;

/**
 * Created by tony on 2018/1/20.
 */
public class AsynchttpclientDownloader implements Downloader{

    @Override
    public Maybe<Response> download(Request request) {
        return null;
    }

    @Override
    public void close() {

    }
}
