package com.cv4j.netdiscovery.extra.pipeline;

import com.cv4j.netdiscovery.core.domain.ResultItems;
import com.cv4j.netdiscovery.core.pipeline.Pipeline;
import com.cv4j.netdiscovery.core.utils.VertxUtils;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisStringReactiveCommands;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Created by tony on 2018/3/22.
 */
@Slf4j
public class RedisPipeline implements Pipeline {

    private RedisClient redisClient;
    private String key;

    public RedisPipeline(RedisClient redisClient, String key) {

        this.redisClient = redisClient;
        this.key = key;
    }

    public RedisPipeline(RedisURI redisURI, String key) {

        this.redisClient = RedisClient.create(redisURI);
        this.key = key;
    }

    @Override
    public void process(ResultItems resultItems) {

        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {

            jsonObject.put(entry.getKey(), entry.getValue());
        }
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisStringReactiveCommands<String, String> commands = connection.reactive();
        commands.set(key, jsonObject.toString())
                .doFinally(signalType -> connection.close())
                .subscribe(res -> log.info(String.format("saved key %s success!", key)),
                        error -> error.getCause().printStackTrace());
    }
}
