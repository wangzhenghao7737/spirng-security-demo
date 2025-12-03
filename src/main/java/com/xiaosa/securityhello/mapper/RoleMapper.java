package com.xiaosa.securityhello.mapper;

import com.xiaosa.securityhello.domain.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author D14
* @description 针对表【role】的数据库操作Mapper
* @createDate 2025-12-02 23:37:53
* @Entity generator.domain.Role
*/
public interface RoleMapper extends BaseMapper<Role> {
    List<Role> selectRoleNameByRoleIds(@Param("roleIds") List<Long> roleIds);
}




