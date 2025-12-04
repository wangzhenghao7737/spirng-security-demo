package com.xiaosa.securityhello.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaosa.securityhello.common.Result;
import com.xiaosa.securityhello.security.LoginUserDetails;
import com.xiaosa.utils.JwtUtils;
import com.xiaosa.securityhello.component.RedisClient;
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
    /**
     * 登录
     * @param phone,password
     * @return
     */
    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestParam("phone") String phone, @RequestParam("password") String password, HttpServletRequest request){
        //判断曾经登录是否还在有效期
        String token_ = request.getHeader("token");
        //判断token是否存在
        if(StringUtils.hasText(token_)){
            String claim = JwtUtils.getClaim(token_);
            if(StringUtils.hasText(claim) && claim.equals(phone)){
                //从redis中删除
                String key="login:token:"+phone;
                redisClient.del(key);
            }
        }


        //封装用户名和密码
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(phone,password);

        /*
         * 调用认证方法
         * Authentication: 如果成功,封装了用户的全部信息，认证/授权
         */
        try {
            Authentication authenticate = authenticationManager.authenticate(authenticationToken);
            if(Objects.isNull(authenticate)){
                return Result.error("用户名或密码错误");
            }

            //生成token
            String token = JwtUtils.sign(phone, 1000 * 60 * 60 * 24 * 7L);
            //将生成的token保存到redis中
            String key="login:token:"+phone;
            //将用户信息json化
            LoginUserDetails principal = (LoginUserDetails) authenticate.getPrincipal();
//            String json = JSONUtil.toJsonStr(principal);
            String json = objectMapper.writeValueAsString(principal);
            //保存到redis中
            redisClient.set(key,json,1000 * 60 * 60 * 24 * 7L);
            //将token返回给客户端
            Map<String,Object> map = new HashMap<>();
            map.put("token",token);
            return Result.ok(map);
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("用户名或密码错误");
        }
    }
}
