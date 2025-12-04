package com.xiaosa.securityhello.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaosa.securityhello.common.Result;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Component
public class LoginUnAccessDeniedHandler implements AccessDeniedHandler {
    /**
     *已经登录，但是没有权限
     */
    @Resource
    private ObjectMapper objectMapper;
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException{
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        Result result = Result.error("权限不足,请重新授权。");
        //将消息json化
        String json = objectMapper.writeValueAsString(result);
        //送到客户端
        response.getWriter().print(json);
    }
}
