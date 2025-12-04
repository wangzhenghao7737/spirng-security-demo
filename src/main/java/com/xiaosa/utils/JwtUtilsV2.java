package com.xiaosa.utils;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.time.Instant;
import java.util.Date;

public class JwtUtilsV2 {
    private static final byte[] SECRET_KEY = "7737R-JCGK9-M62FH-9M9C4-RFXHZ:wangzhenghao@graduate.utm.my".getBytes();

    private static final JWSAlgorithm ALGORITHM = JWSAlgorithm.HS256;

    /**
     * 生成 JWT Token
     *
     * @param userId 用户 ID
     * @param expiresTimeMs 过期时间（毫秒）
     * @return JWT 字符串
     */
    public static String sign(String userId, Long expiresTimeMs) {
        try {
            // 1. 构建 Claims（Payload）
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(userId)
                    .issueTime(new Date())
                    .expirationTime(Date.from(Instant.now().plusMillis(expiresTimeMs)))
                    .build();
            // 2. 创建签名头
            JWSHeader header = new JWSHeader(ALGORITHM);
            // 3. 创建 SignedJWT
            SignedJWT signedJWT = new SignedJWT(header, claimsSet);
            // 4. 签名
            JWSSigner signer = new MACSigner(SECRET_KEY);
            signedJWT.sign(signer);
            // 5. 序列化为字符串
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    /**
     * 验证 JWT Token 是否有效（未过期、签名正确）
     *
     * @param token JWT 字符串
     * @return true if valid
     * @throws RuntimeException if invalid
     */
    public static boolean verify(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(SECRET_KEY);
            if (!signedJWT.verify(verifier)) {
                throw new RuntimeException("Invalid JWT signature");
            }
            // 检查是否过期（Nimbus 会自动检查 exp、nbf 等）
            if (signedJWT.getJWTClaimsSet().getExpirationTime() != null &&
                    new Date().after(signedJWT.getJWTClaimsSet().getExpirationTime())) {
                throw new RuntimeException("JWT token has expired");
            }
            return true;
        } catch (JOSEException | java.text.ParseException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    /**
     * 从 Token 中获取 subject（原 iss 在 auth0 中常被误用，标准应为 sub）
     *
     * @param token JWT 字符串
     * @return subject (e.g., username)
     */
    public static String getSubject(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (java.text.ParseException e) {
            throw new RuntimeException("Failed to parse JWT token", e);
        }
    }

    public static String getLoginTokenKey(Long userId){
        return getLoginTokenKey(userId.toString());
    }
    public static String getLoginTokenKey(String userId){
        return "login:token:" + userId;
    }
}
