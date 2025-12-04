package com.xiaosa.securityhello.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaosa.securityhello.common.Result;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
/**
 * 认证失败处理类
 * 未登录时访问私有化资源会调用此方法
 */
@Component
public class LoginUnAuthenticationEntryPointHandler implements AuthenticationEntryPoint {
    @Resource
    private ObjectMapper objectMapper;
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        Result error = Result.error("用户未登录或登录已过期,请重新登录");
        String json = objectMapper.writeValueAsString(error);
        response.getWriter().print(json);
    }
}
