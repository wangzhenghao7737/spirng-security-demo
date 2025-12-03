package com.xiaosa.securityhello.mapper;

import com.xiaosa.securityhello.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author D14
* @description 针对表【user】的数据库操作Mapper
* @createDate 2025-12-02 23:37:53
* @Entity generator.domain.User
*/
public interface UserMapper extends BaseMapper<User> {

    User selectByUserPhone(String phone);
}




