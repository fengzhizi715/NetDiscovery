package com.cv4j.netdiscovery.core;

import com.cv4j.netdiscovery.core.pipeline.Pipeline;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by tony on 2017/12/22.
 */
public class Spider {

    private Set<Pipeline> pipelines = new LinkedHashSet<>();

    public Spider addPipeline(Pipeline pipeline) {

        this.pipelines.add(pipeline);
        return this;
    }

    public Spider clearPipeline() {

        this.pipelines.clear();
        return this;
    }
}
