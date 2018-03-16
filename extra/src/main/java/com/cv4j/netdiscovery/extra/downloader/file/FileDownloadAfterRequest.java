package com.cv4j.netdiscovery.extra.downloader.file;

import com.cv4j.netdiscovery.core.config.Constant;
import com.cv4j.netdiscovery.core.domain.Page;
import com.cv4j.netdiscovery.core.domain.Request;
import com.safframework.tony.common.utils.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by tony on 2018/3/12.
 */
public class FileDownloadAfterRequest implements Request.AfterRequest {

    private String filePath;
    private String fileName;

    public FileDownloadAfterRequest(String filePath,String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
    }

    @Override
    public void process(Page page) {

        InputStream is = page.getResultItems().get(Constant.RESPONSE_RAW);
        if (is!=null) {

            // 创建文件对象
            File f = new File(filePath+fileName);
            // 创建文件路径
            if (!f.getParentFile().exists())
                f.getParentFile().mkdirs();

            try {
                IOUtils.writeToFile(is,f);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                IOUtils.closeQuietly(is);
            }
        }
    }
}
