package com.cv4j.netdiscovery.example;

import com.cv4j.netdiscovery.core.Spider;
import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.downloader.file.FileDownloadAfterRequest;
import com.cv4j.netdiscovery.core.downloader.file.FileDownloader;

/**
 * Created by tony on 2018/7/21.
 */
public class TestFile {

    public static void main(String[] args) {

        Request request = new Request("http://down-www.7down.net/pcdown/soft/K/kotlinpdf.rar");
        request.afterRequest(new FileDownloadAfterRequest("test","test.rar"));

        Spider.create().name("tony")
                .request(request)
                .downloader(new FileDownloader())
                .run();
    }
}
