package cn.netdiscovery.queue.redis;

import cn.netdiscovery.core.domain.Request;
import cn.netdiscovery.core.queue.AbstractQueue;
import cn.netdiscovery.core.queue.filter.DuplicateFilter;
import cn.netdiscovery.core.utils.SerializableUtils;
import com.safframework.tony.common.utils.Preconditions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.resource.ClientResources;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by tony on 2018/1/1.
 */
public class RedisQueue extends AbstractQueue implements DuplicateFilter {

    private String QUEUE_PREFIX = "queue_";

    private String SET_PREFIX = "set_";

    private String ITEM_PREFIX = "item_";

    protected RedisClient redisClient;

    public RedisQueue(String host) {
        this(host, null);
    }

    public RedisQueue(String host, ClientResources resources) {
        this(host,6379,resources);
    }

    public RedisQueue(String host, int port, ClientResources resources) {
        this(resources != null? RedisClient.create(resources, RedisURI.create(host, port)) : RedisClient.create(RedisURI.create(host, port)));
    }

    public RedisQueue(RedisClient redisClient) {

        this(redisClient,null);
    }

    public RedisQueue(RedisClient redisClient,RedisKeyConfig redisKeyConfig) {

        if (redisKeyConfig!=null) { // 自定义 redis key 的前缀，以免跟自身业务的前缀不统一

            this.QUEUE_PREFIX = redisKeyConfig.QUEUE_PREFIX;
            this.SET_PREFIX = redisKeyConfig.SET_PREFIX;
            this.ITEM_PREFIX = redisKeyConfig.ITEM_PREFIX;
        }

        this.redisClient = redisClient;
        setFilter(this);
    }

    @Override
    public boolean isDuplicate(Request request) {
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> commands = connection.sync();

        try {
            if (request.getCheckDuplicate()) {
                return commands.sadd(getSetKey(request), request.getUrl()) == 0;
            } else {
                commands.sadd(getSetKey(request), request.getUrl());
                return false;
            }
        } finally {
            connection.close();
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
                String value = SerializableUtils.toJson(request);
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

        return Preconditions.isNotBlank(request.getHeader())
                || Preconditions.isNotBlank(request.getCharset())
                || Preconditions.isNotBlank(request.getExtras())
                || request.getPriority() > 0;
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

                return SerializableUtils.fromJson(result, Request.class);
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
