package com.cv4j.netdiscovery.core.pipeline;

import com.cv4j.netdiscovery.core.domain.ResultItems;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Created by tony on 2017/12/23.
 */
@Slf4j
public class ConsolePipeline implements Pipeline {

    @Override
    public void process(ResultItems resultItems) {

        log.info("get page: " + resultItems.getRequest().getUrl());
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            log.info(entry.getKey() + ":\t" + entry.getValue());
        }
    }
}
