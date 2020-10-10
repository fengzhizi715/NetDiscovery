package cn.netdiscovery.core.pipeline;

import cn.netdiscovery.core.domain.ResultItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by tony on 2017/12/23.
 */
public class ConsolePipeline extends Pipeline {

    private Logger log = LoggerFactory.getLogger(ConsolePipeline.class);

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
