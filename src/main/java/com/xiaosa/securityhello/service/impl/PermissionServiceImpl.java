package com.xiaosa.securityhello.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaosa.securityhello.domain.Permission;
import com.xiaosa.securityhello.service.PermissionService;
import com.xiaosa.securityhello.mapper.PermissionMapper;
import org.springframework.stereotype.Service;

/**
* @author D14
* @description 针对表【permission】的数据库操作Service实现
* @createDate 2025-12-02 23:37:53
*/
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission>
    implements PermissionService {

}




