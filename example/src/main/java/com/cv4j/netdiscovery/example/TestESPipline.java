package com.cv4j.netdiscovery.example;

import com.cv4j.netdiscovery.core.domain.ResultItems;
import com.cv4j.netdiscovery.extra.pipeline.ElasticSearchPipline;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * @author bdq
 * @date 2018-12-26
 */
public class TestESPipline {
    public static void main(String[] args) throws UnknownHostException {
        Settings settings = Settings.builder()
                .put("cluster.name", "docker-cluster").build();
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
        ResultItems resultItems = new ResultItems();
        resultItems.put("test", 1);
        new ElasticSearchPipline(client, "test", "_doc").process(resultItems);
    }
}
