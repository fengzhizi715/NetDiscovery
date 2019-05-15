package cn.netdiscovery.core.domain.bean;

import lombok.Data;

/**
 * Created by tony on 2019-05-15.
 */
@Data
public class BaseJobBean {

    private String jobName;
    private String jobGroupName;
    private String triggerName;
    private String triggerGroupName;
    private String cron;
}
