package com.cv4j.netdiscovery.extra.pipeline;

import com.cv4j.netdiscovery.core.domain.ResultItems;
import com.cv4j.netdiscovery.core.pipeline.Pipeline;
import com.cv4j.netdiscovery.core.utils.VertxUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
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

    public RedisPipeline(RedisOptions redisOptions, String key) {

        this.redisClient = RedisClient.create(VertxUtils.getVertx(), redisOptions);
        this.key = key;
    }

    @Override
    public void process(ResultItems resultItems) {

        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {

            jsonObject.put(entry.getKey(), entry.getValue());
        }

        redisClient.setBinary(key, jsonObject.toBuffer(), res -> {
            
            if (res.succeeded()) {

                log.info(String.format("saved key %s success!", key));
            } else {
                res.cause().printStackTrace();
            }
        });
    }
}
