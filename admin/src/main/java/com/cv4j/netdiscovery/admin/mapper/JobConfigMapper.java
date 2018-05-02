package com.cv4j.netdiscovery.admin.mapper;

import com.cv4j.netdiscovery.admin.domain.JobConfig;
import com.cv4j.netdiscovery.admin.domain.JobResource;
import com.cv4j.netdiscovery.admin.domain.SysOption;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface JobConfigMapper {
    @Insert("INSERT INTO biz_job_config(resource_name,job_classpath,cron_expression,job_name,job_group,trigger_name,trigger_group,remark,create_time,update_time) " +
            "VALUES(#{resourceName},#{jobClassPath},#{cronExpression},#{jobName},#{jobGroupName},#{triggerName},#{triggerGroupName},#{remark},#{createTime},#{updateTime})")
    int insertJobConfig(JobConfig jobConfig);

    @Update("UPDATE biz_job_config SET resource_name=#{resourceName},job_classpath=#{jobClassPath},cron_expression=#{cronExpression}, job_name=#{jobName}, " +
            "remark=#{remark}, update_time=#{updateTime} WHERE id=#{primaryId}")
    int updateJobConfigByPrimaryKey(JobConfig jobConfig);

    @Delete("DELETE FROM biz_job_config WHERE id=#{primaryId}")
    int deleteJobConfigByPrimaryKey(@Param("primaryId") Integer primaryId);

    @Select("SELECT * FROM biz_job_config WHERE job_name=#{jobName}")
    @ResultMap("jobConfigMap")
    List<JobConfig> selectJobByName(@Param("jobName") String jobName);

    @SelectProvider(type=SelectProviderForSql.class, method="getSqlForSelectJobConfigs")
    @Results(id="jobConfigMap", value={
            @Result(property = "primaryId", column = "id")
            ,@Result(property = "resourceName", column = "resource_name")
            ,@Result(property = "jobClassPath", column = "job_classpath")
            ,@Result(property = "cronExpression", column = "cron_expression")
            ,@Result(property = "jobName", column = "job_name")
            ,@Result(property = "jobGroupName", column = "job_group")
            ,@Result(property = "triggerName", column = "trigger_name")
            ,@Result(property = "triggerGroupName", column = "trigger_group")
            ,@Result(property = "remark", column = "remark")
            ,@Result(property = "state", column = "state")
            ,@Result(property = "createTime", column = "create_time")
            ,@Result(property = "updateTime", column = "update_time")
    })
    List<JobConfig> selectJobConfigs();

}
