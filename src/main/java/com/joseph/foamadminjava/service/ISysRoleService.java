package com.joseph.foamadminjava.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.joseph.foamadminjava.entity.SysRole;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Joseph.Liu
 * @since 2021-09-13
 */
public interface ISysRoleService extends IService<SysRole> {
    List<SysRole> listRolesByUserId(Long userId);
}
