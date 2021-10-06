package com.joseph.foamadminjava.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.joseph.foamadminjava.common.lang.Const;
import com.joseph.foamadminjava.common.lang.Result;
import com.joseph.foamadminjava.entity.SysRole;
import com.joseph.foamadminjava.entity.SysRoleMenu;
import com.joseph.foamadminjava.entity.SysUserRole;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Joseph.Liu
 * @since 2021-09-13
 */
@RestController
@RequestMapping("/sys/role")
public class SysRoleController extends BaseController {

    /**
     * 通过角色id,查询角色信息，以及该角色对应的权限信息
     * @param id 角色id
     * @return 角色信息，以及该角色对应的权限信息
     */
    @PreAuthorize("hasAuthority('sys:role:list')")
    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id){
        SysRole sysRole = sysRoleService.getById(id);
        //去中间表查询该角色对应的菜单信息
        List<SysRoleMenu> sysRoleMenus = sysRoleMenuService.list(new QueryWrapper<SysRoleMenu>().eq("role_id", id));
        List<Long> menuIds = sysRoleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
        sysRole.setMenuIds(menuIds);
        return Result.success(sysRole);
    }

    @PreAuthorize("hasAuthority('sys:role:list')")
    @GetMapping("/list")
    public Result list(String name){
        Page<SysRole> page = sysRoleService.page(getPage(), new QueryWrapper<SysRole>().like(StrUtil.isNotBlank(name), "name", name));
        return Result.success(page);
    }

    @PreAuthorize("hasAuthority('sys:role:save')")
    @PostMapping("/save")
    public Result save(@Validated @RequestBody SysRole sysRole){
        sysRole.setCreated(LocalDateTime.now());
        sysRole.setStatu(Const.STATUS_ON);
        sysRoleService.save(sysRole);
        return Result.success(sysRole);
    }

    @PreAuthorize("hasAuthority('sys:role:update')")
    @PostMapping("/update")
    public Result update(@Validated @RequestBody SysRole sysRole){
        sysRole.setUpdated(LocalDateTime.now());
        sysRoleService.updateById(sysRole);
        //更新缓存
        sysUserService.clearUserAuthoritiesByRoleId(sysRole.getId());
        return Result.success(sysRole);
    }

    /**
     * 批量删除角色, 前端表单传过来的数据，都是存放到RequestBody中的
     * @param ids 角色id数组
     * @return 删除结果
     */
    @PreAuthorize("hasAuthority('sys:role:delete')")
    @PostMapping("/delete")
    @Transactional(rollbackFor = Throwable.class)
    public Result delete(@RequestBody Long[] ids){
        //数组转list
        sysRoleService.removeByIds(Arrays.asList(ids));
        //删除中间表
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().in("role_id", ids));
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().in("role_id",ids));
        //删除缓存
        Arrays.stream(ids).forEach( id -> {
            sysUserService.clearUserAuthoritiesByRoleId(id);
        });
        return Result.success("Delete Success");
    }

    /**
     * 给角色分配权限
     * @param roleId 角色id, 它是由路径参数传入
     * @param menuIds 菜单id, 它是由前端表单传入
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('sys:role:perm')")
    @PostMapping("/perm/{roleId}")
    @Transactional(rollbackFor = Throwable.class)
    public Result assignPermission(@PathVariable("roleId") Long roleId, @RequestBody Long[] menuIds){
        //先从中间表删除角色id,对应的所有菜单id，
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().eq("role_id",roleId));
        //再保存新传入的角色id,对应的所有菜单id
        List<SysRoleMenu> sysRoleMenus = new ArrayList<>();
        Arrays.stream(menuIds).forEach( menuId -> {
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRoleId(roleId);
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenus.add(sysRoleMenu);
        });
        sysRoleMenuService.saveBatch(sysRoleMenus);
        //删除缓存
        sysUserService.clearUserAuthoritiesByRoleId(roleId);
        return Result.success(menuIds);
    }
}
