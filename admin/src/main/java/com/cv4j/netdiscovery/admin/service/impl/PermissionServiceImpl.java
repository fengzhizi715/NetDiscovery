package com.cv4j.netdiscovery.admin.service.impl;

import com.cv4j.netdiscovery.admin.domain.Permission;
import com.cv4j.netdiscovery.admin.mapper.PermissionMapper;
import com.cv4j.netdiscovery.admin.service.PermissionService;
import com.safframework.tony.common.utils.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

	@Autowired
	private PermissionMapper permissionMapper;

	@Override
	public List<Permission> getMenus() {
		List<Permission> result = new ArrayList<Permission>();
		List<Permission> permissions = permissionMapper.selectPermissions();

		if (Preconditions.isNotBlank(permissions)) {
			for(Permission menu1 : permissions) {
				if("0".equals(menu1.getParentId())){  //0代表一级菜单
					List<Permission> subMenu = new ArrayList<>();
					for(Permission menu2 : permissions) {
						if(menu1.getPermissionId().equals(menu2.getParentId())) {
							subMenu.add(menu2);
						}
					}
					menu1.setSubMenus(subMenu);
					result.add(menu1);
				}
			}
		}

		return result;
	}

}
