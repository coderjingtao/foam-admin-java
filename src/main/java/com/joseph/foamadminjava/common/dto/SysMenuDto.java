package com.joseph.foamadminjava.common.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 按照前端要求的菜单格式，返回当前用户的菜单对象
 * @author Joseph.Liu
 */
@Getter
@Setter
@ToString
public class SysMenuDto implements Serializable {
    private Long id;
    private String name;
    private String title;
    private String icon;
    private String path;
    private String component;
    private List<SysMenuDto> children = new ArrayList<>();
}
