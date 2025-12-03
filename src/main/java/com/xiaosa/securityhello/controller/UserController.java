package com.xiaosa.securityhello.controller;

import com.xiaosa.securityhello.common.Result;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    //匿名访问
    @GetMapping("/test1")
    public Result<String>  getTest(){
        return Result.ok("匿名");
    }
    //认证后访问
    @GetMapping("/test2")
    public Result<String> getTest2(){
        return Result.ok("已认证");
    }
    //认证+admin角色访问
    @PreAuthorize(value = "hasRole('admin')")
    @GetMapping("/test3")
    public Result<String> getTest3(){
        return Result.ok("已admin");
    }
    //认证+cto||cfo角色访问
    @GetMapping("/test4")
    public Result<String> getTest4(){
        return Result.ok("认证+cto||cfo角色访问");
    }
    //认证+cto&&ceo角色访问
    @GetMapping("/test5")
    public Result<String> getTest5(){
        return Result.ok("认证+cto&&cfo角色访问");
    }
    //认证+del权限访问
    @GetMapping("/test6")
    public Result<String> getTest6(){
        return Result.ok("认证+cto&&cfo角色访问");
    }
    //认证+del||edit权限访问
    @GetMapping("/test7")
    public Result<String> getTest7(){
        return Result.ok("认证+cto&&cfo角色访问");
    }
    //认证+del**edit权限访问
    @GetMapping("/test8")
    public Result<String> getTest8(){
        return Result.ok("认证+cto&&cfo角色访问");
    }
}
