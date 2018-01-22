package com.cv4j.netdiscovery.extra.queue;

import com.alibaba.fastjson.JSON;
import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.queue.AbstractQueue;
import com.cv4j.netdiscovery.core.queue.filter.DuplicateFilter;
import org.apache.commons.codec.digest.DigestUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by tony on 2018/1/1.
 */
public class RedisQueue extends AbstractQueue implements DuplicateFilter{

    private static final String QUEUE_PREFIX = "queue_";

    private static final String SET_PREFIX = "set_";

    private static final String ITEM_PREFIX = "item_";

    protected JedisPool pool;

    public RedisQueue(String host) {

        this(new JedisPool(new JedisPoolConfig(), host));
    }

    public RedisQueue(JedisPool pool) {

        this.pool = pool;
        setFilter(this);
    }

    @Override
    public boolean isDuplicate(Request request) {

        if (request.isCheckDuplicate()) {

            Jedis jedis = pool.getResource();
            try {
                return jedis.sadd(getSetKey(request), request.getUrl()) == 0;
            } finally {
                pool.returnResource(jedis);
            }
        } else {

            Jedis jedis = pool.getResource();
            try {
                jedis.sadd(getSetKey(request), request.getUrl());
            } finally {
                pool.returnResource(jedis);
            }

            return false;
        }

    }

    @Override
    protected void pushWhenNoDuplicate(Request request) {

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

    @Override
    public int getLeftRequests(String spiderName) {

        Jedis jedis = pool.getResource();
        try {
            Long size = jedis.llen(getQueueKey(spiderName));
            return size.intValue();
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public int getTotalRequests(String spiderName) {
        Jedis jedis = pool.getResource();
        try {
            Long size = jedis.scard(getSetKey(spiderName));
            return size.intValue();
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public int getTotalRequestsCount() {
        return 0;
    }

    protected String getQueueKey(String spiderName) {
        return QUEUE_PREFIX + spiderName;
    }

    protected String getSetKey(Request request) {
        return SET_PREFIX + request.getSpiderName();
    }

    protected String getSetKey(String spiderName) {
        return SET_PREFIX + spiderName;
    }
}
