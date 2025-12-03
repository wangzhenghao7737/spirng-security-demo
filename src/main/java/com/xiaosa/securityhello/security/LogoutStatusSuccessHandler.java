package com.xiaosa.securityhello.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaosa.securityhello.common.Result;
import com.xiaosa.securityhello.component.RedisManager;
import com.xiaosa.utils.JwtUtils;
import com.xiaosa.utils.KeyUtils;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LogoutStatusSuccessHandler implements LogoutSuccessHandler {
    @Resource
    private RedisManager redisManager;
    @Resource
    private ObjectMapper objectMapper;
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String token_ = request.getHeader("token");
        if(StringUtils.hasText(token_)){
            redisManager.delete(KeyUtils.getLoginKey(token_));
        }
        response.setContentType("application/json;charset=utf-8");
        Result<Object> error = Result.ok("退出成功");
        String s = objectMapper.writeValueAsString(error);
        response.getWriter().write(s);
    }
}
