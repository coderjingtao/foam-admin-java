package com.joseph.foamadminjava.security;

import cn.hutool.core.util.StrUtil;
import com.joseph.foamadminjava.config.JwtConfig;
import com.joseph.foamadminjava.entity.SysUser;
import com.joseph.foamadminjava.service.ISysUserService;
import com.joseph.foamadminjava.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 该过滤器是真正的登录时，用来验证用户名和密码
 * @author Joseph.Liu
 */
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    @Autowired
    private JwtConfig jwtConfig;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private UserDetailServiceImpl userDetailService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String jwt = request.getHeader(jwtConfig.getHeader());
        if(StrUtil.isBlankOrUndefined(jwt)){
            chain.doFilter(request,response);
            return;
        }
        Claims claims = jwtUtil.parseToken(jwt);
        if(claims == null){
            throw new JwtException("Token异常");
        }
        if(jwtUtil.isTokenExpired(claims)){
            throw new JwtException("Token过期");
        }

        String username = claims.getSubject();
        SysUser sysUser = userService.getByUsername(username);
        UsernamePasswordAuthenticationToken token
                = new UsernamePasswordAuthenticationToken(username,null, userDetailService.getUserAuthorities(sysUser.getId()));
        SecurityContextHolder.getContext().setAuthentication(token);
        chain.doFilter(request,response);
    }
}
