package com.xiaosa.securityhello.security;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



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
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.csrf().disable() //防止跨站请求伪造
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //取消session
//                .and()
//                .authorizeRequests()
//                .antMatchers("/login","/test1").permitAll() //登陆和未登录的人都可以访问访问
//                .anyRequest().authenticated();//除了上面设置的地址可以匿名访问,其它所有的请求地址需要认证访问
//
//        //将自定义的过滤器注册到SpringSecurity过滤器链中,并且设置到UsernamePasswordAuthenticationFilter前面
//        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
//        //注册匿名访问私有资源的处理器
//        http.exceptionHandling().authenticationEntryPoint(loginUnAuthenticationEntryPointHandler);
//        //权限不够交给哪个处理器处理
//        http.exceptionHandling().accessDeniedHandler(loginUnAccessDeniedHandler);
//        //注册自定义注销成功的处理器
//        http.logout().logoutSuccessHandler(logoutStatusSuccessHandler);
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/login","/test1").permitAll()
                .anyRequest().authenticated());
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling(exception -> exception.authenticationEntryPoint(loginUnAuthenticationEntryPointHandler));
        http.exceptionHandling(exception -> exception.accessDeniedHandler(loginUnAccessDeniedHandler));
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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
