package com.joseph.foamadminjava.controller;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.map.MapUtil;
import com.google.code.kaptcha.Producer;
import com.joseph.foamadminjava.common.lang.Const;
import com.joseph.foamadminjava.common.lang.Result;
import com.joseph.foamadminjava.entity.SysUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;


/**
 * @author Joseph.Liu
 */
@RestController
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final Producer producer;

    @GetMapping("/captcha")
    public Result captcha() throws IOException {
        String key = UUID.randomUUID().toString();
        String code = producer.createText();
        System.out.println("Token: "+key);
        System.out.println("captcha code: "+code);
        //根据验证码得到验证码图片，并把图片写入到输出流中
        BufferedImage image = producer.createImage(code);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image,"jpg",outputStream);
        //把输出流加密
        BASE64Encoder encoder = new BASE64Encoder();
        String prefix = "data:image/jpeg;base64,";
        String base64ImgStr = prefix + encoder.encode(outputStream.toByteArray());
        //把生成的key和code保存到redis, 并把key和图片字符串返回给前端
        redisUtil.hset(Const.CAPTCHA_KEY,key,code,120);
        return Result.success(MapUtil.builder().put("token",key).put("captchaImg",base64ImgStr).build());
    }

    /**
     * 给用户中心返回用户的相关信息，例如用户名，头像等，注意不要返回用户的密码
     * @param principal 当前用户
     * @return 用户信息
     */
    @GetMapping("/sys/userInfo")
    public Result userInfo(Principal principal){
        SysUser sysUser = sysUserService.getByUsername(principal.getName());
        return Result.success(MapUtil.builder()
                .put("id",sysUser.getId())
                .put("username",sysUser.getUsername())
                .put("avatar",sysUser.getAvatar())
                .put("created",sysUser.getCreated())
                .map()
        );
    }
}
