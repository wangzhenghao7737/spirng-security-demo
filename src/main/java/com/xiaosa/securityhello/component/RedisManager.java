package com.xiaosa.securityhello.component;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
public class RedisManager {
    @Resource
    private RedisTemplate<String,String> redisTemplate;
    public void set(String key,String value){
        redisTemplate.opsForValue().set(key,value);
    }
    public void set(String key,String value,long time){
        redisTemplate.opsForValue().set(key,value, time, TimeUnit.MINUTES);
    }
    public String get(String key){
        return redisTemplate.opsForValue().get(key);
    }
    public void delete(String key){
        redisTemplate.delete(key);
    }
    public boolean exists(String key){
        return redisTemplate.hasKey(key);
    }

}
