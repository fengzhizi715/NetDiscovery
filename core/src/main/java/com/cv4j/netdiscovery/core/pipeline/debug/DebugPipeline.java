package com.cv4j.netdiscovery.core.pipeline.debug;

import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.domain.ResultItems;
import com.cv4j.netdiscovery.core.pipeline.Pipeline;

/**
 * Created by tony on 2018/2/5.
 */
public class DebugPipeline implements Pipeline {

    @Override
    public void process(ResultItems resultItems) {

        Printer.printJsonRequest(resultItems.getRequest());
    }
}
