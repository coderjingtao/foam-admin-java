package com.joseph.foamadminjava.security;

import cn.hutool.json.JSONUtil;
import com.joseph.foamadminjava.common.lang.Result;
import com.joseph.foamadminjava.config.JwtConfig;
import com.joseph.foamadminjava.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 用户登录成功处理器：把服务端生成的jwt发给客户端
 * @author Joseph.Liu
 */
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final JwtConfig jwtConfig;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json; charset=UTF-8");
        ServletOutputStream outputStream = httpServletResponse.getOutputStream();
        //生成jwt,并放置到请求头中
        String jwt = jwtUtil.generateToken(authentication.getName());
        httpServletResponse.setHeader(jwtConfig.getHeader(),jwt);

        Result result = Result.success("Login Success");
        outputStream.write(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}
