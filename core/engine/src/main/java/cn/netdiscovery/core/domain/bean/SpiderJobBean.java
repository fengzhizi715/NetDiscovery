package cn.netdiscovery.core.domain.bean;

import cn.netdiscovery.core.domain.Request;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by tony on 2019-05-13.
 */
public class SpiderJobBean extends BaseJobBean {

    private String spiderName;
    private Request[] requests;

    public String getSpiderName() {
        return spiderName;
    }

    public void setSpiderName(String spiderName) {
        this.spiderName = spiderName;
    }

    public Request[] getRequests() {
        return requests;
    }

    public void setRequests(Request[] requests) {
        this.requests = requests;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SpiderJobBean that = (SpiderJobBean) o;
        return Objects.equals(spiderName, that.spiderName) &&
                Arrays.equals(requests, that.requests);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), spiderName);
        result = 31 * result + Arrays.hashCode(requests);
        return result;
    }
}
