package com.joseph.foamadminjava.security;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.joseph.foamadminjava.common.exception.CaptchaException;
import com.joseph.foamadminjava.common.lang.Const;
import com.joseph.foamadminjava.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @author Joseph.Liu
 */
@Component
@RequiredArgsConstructor
public class CaptchaFilter extends OncePerRequestFilter {

    private final RedisUtil redisUtil;
    private final LoginFailureHandler loginFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String url = httpServletRequest.getRequestURI();
        //用户登录
        if("/login".equals(url) && httpServletRequest.getMethod().equals("POST")){
            try{
                validate(httpServletRequest);
            }catch (CaptchaException e){
                loginFailureHandler.onAuthenticationFailure(httpServletRequest,httpServletResponse,e);
            }
        }
        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }

    /**
     * 校验验证码的逻辑
     * @param httpServletRequest http request
     */
    private void validate(HttpServletRequest httpServletRequest){
        String code = httpServletRequest.getParameter("code");
        String key = httpServletRequest.getParameter("token");
        if(StringUtils.isBlank(code) || StringUtils.isBlank(key)){
            throw new CaptchaException("验证码错误");
        }
        if(!Objects.equals(code,redisUtil.hget(Const.CAPTCHA_KEY,key))){
            throw new CaptchaException("验证码错误");
        }
        //删除key,使key对应的验证码只能使用一次
        redisUtil.hdel(Const.CAPTCHA_KEY,key);
    }
}
