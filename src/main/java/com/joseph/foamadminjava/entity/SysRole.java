package com.joseph.foamadminjava.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author Joseph.Liu
 * @since 2021-09-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysRole extends BaseEntity {

    @NotBlank(message = "角色名称不能为空")
    private String name;
    @NotBlank(message = "角色编码不能为空")
    private String code;
    /**
     * 备注
     */
    private String remark;
    /**
     * 角色对应的菜单id
     */
    @TableField(exist = false)
    private List<Long> menuIds = new ArrayList<>();


}
