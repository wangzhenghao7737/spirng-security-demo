package com.xiaosa.securityhello.common;

public interface LoginConstant {
    String LOGIN_USER_ID = "userId";
    String LOGIN_PHONE = "phone";
    long TOKEN_EXPIRE_SECONDS = 7 * 24 * 60 * 60;      // 7天，秒
    long TOKEN_EXPIRE_MILLIS = 7 * 24 * 60 * 60 * 1000; // 7天，毫秒
}
