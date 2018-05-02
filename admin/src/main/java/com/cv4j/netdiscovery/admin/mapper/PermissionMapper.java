package com.cv4j.netdiscovery.admin.mapper;

import com.cv4j.netdiscovery.admin.domain.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PermissionMapper {

    @Select("SELECT * FROM sys_permission WHERE is_delete = 0")
    @Results(id="permissionMap", value={
            @Result(property = "permissionId", column = "permission_id")
            ,@Result(property = "parentId", column = "parent_id")
            ,@Result(property = "permissionName", column = "permission_name")
            ,@Result(property = "permissionValue", column = "permission_value")
            ,@Result(property = "permissionIcon", column = "permission_icon")
            ,@Result(property = "orderNumber", column = "order_number")
    })
    List<Permission> selectPermissions();

}