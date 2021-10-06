package com.joseph.foamadminjava.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.joseph.foamadminjava.common.dto.SysMenuDto;
import com.joseph.foamadminjava.entity.SysMenu;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Joseph.Liu
 * @since 2021-09-13
 */
public interface ISysMenuService extends IService<SysMenu> {

    List<SysMenuDto> getMenusByCurrentUser();

    List<SysMenu> tree();
}
