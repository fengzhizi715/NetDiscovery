package com.cv4j.netdiscovery.core;

import com.cv4j.netdiscovery.core.pipeline.Pipeline;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tony on 2017/12/22.
 */
public class Spider {

    private List<Pipeline> pipelineList = new ArrayList<>();

    public Spider addPipeline(Pipeline pipeline) {

        pipelineList.add(pipeline);
        return this;
    }
}
