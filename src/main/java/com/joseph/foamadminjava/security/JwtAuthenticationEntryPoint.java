package com.joseph.foamadminjava.security;

import cn.hutool.json.JSONUtil;
import com.joseph.foamadminjava.common.lang.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * JWT认证异常处理器：客户端每次请求都会带着jwt,jwt一旦认证异常或失败，在这里处理
 * @author Joseph.Liu
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json; charset=UTF-8");
        //401 认证异常
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ServletOutputStream outputStream = httpServletResponse.getOutputStream();
        Result result = Result.fail("用户认证失败,请登录访问");

        outputStream.write(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}
