package cn.netdiscovery.pipeline.elasticsearch;

import cn.netdiscovery.core.domain.ResultItems;
import cn.netdiscovery.core.pipeline.Pipeline;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bdq
 * @date 2018-12-26
 */
public class ElasticSearchPipline extends Pipeline {

    private Logger log = LoggerFactory.getLogger(ElasticSearchPipline.class);

    private TransportClient client;
    private String index;
    private String type;

    public ElasticSearchPipline(TransportClient client, String index, String type) {

        this(client,index,type,0);
    }

    public ElasticSearchPipline(TransportClient client, String index, String type, long pipelineDelay) {

        super(pipelineDelay);
        this.client = client;
        this.index = index;
        this.type = type;
    }

    @Override
    public void process(ResultItems resultItems) {
        IndexResponse response = client.prepareIndex(index, type)
                .setSource(resultItems.getAll())
                .get();
        RestStatus restStatus = response.status();
        if (RestStatus.CREATED == restStatus) {
            log.info("key {} have already created!", response.getId());
        } else {
            log.error("an error occured with result id {}!", restStatus.getStatus());
        }
    }
}
