package cn.netdiscovery.core.queue.disruptor;

import cn.netdiscovery.core.domain.Request;
import lombok.Data;

/**
 * Created by tony on 2018/9/1.
 */
@Data
public class RequestEvent {

    private Request request;

    public String toString() {

        return request.toString();
    }
}
