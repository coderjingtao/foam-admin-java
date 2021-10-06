package com.joseph.foamadminjava.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.joseph.foamadminjava.common.dto.PassDto;
import com.joseph.foamadminjava.common.lang.Const;
import com.joseph.foamadminjava.common.lang.Result;
import com.joseph.foamadminjava.entity.SysRole;
import com.joseph.foamadminjava.entity.SysUser;
import com.joseph.foamadminjava.entity.SysUserRole;
import io.jsonwebtoken.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Joseph.Liu
 * @since 2021-09-13
 */
@RestController
@RequestMapping("/sys/user")
public class SysUserController extends BaseController {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    /**
     * 通过用户id,查询用户信息，以及该用户对应的角色信息
     * @param id 用户id
     * @return 用户信息
     */
    @PreAuthorize("hasAuthority('sys:user:list')")
    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id){
        SysUser sysUser = sysUserService.getById(id);
        Assert.notNull(sysUser,"找不到该用户");
        List<SysRole> sysRoles = sysRoleService.listRolesByUserId(sysUser.getId());
        sysUser.setSysRoles(sysRoles);
        return Result.success(sysUser);
    }

    @PreAuthorize("hasAuthority('sys:user:list')")
    @GetMapping("/list")
    public Result list(String username){
        Page<SysUser> page = sysUserService.page(getPage(), new QueryWrapper<SysUser>().like(StrUtil.isNotBlank(username), "username", username));
        //set roles
        page.getRecords().forEach(sysUser -> {
            sysUser.setSysRoles(sysRoleService.listRolesByUserId(sysUser.getId()));
        });
        return Result.success(page);
    }

    @PreAuthorize("hasAuthority('sys:user:save')")
    @PostMapping("/save")
    public Result save(@Validated @RequestBody SysUser sysUser){
        sysUser.setCreated(LocalDateTime.now());
        sysUser.setStatu(Const.STATUS_ON);
        //init password
        String password = bCryptPasswordEncoder.encode(Const.DEFAULT_PASSWORD);
        sysUser.setPassword(password);
        //init avatar
        sysUser.setAvatar(Const.DEFAULT_AVATAR);
        sysUserService.save(sysUser);
        return Result.success(sysUser);
    }

    @PreAuthorize("hasAuthority('sys:user:update')")
    @PostMapping("/update")
    public Result update(@Validated @RequestBody SysUser sysUser){
        sysUser.setUpdated(LocalDateTime.now());
        sysUserService.updateById(sysUser);
        return Result.success(sysUser);
    }

    @PreAuthorize("hasAuthority('sys:user:delete')")
    @Transactional(rollbackFor = Throwable.class)
    @PostMapping("/delete")
    public Result delete(@RequestBody Long[] ids){
        //删除用户表
        sysUserService.removeByIds(Arrays.asList(ids));
        //删除中间表
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().in("user_id",ids));
        return Result.success("Delete Success");
    }

    /**
     * 给用户分配角色
     * @param userId 用户ID,它是由[路径]参数传入
     * @param roleIds 角色ID, 它是由前端[表单]传入
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('sys:user:role')")
    @Transactional(rollbackFor = Throwable.class)
    @PostMapping("/role/{userId}")
    public Result assignRoles(@PathVariable("userId") Long userId,@RequestBody Long[] roleIds){
        //把传入的roleIds和userId形成新的用户角色中间表
        List<SysUserRole> sysUserRoles = new ArrayList<>();
        Arrays.stream(roleIds).forEach( roleId -> {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(userId);
            sysUserRole.setRoleId(roleId);
            sysUserRoles.add(sysUserRole);
        });
        //删除旧的用户对应角色的中间表
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().eq("user_id",userId));
        //保存新的用户对应角色的中间表
        sysUserRoleService.saveBatch(sysUserRoles);
        //删除Redis缓存
        SysUser sysUser = sysUserService.getById(userId);
        sysUserService.clearUserAuthoritiesByUsername(sysUser.getUsername());
        return Result.success("Assign Success");
    }

    @PreAuthorize("hasAuthority('sys:user:repass')")
    @PostMapping("/repass")
    public Result repass(@RequestBody Long userId){
        SysUser sysUser = sysUserService.getById(userId);
        sysUser.setPassword(bCryptPasswordEncoder.encode(Const.DEFAULT_PASSWORD));
        sysUser.setUpdated(LocalDateTime.now());
        sysUserService.updateById(sysUser);
        return Result.success("Reset Password Success");
    }

    @PostMapping("/updatePass")
    public Result updatePass(@Validated @RequestBody PassDto passDto, Principal principal){
        SysUser sysUser = sysUserService.getByUsername(principal.getName());
        //查看旧密码是否和数据库中匹配
        boolean matches = bCryptPasswordEncoder.matches(passDto.getCurrentPass(), sysUser.getPassword());
        if(!matches){
            return Result.fail("旧密码不正确");
        }
        sysUser.setPassword(bCryptPasswordEncoder.encode(passDto.getPassword()));
        sysUser.setUpdated(LocalDateTime.now());
        sysUserService.updateById(sysUser);
        return Result.success("Update Password Success");
    }



}
