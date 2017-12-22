package com.cv4j.netdiscovery.core.pipeline;

import com.cv4j.netdiscovery.core.domain.ResultItems;

import java.util.Map;

/**
 * Created by tony on 2017/12/23.
 */
public class ConsolePipeline implements Pipeline {

    @Override
    public void process(ResultItems resultItems) {
        System.out.println("get page: " + resultItems.getRequest().getUrl());
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            System.out.println(entry.getKey() + ":\t" + entry.getValue());
        }
    }
}
