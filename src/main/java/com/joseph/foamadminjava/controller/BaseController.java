package com.joseph.foamadminjava.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.joseph.foamadminjava.service.*;
import com.joseph.foamadminjava.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Joseph.Liu
 */
public class BaseController {
    @Autowired
    HttpServletRequest req;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    ISysUserService sysUserService;

    @Autowired
    ISysRoleService sysRoleService;

    @Autowired
    ISysMenuService sysMenuService;

    @Autowired
    ISysUserRoleService sysUserRoleService;

    @Autowired
    ISysRoleMenuService sysRoleMenuService;

    /**
     * 获取分页的页面
     * @return page
     */
    public Page getPage(){
        int current = ServletRequestUtils.getIntParameter(req,"current",1);
        int size = ServletRequestUtils.getIntParameter(req,"size",10);
        return new Page(current,size);
    }
}
