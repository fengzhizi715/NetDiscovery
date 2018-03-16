package com.cv4j.netdiscovery.core.downloader.file;

import com.cv4j.netdiscovery.core.config.Constant;
import com.cv4j.netdiscovery.core.domain.Page;
import com.cv4j.netdiscovery.core.domain.Request;
import com.safframework.tony.common.utils.IOUtils;

import java.io.*;

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

            try {
                // 创建保存文件的目录
                File savePath = new File(filePath);
                if (!savePath.exists()) {
                    savePath.mkdir();
                }
                // 创建保存的文件
                File file = new File(savePath + "/" + fileName);
                if (file != null && !file.exists()) {
                    file.createNewFile();
                }

                IOUtils.writeToFile(is,file);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
    }
}
