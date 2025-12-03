package com.xiaosa.securityhello.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaosa.securityhello.domain.User;
import com.xiaosa.securityhello.service.UserService;
import com.xiaosa.securityhello.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author D14
* @description 针对表【user】的数据库操作Service实现
* @createDate 2025-12-02 23:37:53
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

}




