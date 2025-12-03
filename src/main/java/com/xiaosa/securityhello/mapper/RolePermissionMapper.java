package com.xiaosa.securityhello.mapper;

import com.xiaosa.securityhello.domain.RolePermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author D14
* @description 针对表【role_permission】的数据库操作Mapper
* @createDate 2025-12-02 23:57:58
* @Entity com.xiaosa.securityhello.domain.RolePermission
*/
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
    //通过角色id列表，查询权限
    List<RolePermission> selectPermissionsIdByRoleIds(@Param("roleIds")List<Long> roleIds);
}




