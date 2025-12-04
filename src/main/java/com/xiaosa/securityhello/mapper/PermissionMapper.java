package com.xiaosa.securityhello.mapper;

import com.xiaosa.securityhello.domain.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
* @author D14
* @description 针对表【permission】的数据库操作Mapper
* @createDate 2025-12-02 23:37:53
* @Entity generator.domain.Permission
*/
public interface PermissionMapper extends BaseMapper<Permission> {
    List<Permission> selectPermissionsNameByPermissionIds(@Param("permissionIds") List<Long> permissionIds);
}




