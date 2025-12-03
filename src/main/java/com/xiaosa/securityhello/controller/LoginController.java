package com.xiaosa.securityhello.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaosa.securityhello.common.Result;
import com.xiaosa.securityhello.component.RedisManager;
import com.xiaosa.securityhello.security.LoginUserDetails;
import com.xiaosa.utils.JwtUtils;
import com.xiaosa.utils.KeyUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
public class LoginController {

    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private RedisManager redisManager;
    @Resource
    private ObjectMapper objectMapper;
    /**
     * 登录
     * @param phone,password
     * @return
     */
    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestParam("phone") String phone, @RequestParam("password") String password, HttpServletRequest  request){
        //避免多次登录重复token
        String token_ = request.getHeader("token");
        log.info(" last token:{}",token_);
        if(StringUtils.hasText(token_)){
            Claims claims = JwtUtils.parseToken(token_);
            String phone_ = claims.get("phone", String.class);
            if(StringUtils.hasText(phone_)&&phone_.equals(phone)){
                redisManager.delete(KeyUtils.getLoginKey(token_));
            }
        }
        //封装用户名和密码
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(phone, password);
        try{
            //调用认证方法
            Authentication authenticate = authenticationManager.authenticate(authenticationToken);
            if(Objects.isNull(authenticate)){
                return Result.error("登录失败");
            }
            //用户信息json化
            LoginUserDetails principal = (LoginUserDetails)authenticate.getPrincipal();
            String subject = principal.getUser().getUserId().toString();
            HashMap<String, Object> map = new HashMap<>();
            map.put("phone",phone);
            String token = JwtUtils.generateToken(subject,map,1000*60*60L);
            log.info("new token {}",token);
            String json = objectMapper.writeValueAsString(principal);
            redisManager.set(KeyUtils.getLoginKey(token),json,1000*60*60L);
            HashMap<String, String> result_map = new HashMap<>();
            result_map.put("token",token);
            return Result.ok(200,"登录成功",result_map);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("登录失败(Exception)");
        }
    }
}
