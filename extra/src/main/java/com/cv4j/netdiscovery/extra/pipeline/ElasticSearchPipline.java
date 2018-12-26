package com.cv4j.netdiscovery.extra.pipeline;

import com.cv4j.netdiscovery.core.domain.ResultItems;
import com.cv4j.netdiscovery.core.pipeline.Pipeline;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.rest.RestStatus;

/**
 * @author bdq
 * @date 2018-12-26
 */
@Slf4j
public class ElasticSearchPipline implements Pipeline {
    private TransportClient client;
    private String index;
    private String type;

    public ElasticSearchPipline(TransportClient client, String index, String type) {
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
        if (restStatus == RestStatus.CREATED) {
            log.info("key {} have already created!", response.getId());
        } else {
            log.error("an error occured with result id {}!", restStatus.getStatus());
        }
    }
}
