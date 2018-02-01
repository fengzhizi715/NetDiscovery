package com.cv4j.netdiscovery.core.downloader;

import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.domain.Response;
import io.reactivex.Maybe;

import java.io.Closeable;

/**
 * Created by tony on 2017/12/23.
 */
public interface Downloader extends Closeable {

    Maybe<Response> download(Request request);
}
