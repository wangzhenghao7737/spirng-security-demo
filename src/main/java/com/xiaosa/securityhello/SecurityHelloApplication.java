package com.xiaosa.securityhello;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.xiaosa.securityhello.mapper")
public class SecurityHelloApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityHelloApplication.class, args);
    }

}
