package com.joseph.foamadminjava.service;

import com.joseph.foamadminjava.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Joseph.Liu
 * @since 2021-09-13
 */
public interface ISysUserService extends IService<SysUser> {

    SysUser getByUsername(String username);

    String getUserAuthorityByUserId(Long userId);

    void clearUserAuthoritiesByUsername(String username);

    void clearUserAuthoritiesByRoleId(Long roleId);

    void clearUserAuthoritiesByMenuId(Long menuId);
}
