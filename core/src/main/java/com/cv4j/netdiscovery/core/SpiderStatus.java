package com.cv4j.netdiscovery.core;

/**
 * Created by tony on 2018/1/27.
 */
public enum SpiderStatus {

    INIT("初始化", 0), RUNNING("运行", 1), PAUSE("暂停", 2), RESUME("重新开始", 3),STOPPED("停止",4);

    private String statusName ;
    private int index ;

    SpiderStatus( String statusName , int index ){
        this.statusName = statusName ;
        this.index = index ;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
