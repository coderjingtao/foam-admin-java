package com.joseph.foamadminjava.controller;


import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.joseph.foamadminjava.common.dto.SysMenuDto;
import com.joseph.foamadminjava.common.lang.Result;
import com.joseph.foamadminjava.entity.SysMenu;
import com.joseph.foamadminjava.entity.SysRoleMenu;
import com.joseph.foamadminjava.entity.SysUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  前端控制器: 菜单控制器
 * </p>
 *
 * @author Joseph.Liu
 * @since 2021-09-13
 */
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController extends BaseController {

    /**
     * 查询当前用户的导航信息，包括菜单和权限2部分信息
     * @param principal which can be used to represent any entity, such as an individual, a corporation, and a login id
     * @return 当前用户的前端需要的授权信息和菜单信息
     */
    @GetMapping("/nav")
    public Result navigate(Principal principal){
        //1.通过用户名获取用户实例: principal是來自Authentication.getPrincipal()，返回值被认证过的主体
        SysUser sysUser = sysUserService.getByUsername(principal.getName());
        //2.获取用户权限信息
        //示例: ROLE_admin,ROLE_user,sys:user:list
        String authorities = sysUserService.getUserAuthorityByUserId(sysUser.getId());
        String[] authorityArr = StringUtils.tokenizeToStringArray(authorities, ",");
        //3.获取用户菜单信息:按照前端的格式要求
        List<SysMenuDto> frontEndMenus = sysMenuService.getMenusByCurrentUser();
        //返回的属性code需要与前端保持一致
        return Result.success(MapUtil.builder()
                .put("authorities",authorityArr)
                .put("nav",frontEndMenus)
                .map()
        );
    }
    /**
     * 根据menu id,获得menu的信息
     * @param id menu id
     * @return menu info json
     */
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sys:menu:list')")
    public Result info(@PathVariable("id") Long id){
        return Result.success(sysMenuService.getById(id));
    }
    /**
     * 获得所有menu的列表
     * @return 树状结构的menu列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:menu:list')")
    public Result list(){
        List<SysMenu> sysMenus = sysMenuService.tree();
        return Result.success(sysMenus);
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sys:menu:save')")
    public Result save(@Validated @RequestBody SysMenu sysMenu){
        sysMenu.setCreated(LocalDateTime.now());
        sysMenuService.save(sysMenu);
        return Result.success(sysMenu);
    }
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sys:menu:update')")
    public Result update(@Validated @RequestBody SysMenu sysMenu){
        sysMenu.setUpdated(LocalDateTime.now());
        sysMenuService.updateById(sysMenu);
        //清除所有与该菜单相关的权限缓存
        sysUserService.clearUserAuthoritiesByMenuId(sysMenu.getId());
        return Result.success(sysMenu);
    }
    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('sys:menu:delete')")
    public Result delete(@PathVariable("id") Long id){
        //先判断该菜单是否有子菜单
        long count = sysMenuService.count(new QueryWrapper<SysMenu>().eq("parent_id", id));
        if(count > 0){
            return Result.fail("请先删除子菜单");
        }
        //清除所有与该菜单相关的权限缓存
        sysUserService.clearUserAuthoritiesByMenuId(id);
        //删除菜单表
        sysMenuService.removeById(id);
        //删除中间表
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().eq("menu_id",id));
        return Result.success("删除菜单成功");
    }
}
