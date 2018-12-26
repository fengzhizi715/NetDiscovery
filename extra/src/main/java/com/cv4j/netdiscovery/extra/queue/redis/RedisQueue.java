package com.cv4j.netdiscovery.extra.queue.redis;

import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.queue.AbstractQueue;
import com.cv4j.netdiscovery.core.queue.filter.DuplicateFilter;
import com.google.gson.Gson;
import com.safframework.tony.common.utils.Preconditions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisHashReactiveCommands;
import io.lettuce.core.api.reactive.RedisListReactiveCommands;
import io.lettuce.core.api.reactive.RedisSetReactiveCommands;
import io.lettuce.core.api.reactive.RedisStringReactiveCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.resource.ClientResources;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by tony on 2018/1/1.
 */
public class RedisQueue extends AbstractQueue implements DuplicateFilter {

    private static final String QUEUE_PREFIX = "queue_";

    private static final String SET_PREFIX = "set_";

    private static final String ITEM_PREFIX = "item_";

    protected RedisClient redisClient;

    protected Gson gson = new Gson();

    public RedisQueue(String host) {
        this(host, null);
    }

    public RedisQueue(String host, ClientResources resources) {
        if (resources != null) {
            this.redisClient = RedisClient.create(resources, RedisURI.create(host, 6379));
        } else {
            this.redisClient = RedisClient.create(RedisURI.create(host, 6379));
        }

    }

    public RedisQueue(RedisClient redisClient) {

        this.redisClient = redisClient;
        setFilter(this);
    }

    @Override
    public boolean isDuplicate(Request request) {
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> commands = connection.sync();
        if (request.isCheckDuplicate()) {
            try {
                return commands.sadd(getSetKey(request), request.getUrl()) == 0;
            } finally {
                connection.close();
            }
        } else {

            try {
                commands.sadd(getSetKey(request), request.getUrl());
            } finally {
                connection.close();
            }

            return false;
        }

    }

    @Override
    protected void pushWhenNoDuplicate(Request request) {
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> commands = connection.sync();
        try {
            commands.lpush(request.getSpiderName(), request.getUrl());

            if (hasExtraRequestInfo(request)) {
                String field = DigestUtils.sha1Hex(request.getUrl());
                String value = gson.toJson(request);
                commands.hset((ITEM_PREFIX + request.getUrl()), field, value);
            }

        } finally {
            connection.close();
        }

    }

    private boolean hasExtraRequestInfo(Request request) {

        if (request == null) {
            return false;
        }

        if (Preconditions.isNotBlank(request.getHeader())) {
            return true;
        }

        if (Preconditions.isNotBlank(request.getCharset())) {
            return true;
        }

        if (Preconditions.isNotBlank(request.getExtras())) {
            return true;
        }

        if (request.getPriority() > 0) {
            return true;
        }

        return false;
    }

    @Override
    public synchronized Request poll(String spiderName) {
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> commands = connection.sync();
        try {
            String url = commands.lpop(getQueueKey(spiderName));
            if (url == null) {
                return null;
            }

            String key = ITEM_PREFIX + url;
            String field = DigestUtils.sha1Hex(url);
            String result = commands.hget(key, field);

            if (result != null) {

                return gson.fromJson(result, Request.class);
            }

            return new Request(url);
        } finally {
            connection.close();
        }
    }

    @Override
    public int getLeftRequests(String spiderName) {
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> commands = connection.sync();
        try {
            Long size = commands.llen(getQueueKey(spiderName));
            return size.intValue();
        } finally {
            connection.close();
        }
    }

    @Override
    public int getTotalRequests(String spiderName) {
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> commands = connection.sync();
        try {
            Long size = commands.scard(getSetKey(spiderName));
            return size.intValue();
        } finally {
            connection.close();
        }
    }

    /**
     * RedisQueue 无须使用该方法来获取Queue中总共的Request数量，所以返回0
     *
     * @return
     */
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

    protected String getItemKey(Request request) {
        return ITEM_PREFIX + request.getUrl();
    }

    protected String getItemKey(String url) {
        return ITEM_PREFIX + url;
    }
}
