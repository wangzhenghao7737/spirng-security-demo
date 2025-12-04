package com.xiaosa.securityhello.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaosa.securityhello.common.LoginConstant;
import com.xiaosa.securityhello.common.Result;
import com.xiaosa.securityhello.domain.User;
import com.xiaosa.securityhello.security.LoginUserDetails;
import com.xiaosa.securityhello.component.RedisClient;
import com.xiaosa.utils.JwtUtilsV2;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
public class LoginController {

    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private RedisClient redisClient;
    @Resource
    private ObjectMapper objectMapper;
    @PostMapping("/login")
    public Result<Map<String, String>> loginPhone(@RequestParam("phone") String phone, @RequestParam("password") String password, HttpServletRequest request){
        return loginLogic(phone,password,request);
    }
    @PostMapping("/loginId")
    public Result<Map<String, String>> loginId(@RequestParam("userId") Long userId, @RequestParam("password") String password, HttpServletRequest request){
        return loginLogic(String.valueOf(userId),password,request);
    }
    public Result<Map<String, String>> loginLogic(String id,String pwd,HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(id, pwd);
        try {
            Authentication authenticate = authenticationManager.authenticate(authenticationToken);
            if (Objects.isNull(authenticate)) {
                return Result.error("用户名或密码错误");
            }
            LoginUserDetails principal = (LoginUserDetails) authenticate.getPrincipal();
            User user = principal.getUser();
            // 有则删缓存
            redisClient.del(JwtUtilsV2.getLoginTokenKey(user.getUserId()));
            String json = objectMapper.writeValueAsString(principal);
            //生成token
            String token = JwtUtilsV2.sign(String.valueOf(user.getUserId()),  LoginConstant.TOKEN_EXPIRE_MILLIS);
            //将生成的token保存到redis中
            redisClient.set(JwtUtilsV2.getLoginTokenKey(user.getUserId()), json, LoginConstant.TOKEN_EXPIRE_MILLIS);
            //将token返回给客户端
            Map<String, String> map = new HashMap<>();
            map.put("token", token);
            return Result.ok(map);
        } catch (JsonProcessingException e) {
            return Result.error("登录失败LoginController");
        } catch (Exception e){
            return Result.error("用户名或密码错误");
        }
    }
    @PostMapping("/refreshLogin")
    public Result<Map<String, String>> refreshLogin(HttpServletRequest request) {
        //判断曾经登录是否还在有效期
        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
            return Result.error("缺失登录凭证");
        }
        if (!JwtUtilsV2.verify(token)) {
            return Result.error("登录已过期jwt");
        }
        String userId = JwtUtilsV2.getSubject(token);
        String s = redisClient.get(JwtUtilsV2.getLoginTokenKey(userId));
        if (!StringUtils.hasText(s)) {
            return Result.error("登录已过期redis");
        }
        redisClient.expire(JwtUtilsV2.getLoginTokenKey(userId),  LoginConstant.TOKEN_EXPIRE_MILLIS);
        //LoginUserDetails principal = objectMapper.readValue(s, LoginUserDetails.class);
        String new_token = JwtUtilsV2.sign(userId,  LoginConstant.TOKEN_EXPIRE_MILLIS);
        Map<String, String> map = new HashMap<>();
        map.put("token", new_token);
        return Result.ok(map);
    }
}
