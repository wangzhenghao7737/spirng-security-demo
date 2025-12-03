package com.xiaosa.securityhello.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaosa.securityhello.common.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        Result<Object> error = Result.error("用户未登录或过期");
        String s = objectMapper.writeValueAsString(error);
        response.getWriter().write(s);
    }
}
