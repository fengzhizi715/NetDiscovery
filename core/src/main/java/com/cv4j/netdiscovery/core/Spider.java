package com.cv4j.netdiscovery.core;

import com.cv4j.netdiscovery.core.http.Request;
import com.cv4j.netdiscovery.core.pipeline.Pipeline;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by tony on 2017/12/22.
 */
public class Spider {

    public final static int SPIDER_STATUS_INIT = 0;
    public final static int SPIDER_STATUS_RUNNING = 1;
    public final static int SPIDER_STATUS_STOPPED = 2;

    @Getter
    private String name;// 爬虫的名字

    private Request request;

    private Set<Pipeline> pipelines = new LinkedHashSet<>();

    public static Spider create() {

        return new Spider();
    }

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

    public Spider request(Request request) {

        this.request = request;
        return this;
    }
}
