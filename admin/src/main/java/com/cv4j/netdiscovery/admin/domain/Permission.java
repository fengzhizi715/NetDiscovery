package com.cv4j.netdiscovery.admin.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Setter
@Getter
public class Permission {

	private String permissionId;

    private String parentId;

    private Integer permissionType;

    private String permissionName;

    private String permissionValue;

    private String permissionIcon;

    private Integer orderNumber;

    private Integer isDelete;

    private Date createTime;

    private Date updateTime;

    private List<Permission> subMenus;  //子菜单

}