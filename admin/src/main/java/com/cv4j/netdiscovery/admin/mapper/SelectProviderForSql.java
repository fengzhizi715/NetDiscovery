package com.cv4j.netdiscovery.admin.mapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SelectProviderForSql {

    public String getSqlForSelectResources() {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM biz_resource WHERE 1 = 1 ");
//        if(CommonUtil.isValidQueryParam(parserClassPath)) {
//            sql.append(" AND parser_class LIKE '%"+parserClassPath+"%'");
//        }
        sql.append(" ORDER BY resource_name");

        log.info("getSqlForSelectResources = "+sql.toString());
        return sql.toString();
    }

    public String getSqlForSelectJobConfigs() {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM biz_job_config WHERE 1 = 1");
        sql.append(" ORDER BY job_name");
        log.info("getSqlForSelectJobConfigs = "+sql.toString());
        return sql.toString();
    }

}
