package cn.netdiscovery.core.registry;

/**
 * Created by tony on 2019-06-09.
 */
public class Provider {

    private String connectString;
    private String path;

    public String getConnectString() {
        return connectString;
    }

    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
