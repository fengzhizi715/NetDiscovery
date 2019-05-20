package cn.netdiscovery.core.domain.bean;

import lombok.Data;

/**
 * Created by tony on 2019-05-13.
 */
@Data
public class SpiderJobBean extends BaseJobBean {

    private String spiderName;
    private String url;
}
