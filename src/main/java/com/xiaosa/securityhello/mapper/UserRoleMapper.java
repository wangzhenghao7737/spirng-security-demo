package com.xiaosa.securityhello.mapper;

import com.xiaosa.securityhello.domain.UserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author D14
* @description 针对表【user_role】的数据库操作Mapper
* @createDate 2025-12-03 00:01:32
* @Entity com.xiaosa.securityhello.domain.UserRole
*/
public interface UserRoleMapper extends BaseMapper<UserRole> {
    List<UserRole> selectByUserId(Long userId);
}




