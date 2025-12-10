package com.example.reqsmanager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // 1. 生成一个安全的密钥
    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 2. Token 有效期 (例如: 1小时)
    private final long validityInMilliseconds = 3600000;

    // 3. 生成 Token
    public String createToken(String username) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
                .compact();
    }

    // ... (后续会添加解析和验证 Token 的方法) ...
}

