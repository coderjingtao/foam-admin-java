package com.joseph.foamadminjava.common.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author Joseph.Liu
 */
@Data
public class PassDto implements Serializable {

    @NotBlank(message = "旧密码不能为空")
    private String currentPass;

    @NotBlank(message = "新密码不能为空")
    private String password;

}
