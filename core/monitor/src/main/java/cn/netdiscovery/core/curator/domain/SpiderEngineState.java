package cn.netdiscovery.core.curator.domain;

/**
 * Created by tony on 2019-06-01.
 */
public enum SpiderEngineState {

    ONLINE("online"),
    OFFLINE("offline");

    private String state;

    SpiderEngineState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
