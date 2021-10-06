package com.joseph.foamadminjava.security;

import cn.hutool.json.JSONUtil;
import com.joseph.foamadminjava.common.lang.Result;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * JWT权限异常处理器：jwt虽然认证成功，但权限不足，拒绝访问资源
 * @author Joseph.Liu
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json; charset=UTF-8");
        //403 权限不足
        httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);

        ServletOutputStream outputStream = httpServletResponse.getOutputStream();
        Result result = Result.fail(e.getMessage());
        outputStream.write(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}
