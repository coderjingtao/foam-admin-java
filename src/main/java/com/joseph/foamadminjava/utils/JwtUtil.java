package com.joseph.foamadminjava.utils;

import com.joseph.foamadminjava.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Joseph.Liu
 */

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtConfig jwtConfig;

    /**
     * 生成 jwt token
     * @param username 从客户端请求token的用户名
     * @return token string
     */
    public String generateToken(String username){
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + jwtConfig.getExpire() * 1000);

        return Jwts.builder()
                .setHeaderParam("typ","JWT")
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512,jwtConfig.getSecret())
                .compact();
    }

    /**
     * 解析 jwt token
     * @param jwtToken jwt token
     * @return jwt claims = jwt body
     */
    public Claims parseToken(String jwtToken){
        try{
            return Jwts.parser()
                    .setSigningKey(jwtConfig.getSecret())
                    .parseClaimsJws(jwtToken)
                    .getBody();
        }catch (Throwable e){
            return null;
        }
    }

    /**
     * Jwt token 是否过期
     * @param claims Jwt claims
     * @return true: expired
     */
    public boolean isTokenExpired(Claims claims){
        return claims.getExpiration().before(new Date());
    }
}
