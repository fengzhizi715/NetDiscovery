package com.cv4j.netdiscovery.extra.queue.redis;

import com.alibaba.fastjson.JSON;
import com.cv4j.netdiscovery.core.domain.Request;
import com.safframework.tony.common.utils.Preconditions;
import org.apache.commons.codec.digest.DigestUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;

/**
 * Created by tony on 2018/6/19.
 */
public class RedisPriorityQueue extends RedisQueue {

    private static final String ZSET_PREFIX = "zset_";

    private static final String QUEUE_PREFIX = "queue_";

    private static final String NO_PRIORITY_SUFFIX = "_zore";

    private static final String PRIORITY_SUFFIX = "_plus";

    public RedisPriorityQueue(String host) {
        super(host);
    }

    public RedisPriorityQueue(JedisPool pool) {
        super(pool);
    }

    @Override
    protected void pushWhenNoDuplicate(Request request) {

        Jedis jedis = pool.getResource();

        try {
            if (request.getPriority() > 0)
                jedis.zadd(getZsetPriorityKey(request.getSpiderName()), request.getPriority(), request.getUrl());
            else
                jedis.lpush(getQueueNoPriorityKey(request.getSpiderName()), request.getUrl());

            setExtrasInItem(jedis, request);
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public synchronized Request poll(String spiderName) {

        Jedis jedis = pool.getResource();

        try {
            String url = getRequest(jedis, spiderName);
            if (Preconditions.isBlank(url))
                return null;

            return getExtrasInItem(jedis, url, spiderName);
        } finally {
            pool.returnResource(jedis);
        }
    }

    private String getRequest(Jedis jedis, String spiderName) {

        String url;
        Set<String> urls = jedis.zrevrange(getZsetPriorityKey(spiderName), 0, 0);

        if (urls.isEmpty()) {
            url = jedis.lpop(getQueueNoPriorityKey(spiderName));
        } else {
            url = urls.toArray(new String[0])[0];
            jedis.zrem(getZsetPriorityKey(spiderName), url);
        }

        return url;
    }

    private String getZsetPriorityKey(String spiderName) {
        return ZSET_PREFIX + spiderName + PRIORITY_SUFFIX;
    }

    private String getQueueNoPriorityKey(String spiderName) {
        return QUEUE_PREFIX + spiderName + NO_PRIORITY_SUFFIX;
    }

    private void setExtrasInItem(Jedis jedis, Request request) {

        if (request.getExtras() != null) {
            String field = DigestUtils.shaHex(request.getUrl());
            String value = JSON.toJSONString(request);
            jedis.hset(getItemKey(request), field, value);
        }
    }

    private Request getExtrasInItem(Jedis jedis, String url, String spiderName) {

        String key = getItemKey(spiderName);
        String field = DigestUtils.shaHex(url);
        byte[] bytes = jedis.hget(key.getBytes(), field.getBytes());
        if (bytes != null)
            return JSON.parseObject(new String(bytes), Request.class);
        return new Request(url);
    }
}
