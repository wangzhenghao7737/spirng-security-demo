package com.xiaosa.securityhello.security;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.ResponseStatus;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Resource
    private LoginUnAuthenticationEntryPointHandler loginUnAuthenticationEntryPointHandler;
    @Resource
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    @Resource
    private LogoutStatusSuccessHandler logoutStatusSuccessHandler;
    @Resource
    private LoginUnAccessDeniedHandler loginUnAccessDeniedHandler;
    @Resource
    private SmsCodeAuthenticationProvider smsCodeAuthenticationProvider;
    @Resource
    private UserDetailsService userDetailsService;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/login/account","/login/phone-password","/login/sms",
                        "/sms/send","/refreshLogin","/create").permitAll()
                .anyRequest().authenticated());
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint(loginUnAuthenticationEntryPointHandler)
                .accessDeniedHandler(loginUnAccessDeniedHandler));
        http.logout(logout -> logout.logoutSuccessHandler(logoutStatusSuccessHandler));
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
    public AuthenticationManager authenticationManager() throws Exception {
        // 1. 创建密码登录的 Provider
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(userDetailsService);
        daoProvider.setPasswordEncoder(encoder());

        // 2. 创建组合 ProviderManager
        ProviderManager providerManager = new ProviderManager(daoProvider, smsCodeAuthenticationProvider);
        return providerManager;
    }

}
