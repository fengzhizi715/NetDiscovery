package cn.netdiscovery.core.pipeline;

import cn.netdiscovery.core.domain.Request;
import cn.netdiscovery.core.domain.ResultItems;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by tony on 2019-05-29.
 */
@Slf4j
public class PrintRequestPipeline extends Pipeline {

    @Override
    public void process(ResultItems resultItems) {

        Request request = resultItems.getRequest();

        log.info(request.toString());
    }
}
