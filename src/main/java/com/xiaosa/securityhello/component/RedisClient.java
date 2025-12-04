package com.xiaosa.securityhello.component;


import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

/**
 * redis数据库客户端
 */
@Component
public class RedisClient {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 保存数据
     */
    public void set (String key,String value){
        stringRedisTemplate.opsForValue().set(key,value);
    }

    public void set (String key,String value,Long time){
        stringRedisTemplate.opsForValue().set(key,value,time, TimeUnit.MILLISECONDS);
    }

    public String get(String key){
        return stringRedisTemplate.opsForValue().get(key);
    }

    public void del(String key){
        stringRedisTemplate.delete(key);
    }

    public Boolean exists(String key){
        return stringRedisTemplate.hasKey(key);
    }
    public void expire(String key,Long time){
        stringRedisTemplate.expire(key,time,TimeUnit.MILLISECONDS);
    }
}
