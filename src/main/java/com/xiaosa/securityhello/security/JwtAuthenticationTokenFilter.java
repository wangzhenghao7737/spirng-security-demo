package com.xiaosa.securityhello.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaosa.securityhello.component.RedisClient;
import com.xiaosa.utils.JwtUtilsV2;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

/**
 * 统一token凭证处理
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private RedisClient redisClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取token
        String token = request.getHeader("token");
        if(StringUtils.hasText(token)){
            String key="login:token:"+ JwtUtilsV2.getSubject(token);
            String json = redisClient.get(key);
            if(StringUtils.hasText(json)){
                LoginUserDetails userDetails = objectMapper.readValue(json, LoginUserDetails.class);
                if(Objects.nonNull(userDetails)){
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }else {
                    SecurityContextHolder.getContext().setAuthentication(null);
                }
            }
        }
        //放行,后面交给Spring Security 框架
        filterChain.doFilter(request,response);
    }
}
