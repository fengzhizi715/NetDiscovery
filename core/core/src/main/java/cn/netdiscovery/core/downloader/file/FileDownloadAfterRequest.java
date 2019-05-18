package cn.netdiscovery.core.downloader.file;

import cn.netdiscovery.core.config.Constant;
import cn.netdiscovery.core.domain.Page;
import cn.netdiscovery.core.domain.Request;
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

            try {
                // 创建保存文件的目录
                File dir = new File(filePath);
                if (!dir.exists() && dir.isDirectory()) {
                    dir.mkdirs();
                }
                // 创建保存的文件
                File file = new File(filePath + File.separator + fileName);

                IOUtils.writeToFile(is,file);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
    }
}
