package com.xiaosa.securityhello.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaosa.securityhello.domain.UserRole;
import com.xiaosa.securityhello.mapper.UserRoleMapper;
import com.xiaosa.securityhello.service.UserRoleService;
import org.springframework.stereotype.Service;

/**
* @author D14
* @description 针对表【user_role】的数据库操作Service实现
* @createDate 2025-12-03 00:01:32
*/
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole>
    implements UserRoleService {

}




