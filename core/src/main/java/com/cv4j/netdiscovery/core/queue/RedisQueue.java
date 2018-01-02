package com.cv4j.netdiscovery.core.queue;

import com.alibaba.fastjson.JSON;
import com.cv4j.netdiscovery.core.http.Request;
import org.apache.commons.codec.digest.DigestUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by tony on 2018/1/1.
 */
public class RedisQueue implements Queue{

    private static final String QUEUE_PREFIX = "queue_";

    private static final String SET_PREFIX = "set_";

    private static final String ITEM_PREFIX = "item_";

    protected JedisPool pool;

    public RedisQueue(String host) {

        this(new JedisPool(new JedisPoolConfig(), host));
    }

    public RedisQueue(JedisPool pool) {

        this.pool = pool;
    }

    @Override
    public void push(Request request) {

        Jedis jedis = pool.getResource();
        try {
            jedis.rpush(getQueueKey(request.getSpiderName()), request.getUrl());
            String field = DigestUtils.shaHex(request.getUrl());
            String value = JSON.toJSONString(request);
            jedis.hset((ITEM_PREFIX + request.getUrl()), field, value);
        } finally {
            jedis.close();
        }
    }

    @Override
    public synchronized Request poll(String spiderName) {

        Jedis jedis = pool.getResource();
        try {
            String url = jedis.lpop(getQueueKey(spiderName));
            if (url == null) {
                return null;
            }

            String key = ITEM_PREFIX + url;
            String field = DigestUtils.shaHex(url);
            byte[] bytes = jedis.hget(key.getBytes(), field.getBytes());
            if (bytes != null) {
                Request o = JSON.parseObject(new String(bytes), Request.class);
                return o;
            }

            Request request = new Request(url);
            return request;
        } finally {
            pool.returnResource(jedis);
        }
    }

    protected String getQueueKey(String spiderName) {
        return QUEUE_PREFIX + spiderName;
    }
}
