package com.joseph.foamadminjava.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.joseph.foamadminjava.common.dto.SysMenuDto;
import com.joseph.foamadminjava.entity.SysMenu;
import com.joseph.foamadminjava.entity.SysUser;
import com.joseph.foamadminjava.mapper.SysMenuMapper;
import com.joseph.foamadminjava.mapper.SysUserMapper;
import com.joseph.foamadminjava.service.ISysMenuService;
import com.joseph.foamadminjava.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public List<SysMenuDto> getMenusByCurrentUser() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SysUser sysUser = sysUserService.getByUsername(username);
        List<Long> menuIds = sysUserMapper.getMenuIdsByUserId(sysUser.getId());
        List<SysMenu> sysMenus = this.listByIds(menuIds);
        //把数据表sys_menu中的数据转为树状结构
        List<SysMenu> topMenus = buildTreeMenu(sysMenus);
        return convert(topMenus);
    }

    /**
     * 获得所有的菜单列表，并把菜单列表按照树状结构返回
     * @return 树状结构的所有菜单
     */
    @Override
    public List<SysMenu> tree() {
        //1.查询所有菜单，并按照order_num排序
        List<SysMenu> sysMenus = this.list(new QueryWrapper<SysMenu>().orderByAsc("order_num"));
        //2.转换成树状结构
        return buildTreeMenu(sysMenus);
    }

    /**
     * 把数据表sys_menu中的数据转为树状结构
     * @param menus 所有菜单
     * @return 顶级导航菜单
     */
    private List<SysMenu> buildTreeMenu(final List<SysMenu> menus){
        List<SysMenu> topMenus = getTopMenus(menus);
        setChildrenNodes(topMenus,menus);
        return topMenus;
    }

    private List<SysMenu> getTopMenus(List<SysMenu> menus){
        if(CollectionUtil.isEmpty(menus)){
            return ListUtil.empty();
        }
        return menus.stream().filter(m -> m.getParentId() == 0).collect(Collectors.toList());
    }

    private void setChildrenNodes(List<SysMenu> parentNodes, final List<SysMenu> allNodes){
        if(CollectionUtil.isEmpty(parentNodes)){
            return;
        }
        for(SysMenu parentNode : parentNodes){
            List<SysMenu> childrenNodes = allNodes.stream().filter(n -> Objects.equals(n.getParentId(),parentNode.getId())).collect(Collectors.toList());
            parentNode.setChildren(childrenNodes);
            setChildrenNodes(childrenNodes,allNodes);
        }
    }

    /**
     * 把树状结构的后端菜单,转化为树状结构的前端格式的菜单
     * @param topMenus 后端菜单的顶级菜单
     * @return 前端需要的树状结构的菜单
     */
    private List<SysMenuDto> convert(List<SysMenu> topMenus){
        if(CollectionUtil.isEmpty(topMenus)){
            return ListUtil.empty();
        }
        List<SysMenuDto> menuDtos = new ArrayList<>();
        topMenus.forEach( topMenu -> {
            SysMenuDto dto = new SysMenuDto();
            dto.setId(topMenu.getId());
            dto.setName(topMenu.getPerms());
            dto.setTitle(topMenu.getName());
            dto.setComponent(topMenu.getComponent());
            dto.setPath(topMenu.getPath());
            if(topMenu.getChildren().size() > 0){
                dto.setChildren(convert(topMenu.getChildren()));
            }
            menuDtos.add(dto);
        });
        return menuDtos;
    }
}
