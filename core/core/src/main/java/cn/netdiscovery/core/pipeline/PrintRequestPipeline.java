package cn.netdiscovery.core.pipeline;

import cn.netdiscovery.core.domain.Request;
import cn.netdiscovery.core.domain.ResultItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by tony on 2019-05-29.
 */
public class PrintRequestPipeline extends Pipeline {

    private Logger log = LoggerFactory.getLogger(PrintRequestPipeline.class);

    @Override
    public void process(ResultItems resultItems) {

        Request request = resultItems.getRequest();

        if(request!=null) {
            log.info(request.toString());
        }
    }
}
