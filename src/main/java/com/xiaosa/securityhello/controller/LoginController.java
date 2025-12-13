package com.xiaosa.securityhello.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaosa.securityhello.common.LoginConstant;
import com.xiaosa.securityhello.common.LoginType;
import com.xiaosa.securityhello.common.Result;
import com.xiaosa.securityhello.domain.User;
import com.xiaosa.securityhello.security.LoginUserDetails;
import com.xiaosa.securityhello.component.RedisClient;
import com.xiaosa.securityhello.security.SmsCodeAuthenticationToken;
import com.xiaosa.utils.JwtUtilsV2;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.xiaosa.securityhello.common.LoginType.PHONE;

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
    public Result<Map<String, String>> loginPhone(
            @RequestParam("loginType") String loginTypeStr,
            @RequestParam(required = false) String account,   // 账号或手机号
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String code
            ){
        LoginType loginType;
        try {
            loginType = LoginType.valueOf(loginTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Result.error("不支持的登录方式");
        }

        Authentication authenticationToken = null;

        switch (loginType) {
            case ACCOUNT:
                if (!StringUtils.hasText(account) || !StringUtils.hasText(password)) {
                    return Result.error("账号和密码不能为空");
                }
                authenticationToken = new UsernamePasswordAuthenticationToken(account, password);
                break;

            case PHONE:
                if (!StringUtils.hasText(account) || !StringUtils.hasText(password)) {
                    return Result.error("手机号和密码不能为空");
                }
                if (!account.matches("1[3-9]\\d{9}")) {
                    return Result.error("手机号格式错误");
                }
                authenticationToken = new UsernamePasswordAuthenticationToken(account, password);
                break;

            case SMS:
                if (!StringUtils.hasText(account) || !StringUtils.hasText(code)) {
                    return Result.error("手机号和验证码不能为空");
                }
                if (!account.matches("1[3-9]\\d{9}")) {
                    return Result.error("手机号格式错误");
                }
                authenticationToken = new SmsCodeAuthenticationToken(account, code);
                break;

            default:
                return Result.error("未知登录类型");
        }

        try {
            Authentication authenticate = authenticationManager.authenticate(authenticationToken);
            if (authenticate == null || !authenticate.isAuthenticated()) {
                return Result.error("认证失败");
            }

            LoginUserDetails principal = (LoginUserDetails) authenticate.getPrincipal();
            User user = principal.getUser();

            // 清除旧 token（防多端冲突）
            redisClient.del(JwtUtilsV2.getLoginTokenKey(user.getUserId()));

            // 生成新 token
            String token = JwtUtilsV2.sign(String.valueOf(user.getUserId()), LoginConstant.TOKEN_EXPIRE_MILLIS);
            String json = objectMapper.writeValueAsString(principal);
            redisClient.set(JwtUtilsV2.getLoginTokenKey(user.getUserId()), json, LoginConstant.TOKEN_EXPIRE_MILLIS);

            Map<String, String> map = new HashMap<>();
            map.put("token", token);
            return Result.ok(map);

        } catch (JsonProcessingException e) {
            log.error("序列化用户信息失败", e);
            return Result.error("系统异常");
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("登录失败: {}", e.getMessage());
            return Result.error("用户名或密码错误");
        }
    }
    @PostMapping("/login/account")
    public Result<Map<String, String>> loginByAccount(
            @RequestParam String account,
            @RequestParam String password) {

        if (!StringUtils.hasText(account) || !StringUtils.hasText(password)) {
            return Result.error("账号和密码不能为空");
        }

        Authentication auth = new UsernamePasswordAuthenticationToken(account, password);
        return performLogin(auth);
    }
    @PostMapping("/login/phone-password")
    public Result<Map<String, String>> loginByPhoneAndPassword(
            @RequestParam String phone,
            @RequestParam String password) {

        if (!isValidPhone(phone)) {
            return Result.error("手机号格式错误");
        }
        if (!StringUtils.hasText(password)) {
            return Result.error("密码不能为空");
        }

        Authentication auth = new UsernamePasswordAuthenticationToken(phone, password);
        return performLogin(auth);
    }
    @PostMapping("/login/sms")
    public Result<Map<String, String>> loginBySms(
            @RequestParam String phone,
            @RequestParam String code) {

        if (!isValidPhone(phone)) {
            return Result.error("手机号格式错误");
        }
        if (!StringUtils.hasText(code)) {
            return Result.error("验证码不能为空");
        }

        Authentication auth = new SmsCodeAuthenticationToken(phone, code);
        return performLogin(auth);
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
    @PutMapping("/sms/send")
    public Result<String> sendSms(@RequestParam("phone") String phone
                                , @RequestParam("code") String code) {
        redisClient.set("sms:code:"+phone, code,1000*60L);
        return Result.ok("发送成功");
    }
    private Result<Map<String, String>> performLogin(Authentication authentication) {
        try {
            Authentication authenticated = authenticationManager.authenticate(authentication);
            LoginUserDetails principal = (LoginUserDetails) authenticated.getPrincipal();
            User user = principal.getUser();

            // 清除旧 token
            redisClient.del(JwtUtilsV2.getLoginTokenKey(user.getUserId()));

            // 生成新 token
            String token = JwtUtilsV2.sign(String.valueOf(user.getUserId()), LoginConstant.TOKEN_EXPIRE_MILLIS);
            String json = objectMapper.writeValueAsString(principal);
            redisClient.set(JwtUtilsV2.getLoginTokenKey(user.getUserId()), json, LoginConstant.TOKEN_EXPIRE_MILLIS);

            Map<String, String> map = new HashMap<>();
            map.put("token", token);
            return Result.ok(map);

        } catch (JsonProcessingException e) {
            log.error("序列化用户失败", e);
            return Result.error("系统异常");
        } catch (Exception e) {
            log.warn("登录失败: {}", e.getMessage());
            return Result.error("认证失败"); // 可根据异常类型细化
        }
    }

    private boolean isValidPhone(String phone) {
        return StringUtils.hasText(phone) && phone.matches("1[3-9]\\d{9}");
    }
}
