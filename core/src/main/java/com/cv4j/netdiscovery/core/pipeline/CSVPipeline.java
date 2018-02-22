package com.cv4j.netdiscovery.core.pipeline;

import com.cv4j.netdiscovery.core.domain.ResultItems;

/**
 * Created by tony on 2018/2/22.
 */
public class CSVPipeline implements Pipeline{

    String filePath;

    public CSVPipeline(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void process(ResultItems resultItems) {

    }
}
