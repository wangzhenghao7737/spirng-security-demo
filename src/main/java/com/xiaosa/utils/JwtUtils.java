package com.xiaosa.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class JwtUtils {
    private static final String SECRET = "7737R-JCGK9-M62FH-9M9C4-RFXHZ:wangzhenghao@graduate.utm.my"; // 至少32字符
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());
    private static final long EXPIRATION = 1000 * 60 * 60L;
    public static String generateToken(String subject, Map<String, Object> claims, long expiration) {
        if (claims == null) {
            claims = new HashMap<>();
        }

        return Jwts.builder()
                .setClaims(claims)                  // 设置自定义声明
                .setSubject(subject)                // 设置主题（如 username）
                .setIssuedAt(new Date())            // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() +   (Objects.isNull(expiration) ?EXPIRATION:expiration))) // 过期时间
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // 签名算法和密钥
                .compact();                         // 压缩生成字符串
    }
    public static Claims parseToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 安全地解析 Token（捕获常见异常）
     */
    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            System.err.println("Invalid JWT signature or malformed token");
        } catch (ExpiredJwtException e) {
            System.err.println("JWT token is expired");
        } catch (UnsupportedJwtException e) {
            System.err.println("JWT token is unsupported");
        } catch (IllegalArgumentException e) {
            System.err.println("JWT claims string is empty");
        }
        return false;
    }
}
