package com.cv4j.netdiscovery.admin.domain;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Setter
@Getter
public class Permission {

	private String permissionId;

    private String parentId;

    private String permissionName;

    private String permissionValue;

    private String permissionIcon;

    private Integer orderNumber;

    private Integer isDelete;

    private Timestamp createTime;

    private Timestamp updateTime;

    private List<Permission> subMenus;  //子菜单

}