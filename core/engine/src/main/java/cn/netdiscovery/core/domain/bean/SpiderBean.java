package cn.netdiscovery.core.domain.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by tony on 2018/1/15.
 */
@Data
public class SpiderBean implements Serializable {

    private String spiderName;
    private int spiderStatus;
    private int leftRequestSize;     // 剩余的请求数
    private int totalRequestSize;    // 总共的请求数
    private int consumedRequestSize; // 已消费的请求数
    private String queueType;
    private String downloaderType;
}
