package cn.netdiscovery.core.domain.bean;

/**
 * Created by tony on 2019-06-01.
 */
public class MonitorBean {

    public String ip;
    public String port;
    public String state;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
