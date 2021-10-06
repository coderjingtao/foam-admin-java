package com.joseph.foamadminjava.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joseph.foamadminjava.entity.SysRole;
import com.joseph.foamadminjava.mapper.SysRoleMapper;
import com.joseph.foamadminjava.service.ISysRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Joseph.Liu
 * @since 2021-09-13
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    @Override
    public List<SysRole> listRolesByUserId(Long userId) {
        return this.list(new QueryWrapper<SysRole>().inSql("id", "select role_id from sys_user_role where user_id = " + userId));
    }
}
