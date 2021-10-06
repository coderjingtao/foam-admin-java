package com.joseph.foamadminjava.config;

import com.joseph.foamadminjava.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * spring security configuration
 * @author Joseph.Liu
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;
    private final JwtLogoutSuccessHandler logoutSuccessHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final CaptchaFilter captchaFilter;
    private final UserDetailServiceImpl userDetailService;

    private static final String[] URL_WHITELIST = {
            "/login",
            "/logout",
            "/captcha",
            "/favicon.ico",
            "/pass",
    };

    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager());
        return jwtAuthenticationFilter;
    }

    /**
     * 告诉spring security 用户密码在数据库中的以何种算法加密，方便进行加密后的密码对比
     * @return 密码加密器
     */
    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()//允许跨域
            .and()
                .csrf().disable()
            //1.登录配置
            .formLogin()//表单登录
                .successHandler(loginSuccessHandler)
                .failureHandler(loginFailureHandler)
            .and()
                .logout()
                .logoutSuccessHandler(logoutSuccessHandler)
            .and()
            //2.禁用session
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            //3.配置拦截规则:只有白名单内的请求不需要拦截,其他的请求都需要授权访问
            .authorizeRequests()
                .antMatchers(URL_WHITELIST).permitAll()
                .anyRequest().authenticated()
            .and()
            //4.异常处理器
            .exceptionHandling()
                //处理jwt认证异常
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                //处理jwt鉴权异常
                .accessDeniedHandler(jwtAccessDeniedHandler)
            .and()
            //5.配置自定义过滤器: 判断JWT是否过期，以及通过JWT获得用户的authorities
            .addFilter(jwtAuthenticationFilter())
                //在用户名密码校验过滤器之前，加入验证码过滤器，使其先验证用户登录时验证码是否正确，
                .addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class)
            ;
    }

    /**
     * 配置通过查询数据库获得用户名和密码的自定义的UserDetailServiceImpl
     * 把该service注入到spring security
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService);
    }
}
