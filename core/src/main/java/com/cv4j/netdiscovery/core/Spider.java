package com.cv4j.netdiscovery.core;

import com.cv4j.netdiscovery.core.pipeline.Pipeline;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by tony on 2017/12/22.
 */
public class Spider {

    public final static int SPIDER_STAT_INIT = 0;
    public final static int SPIDER_STAT_RUNNING = 1;
    public final static int SPIDER_STAT_STOPPED = 2;

    private String name;
    private Set<Pipeline> pipelines = new LinkedHashSet<>();

    public Spider name(String name) {

        this.name = name;
        return this;
    }

    public Spider pipeline(Pipeline pipeline) {

        this.pipelines.add(pipeline);
        return this;
    }

    public Spider clearPipeline() {

        this.pipelines.clear();
        return this;
    }
}
