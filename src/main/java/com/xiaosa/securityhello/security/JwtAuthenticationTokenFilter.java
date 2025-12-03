package com.xiaosa.securityhello.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaosa.utils.KeyUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * 统一token凭证处理
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private ObjectMapper objectMapper;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("token");
        if(StringUtils.hasText( token)){
            String json = redisTemplate.opsForValue().get(KeyUtils.getLoginKey(token));
            if(StringUtils.hasText(json)){
                LoginUserDetails userDetails = objectMapper.readValue(json, LoginUserDetails.class);
                if(Objects.nonNull(userDetails)){
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }else{
                    SecurityContextHolder.getContext().setAuthentication(null);
                }
            }
        }
        filterChain.doFilter(request,response);
    }
}
