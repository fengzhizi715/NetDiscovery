package cn.netdiscovery.core.pipeline.debug;

import cn.netdiscovery.core.domain.ResultItems;
import cn.netdiscovery.core.pipeline.Pipeline;

/**
 * Created by tony on 2018/2/5.
 */
public class DebugPipeline extends Pipeline {

    public DebugPipeline() {
        this(0);
    }

    public DebugPipeline(long pipelineDelay) {
        super(pipelineDelay);
    }

    @Override
    public void process(ResultItems resultItems) {

        Printer.printJsonRequest(resultItems.getRequest());
    }
}
