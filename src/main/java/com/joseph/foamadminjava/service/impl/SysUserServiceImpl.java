package com.joseph.foamadminjava.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joseph.foamadminjava.entity.SysMenu;
import com.joseph.foamadminjava.entity.SysRole;
import com.joseph.foamadminjava.entity.SysUser;
import com.joseph.foamadminjava.mapper.SysUserMapper;
import com.joseph.foamadminjava.service.ISysMenuService;
import com.joseph.foamadminjava.service.ISysRoleService;
import com.joseph.foamadminjava.service.ISysUserService;
import com.joseph.foamadminjava.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Joseph.Liu
 * @since 2021-09-13
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ISysRoleService sysRoleService;
    @Autowired
    private ISysMenuService sysMenuService;

    @Override
    public SysUser getByUsername(String username) {
        return this.getOne(new QueryWrapper<SysUser>().eq("username",username));
    }

    @Override
    public String getUserAuthorityByUserId(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        //  ROLE_admin,ROLE_normal,sys:user:list,....
        String authority = "";
        // 先在Redis中查询是否有该用户的权限缓存
        if(redisUtil.hasKey("GrantedAuthority:"+user.getUsername())){
            authority = (String) redisUtil.get("GrantedAuthority:"+user.getUsername());
        }else{
            // 获取角色编码
            List<SysRole> roles = sysRoleService.list(new QueryWrapper<SysRole>().inSql("id", "select role_id from sys_user_role where user_id = " + userId));
            if(roles.size() > 0){
                String roleCodes = roles.stream().map(r -> "ROLE_"+r.getCode()).collect(Collectors.joining(","));
                authority = roleCodes.concat(",");
            }
            // 获取菜单操作编码
            List<Long> menuIds = sysUserMapper.getMenuIdsByUserId(userId);
            if(menuIds.size() > 0){
                List<SysMenu> menus = sysMenuService.listByIds(menuIds);
                String menuPermissions = menus.stream().map(SysMenu::getPerms).collect(Collectors.joining(", "));
                authority = authority.concat(menuPermissions);
            }
            //put authorities in Redis for an hour
            redisUtil.set("GrantedAuthority:"+user.getUsername(),authority,60*60);
        }
        return authority;
    }

    @Override
    public void clearUserAuthoritiesByUsername(String username) {
        redisUtil.del("GrantedAuthority:" + username);
    }

    @Override
    public void clearUserAuthoritiesByRoleId(Long roleId) {
        List<SysUser> sysUsers = this.list(new QueryWrapper<SysUser>()
                .inSql("id", "select user_id from sys_user_role where role_id = " + roleId));
        sysUsers.forEach( user -> this.clearUserAuthoritiesByUsername(user.getUsername()));
    }

    @Override
    public void clearUserAuthoritiesByMenuId(Long menuId) {
        List<SysUser> sysUsers = sysUserMapper.listByMenuId(menuId);
        sysUsers.forEach( user -> this.clearUserAuthoritiesByUsername(user.getUsername()));
    }
}