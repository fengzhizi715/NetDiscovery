package com.cv4j.netdiscovery.admin.mapper;

import com.cv4j.netdiscovery.admin.domain.JobConfigModel;
import com.cv4j.netdiscovery.admin.domain.JobModel;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface JobMapper {

    @Select("SELECT * FROM biz_job_config WHERE id=#{primaryId}")
    @Results(id="jobConfigMap", value={
            @Result(property = "primaryId", column = "id")
            ,@Result(property = "jobName", column = "job_name")
            ,@Result(property = "jobClass", column = "job_class")
            ,@Result(property = "parserClass", column = "parser_class")
            ,@Result(property = "urlPrefix", column = "url_prefix")
            ,@Result(property = "urlSuffix", column = "url_suffix")
            ,@Result(property = "startPage", column = "start_page")
            ,@Result(property = "endPage", column = "end_page")
            ,@Result(property = "createTime", column = "create_time")
            ,@Result(property = "updateTime", column = "update_time")
    })
    JobConfigModel selectJobConfig(@Param("primaryId") Integer primaryId);

    @Select("SELECT * FROM biz_job WHERE id=#{primaryId}")
    @Results(id="jobMap", value={
            @Result(property = "primaryId", column = "id")
            ,@Result(property = "jobConfigId", column = "job_config_id")
            ,@Result(property = "name", column = "name")
            ,@Result(property = "group", column = "group")
            ,@Result(property = "cron", column = "cron")
            ,@Result(property = "remark", column = "remark")
            ,@Result(property = "status", column = "status")
            ,@Result(property = "createTime", column = "create_time")
            ,@Result(property = "updateTime", column = "update_time")
    })
    JobModel selectJob(@Param("primaryId") Integer primaryId);

    @Select("SELECT * FROM biz_job ORDER BY update_time desc")
    @ResultMap("jobMap")
    List<JobModel> selectJobs();
}
