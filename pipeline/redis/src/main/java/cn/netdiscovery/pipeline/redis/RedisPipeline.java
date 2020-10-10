package cn.netdiscovery.pipeline.redis;

import cn.netdiscovery.core.domain.ResultItems;
import cn.netdiscovery.core.pipeline.Pipeline;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisStringReactiveCommands;
import io.lettuce.core.resource.ClientResources;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by tony on 2018/3/22.
 */
public class RedisPipeline extends Pipeline {

    private Logger log = LoggerFactory.getLogger(RedisPipeline.class);

    private RedisClient redisClient;
    private String key;

    public RedisPipeline(RedisClient redisClient, String key) {

        this(redisClient,key,0);
    }

    public RedisPipeline(RedisClient redisClient, String key, long pipelineDelay) {

        super(pipelineDelay);
        this.redisClient = redisClient;
        this.key = key;
    }

    public RedisPipeline(RedisURI redisURI, String key) {

        this(null, redisURI, key);
    }

    public RedisPipeline(ClientResources resources, RedisURI redisURI, String key) {

        this(resources,redisURI,key,0);
    }

    public RedisPipeline(ClientResources resources, RedisURI redisURI, String key, long pipelineDelay) {

        super(pipelineDelay);
        if (null != resources) {
            this.redisClient = RedisClient.create(resources, redisURI);
        } else {
            this.redisClient = RedisClient.create(redisURI);
        }
        this.key = key;
    }

    @Override
    public void process(ResultItems resultItems) {

        JsonObject jsonObject = new JsonObject();
        resultItems.getAll().forEach(jsonObject::put);

        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisStringReactiveCommands<String, String> commands = connection.reactive();
        commands.set(key, jsonObject.toString())
                .doFinally(signalType -> connection.close())
                .subscribe(res -> log.info(String.format("saved key %s success!", key)),
                        error -> error.getCause().printStackTrace());
    }
}
