package com.cv4j.netdiscovery.admin.mapper;

import com.cv4j.netdiscovery.admin.domain.JobConfig;
import com.cv4j.netdiscovery.admin.domain.JobResource;
import com.cv4j.netdiscovery.admin.domain.SysOption;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ResourceMapper {
    @Insert("INSERT INTO biz_resource(resource_name,parser_classpath,url_prefix,url_suffix,start_page,end_page,create_time,update_time) " +
            "VALUES(#{resourceName},#{parserClassPath},#{urlPrefix},#{urlSuffix},#{startPage},#{endPage},#{createTime},#{updateTime})")
    int insertResource(JobResource jobResource);

    @Update("UPDATE biz_resource SET resource_name=#{resourceName},parser_classpath=#{parserClassPath},url_prefix=#{urlPrefix}, url_suffix=#{urlSuffix}, " +
            "start_page=#{startPage},end_page=#{endPage},update_time=#{updateTime}" +
            "WHERE id=#{primaryId}")
    int updateResourceByPrimaryKey(JobResource jobResource);

    @Delete("DELETE FROM biz_resource WHERE id=#{primaryId}")
    int deleteResourceByPrimaryKey(@Param("primaryId") Integer primaryId);

    @SelectProvider(type=SelectProviderForSql.class, method="getSqlForSelectResources")
    @Results(id="resourceMap", value={
            @Result(property = "primaryId", column = "id")
            ,@Result(property = "resourceName", column = "resource_name")
            ,@Result(property = "parserClassPath", column = "parser_classpath")
            ,@Result(property = "urlPrefix", column = "url_prefix")
            ,@Result(property = "urlSuffix", column = "url_suffix")
            ,@Result(property = "startPage", column = "start_page")
            ,@Result(property = "endPage", column = "end_page")
            ,@Result(property = "createTime", column = "create_time")
            ,@Result(property = "updateTime", column = "update_time")
    })
    List<JobResource> selectResources();

    @Select("SELECT * FROM biz_resource WHERE resource_name=#{resourceName}")
    @ResultMap("resourceMap")
    JobResource selectOneResource(@Param("resourceName") String resourceName);

//    @Select("SELECT * FROM sys_parser_class ORDER BY parser_name")
//    @Results(id="parserMap", value= {
//            @Result(property = "primaryId", column = "id")
//            , @Result(property = "optText", column = "parser_name")
//            , @Result(property = "optValue", column = "parser_classpath")
//    })
//    List<SysOption> selectParsers();
//
//    @Select("SELECT * FROM sys_job_class ORDER BY job_name ")
//    @Results(id="jobMap", value= {
//            @Result(property = "primaryId", column = "id")
//            , @Result(property = "optText", column = "job_name")
//            , @Result(property = "optValue", column = "job_classpath")
//    })
//    List<SysOption> selectJobs();
}
