package com.cv4j.netdiscovery.core.pipeline.debug;

import com.cv4j.netdiscovery.core.domain.ResultItems;
import com.cv4j.netdiscovery.core.pipeline.Pipeline;

/**
 * Created by tony on 2018/2/5.
 */
public class DebugPipeline extends Pipeline {

    public DebugPipeline() {
        this(0);
    }

    public DebugPipeline(int pipelineDelay) {
        super(pipelineDelay);
    }

    @Override
    public void process(ResultItems resultItems) {

        Printer.printJsonRequest(resultItems.getRequest());
    }
}
