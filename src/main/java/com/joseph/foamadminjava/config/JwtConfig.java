package com.joseph.foamadminjava.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 自定义服务器给客户端生成的jwt信息，包括请求头字段，密钥，过期时间等
 * @author Joseph.Liu
 */
@Component
@ConfigurationProperties(prefix = "joseph.jwt")
@Getter
@Setter
public class JwtConfig {
    private long expire;
    private String secret;
    private String header;
}
