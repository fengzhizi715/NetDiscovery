package cn.netdiscovery.core.domain;

import lombok.Data;

/**
 * Created by tony on 2019-05-13.
 */
@Data
public class JobEntity {

    private String jobName;
    private String jobGroupName;
    private String triggerName;
    private String triggerGroupName;
    private String cron;
    private String spiderName;
    private String url;
}
