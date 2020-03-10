package cn.netdiscovery.core.domain.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by tony on 2019-05-15.
 */
@Data
public class BaseJobBean implements Serializable {

    private String jobName;
    private String jobGroupName;
    private String triggerName;
    private String triggerGroupName;
    private String cron;
}
