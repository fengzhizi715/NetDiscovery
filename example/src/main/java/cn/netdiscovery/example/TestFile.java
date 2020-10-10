package cn.netdiscovery.example;

import cn.netdiscovery.core.Spider;
import cn.netdiscovery.core.downloader.file.FileDownloadAfterRequest;
import cn.netdiscovery.core.downloader.file.FileDownloader;

/**
 * Created by tony on 2018/7/21.
 */
public class TestFile {

    public static void main(String[] args) {

        Request request = new Request("http://down-www.7down.net/pcdown/soft/K/kotlinpdf.rar");
        request.afterRequest(new FileDownloadAfterRequest("test","test.rar")); // 在使用FileDownloader时，可以使用AfterRequest或者Pipeline对文件进行保存等处理。这里使用FileDownloadAfterRequest

        Spider.create().name("tony")
                .request(request)
                .downloader(new FileDownloader()) // 文件的下载需要使用FileDownloader
                .run();
    }
}
