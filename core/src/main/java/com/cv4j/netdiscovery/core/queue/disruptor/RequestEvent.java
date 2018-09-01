package com.cv4j.netdiscovery.core.queue.disruptor;

import com.cv4j.netdiscovery.core.domain.Request;
import lombok.Data;

/**
 * Created by tony on 2018/9/1.
 */
@Data
public class RequestEvent {

    private Request request;
}
