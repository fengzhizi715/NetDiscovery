package cn.netdiscovery.core.pipeline;

import cn.netdiscovery.core.domain.ResultItems;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by tony on 2017/12/23.
 */
@Slf4j
public class ConsolePipeline extends Pipeline {

    public ConsolePipeline() {
        this(0);
    }

    public ConsolePipeline(long pipelineDelay) {
        super(pipelineDelay);
    }

    @Override
    public void process(ResultItems resultItems) {

        log.info("get page: " + resultItems.getRequest().getUrl());

        resultItems.getAll().forEach((key,value)->{
            log.info(key + ":\t" + value);
        });
    }
}
