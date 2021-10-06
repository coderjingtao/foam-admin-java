package com.joseph.foamadminjava.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.joseph.foamadminjava.entity.SysUser;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Joseph.Liu
 * @since 2021-09-13
 */
@Repository
public interface SysUserMapper extends BaseMapper<SysUser> {
    List<Long> getMenuIdsByUserId(Long userId);
    List<SysUser> listByMenuId(Long menuId);
}
