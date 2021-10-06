package com.joseph.foamadminjava.controller;

import com.joseph.foamadminjava.common.lang.Result;
import com.joseph.foamadminjava.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Joseph.Liu
 */
@RestController
public class TestController {

    @Autowired
    ISysUserService userService;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/test")
    public Result test(){
        return Result.success(userService.list());
    }
    @PreAuthorize("hasAuthority('sys:user:list')")
    @GetMapping("/test2")
    public Result test2(){
        return Result.success(userService.list());
    }

    @GetMapping("/pass")
    public Result pass(){
        //123456:$2a$10$eOCljS3EjhPTCkSb41H8i.LXRtxEaKiglG3XMDi7xT8KeFuUMhdkW
        String password = passwordEncoder.encode("123456");
        boolean matches = passwordEncoder.matches("123456", password);
        System.out.println("匹配结果："+matches);
        return Result.success(password);
    }
}
