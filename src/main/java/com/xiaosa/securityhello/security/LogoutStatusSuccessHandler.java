package com.xiaosa.securityhello.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaosa.securityhello.common.Result;
import com.xiaosa.securityhello.component.RedisClient;
import com.xiaosa.utils.JwtUtils;
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
    private RedisClient redisClient;
    @Resource
    private ObjectMapper objectMapper;
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String token = request.getHeader("token");
        //判断token是否存在
        if(StringUtils.hasText(token)){
            //从redis中删除
            String key="login:token:"+ JwtUtils.getClaim(token);
            redisClient.del(key);
        }
        //返回给客户端注销成功的提示
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        Result result = Result.ok("注销成功");
//        String json = JSONUtil.toJsonStr(result);
        String json = objectMapper.writeValueAsString(result);
        response.getWriter().print(json);

    }
}
