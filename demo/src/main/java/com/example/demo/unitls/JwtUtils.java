package com.example.demo.unitls;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {
    private static final long EXPIRATION_TIME = 1000L * 60 * 60;  // 过期时间：60分钟

    // 生成符合HS256要求的安全密钥（256位）
    // 实际项目中，这个密钥应该从配置文件读取，而不是硬编码
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public static String generateJwt(Map<String, Object> map) {
        return Jwts.builder()
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)  // 使用安全密钥
                .setClaims(map)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .compact();
    }

    public static Claims parseJwt(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    public static void main(String[] args) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", "lihua");
        claims.put("id", "1");
        claims.put("email", "lihua@gmail.com");
        // 注意：不要在JWT中存储密码等敏感信息！

        String jwt = JwtUtils.generateJwt(claims);
        System.out.println("生成的JWT令牌：" + jwt);
        System.out.println(claims);
    }
}
