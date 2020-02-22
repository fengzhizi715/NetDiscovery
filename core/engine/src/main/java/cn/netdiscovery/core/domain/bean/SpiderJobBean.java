package cn.netdiscovery.core.domain.bean;

import cn.netdiscovery.core.domain.Request;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by tony on 2019-05-13.
 */
@EqualsAndHashCode(callSuper=false)
@Data
public class SpiderJobBean extends BaseJobBean {

    private String spiderName;
    private Request[] requests;
}
