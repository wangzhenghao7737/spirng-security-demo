package com.xiaosa.securityhello;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;

@SpringBootTest
class SecurityHelloApplicationTests {

    @Resource
    private PasswordEncoder passwordEncoder;
    @Test
    void contextLoads() {
    }
    @Test
    void test_pwd() {
        String encode = passwordEncoder.encode("123456");
        System.out.println( encode);
    }

}
