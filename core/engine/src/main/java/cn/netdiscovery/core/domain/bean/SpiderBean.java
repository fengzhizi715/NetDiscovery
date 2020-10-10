package cn.netdiscovery.core.domain.bean;

import java.io.Serializable;

/**
 * Created by tony on 2018/1/15.
 */
public class SpiderBean implements Serializable {

    private String spiderName;
    private int spiderStatus;
    private int leftRequestSize;     // 剩余的请求数
    private int totalRequestSize;    // 总共的请求数
    private int consumedRequestSize; // 已消费的请求数
    private String queueType;
    private String downloaderType;

    public String getSpiderName() {
        return spiderName;
    }

    public void setSpiderName(String spiderName) {
        this.spiderName = spiderName;
    }

    public int getSpiderStatus() {
        return spiderStatus;
    }

    public void setSpiderStatus(int spiderStatus) {
        this.spiderStatus = spiderStatus;
    }

    public int getLeftRequestSize() {
        return leftRequestSize;
    }

    public void setLeftRequestSize(int leftRequestSize) {
        this.leftRequestSize = leftRequestSize;
    }

    public int getTotalRequestSize() {
        return totalRequestSize;
    }

    public void setTotalRequestSize(int totalRequestSize) {
        this.totalRequestSize = totalRequestSize;
    }

    public int getConsumedRequestSize() {
        return consumedRequestSize;
    }

    public void setConsumedRequestSize(int consumedRequestSize) {
        this.consumedRequestSize = consumedRequestSize;
    }

    public String getQueueType() {
        return queueType;
    }

    public void setQueueType(String queueType) {
        this.queueType = queueType;
    }

    public String getDownloaderType() {
        return downloaderType;
    }

    public void setDownloaderType(String downloaderType) {
        this.downloaderType = downloaderType;
    }
}
