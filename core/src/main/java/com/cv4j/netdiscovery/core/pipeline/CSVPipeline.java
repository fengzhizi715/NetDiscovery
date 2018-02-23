package com.cv4j.netdiscovery.core.pipeline;

import com.cv4j.netdiscovery.core.domain.ResultItems;
import com.cv4j.netdiscovery.core.utils.Utils;
import com.safframework.tony.common.utils.Preconditions;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by tony on 2018/2/22.
 */
public class CSVPipeline implements Pipeline{

    private File csvFile = null;

    public CSVPipeline(String filePath,String fileName) {

        csvFile = new File(filePath + fileName + ".csv");

        if (!csvFile.exists()) {

            File parent = csvFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try {
                csvFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void process(ResultItems resultItems) {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {

            sb.append(entry.getValue()).append(",");
        }

        String[] ss = sb.toString().split(",");

        if (Preconditions.isNotBlank(ss)) {

            List<String> dataList = Arrays.asList(ss);
            Utils.exportCsv(csvFile,dataList);
        }
    }


}
