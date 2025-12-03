package com.xiaosa.securityhello.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

@Configuration
public class SecurityConfig {
    @Resource
    private LoginUnAuthenticationEntryPointHandler loginUnAuthenticationEntryPointHandler;
    @Resource
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    @Resource
    private LogoutStatusSuccessHandler logoutStatusSuccessHandler;
    @Resource
    private LoginUnAccessDeniedHandler loginUnAccessDeniedHandler;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/login","/test1").permitAll()
                .anyRequest().authenticated();
        //注册自定义过滤链
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        //注册匿名访问私有资源的处理器
        http.exceptionHandling().authenticationEntryPoint(loginUnAuthenticationEntryPointHandler);
        //注册权限不够的处理器
        http.exceptionHandling().accessDeniedHandler(loginUnAccessDeniedHandler);
        //注销
        http.logout().logoutSuccessHandler(logoutStatusSuccessHandler);
        return http.build();

    }
    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }
    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
