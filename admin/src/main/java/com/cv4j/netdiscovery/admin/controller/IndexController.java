package com.cv4j.netdiscovery.admin.controller;

import com.cv4j.netdiscovery.admin.dto.ResultMap;
import com.cv4j.netdiscovery.admin.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/index")
@Slf4j
public class IndexController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 获取导航菜单
     */
    @GetMapping("/menu")
    public ResultMap navMenu() {
        return ResultMap.ok().put("menus", permissionService.getMenus());
    }

}