package com.cv4j.netdiscovery.extra.queue.redis;

import com.cv4j.netdiscovery.core.domain.Request;
import com.safframework.tony.common.utils.Preconditions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.resource.ClientResources;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.List;

/**
 * Created by tony on 2018/6/19.
 */
public class RedisPriorityQueue extends RedisQueue {

    private String ZSET_PREFIX = "zset_";

    private String QUEUE_PREFIX = "queue_";

    private String NORMAL_SUFFIX = "_normal";

    private String PRIORITY_SUFFIX = "_priority";

    public RedisPriorityQueue(String host) {
        super(host);
    }

    public RedisPriorityQueue(String host, ClientResources resources) {
        super(host, resources);
    }

    public RedisPriorityQueue(String host, int port,ClientResources resources) {
        super(host, port,resources);
    }

    public RedisPriorityQueue(RedisClient redisClient) {
        super(redisClient);
    }

    public RedisPriorityQueue(RedisClient redisClient,RedisKeyConfig redisKeyConfig) {
        super(redisClient,redisKeyConfig);

        if (redisKeyConfig!=null) { // 自定义 redis key 的前缀，以免跟自身业务的前缀不统一
            this.ZSET_PREFIX = redisKeyConfig.ZSET_PREFIX;
            this.QUEUE_PREFIX = redisKeyConfig.QUEUE_PREFIX;
            this.NORMAL_SUFFIX = redisKeyConfig.NORMAL_SUFFIX;
            this.PRIORITY_SUFFIX = redisKeyConfig.PRIORITY_SUFFIX;
        }
    }

    @Override
    protected void pushWhenNoDuplicate(Request request) {

        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> commands = connection.sync();

        try {
            if (request.getPriority() > 0) {
                commands.zadd(getZsetPriorityKey(request.getSpiderName()), request.getPriority(), request.getUrl());
            } else {
                commands.lpush(getQueueNormalKey(request.getSpiderName()), request.getUrl());
            }

            setExtrasInItem(commands, request);
        } finally {
            connection.close();
        }
    }

    @Override
    public synchronized Request poll(String spiderName) {
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> commands = connection.sync();
        try {
            String url = getRequest(commands, spiderName);
            if (Preconditions.isBlank(url)) {
                return null;
            }

            return getExtrasInItem(commands, url, spiderName);
        } finally {
            connection.close();
        }
    }

    private String getRequest(RedisCommands<String, String> commands, String spiderName) {

        String url;
        List<String> urls = commands.zrevrange(getZsetPriorityKey(spiderName), 0, 0);

        if (urls.isEmpty()) {
            url = commands.lpop(getQueueNormalKey(spiderName));
        } else {
            url = urls.toArray(new String[0])[0];
            commands.zrem(getZsetPriorityKey(spiderName), url);
        }

        return url;
    }

    private String getZsetPriorityKey(String spiderName) {
        return ZSET_PREFIX + spiderName + PRIORITY_SUFFIX;
    }

    private String getQueueNormalKey(String spiderName) {
        return QUEUE_PREFIX + spiderName + NORMAL_SUFFIX;
    }

    private void setExtrasInItem(RedisCommands<String, String> commands, Request request) {

        if (request.getExtras() != null) {
            String field = DigestUtils.shaHex(request.getUrl());
            String value = gson.toJson(request);
            commands.hset(getItemKey(request), field, value);
        }
    }

    private Request getExtrasInItem(RedisCommands<String, String> commands, String url, String spiderName) {

        String key = getItemKey(url);
        String field = DigestUtils.shaHex(url);
        String result = commands.hget(key, field);
        if (result != null) {
            return gson.fromJson(result, Request.class);
        }

        return new Request(url);
    }
}
