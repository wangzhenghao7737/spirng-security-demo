package com.xiaosa.securityhello.security;

import com.xiaosa.securityhello.domain.*;
import com.xiaosa.securityhello.mapper.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author D14
 * @description 从数据库获取用户心信息，放入SpringSecurity上下文
 * @createDate 2025-12-02 23:37:53
 */
@Transactional
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private RolePermissionMapper rolePermissionMapper;
    @Resource
    private PermissionMapper permissionMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectByUserPhone(username);
        if(Objects.isNull( user)){
            throw new UsernameNotFoundException("用户不存在");
        }
        List<String> roleNameList = new ArrayList<>();
        List<String> permissionNameList = new ArrayList<>();
        //用户角色列表
        List<UserRole> userRoles = userRoleMapper.selectByUserId(user.getUserId());
        if(!CollectionUtils.isEmpty(userRoles)){
            List<Long> roleIdList = userRoles.stream()
                    .map(UserRole::getRoleId)
                    .collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(roleIdList)){
                //角色列表
                List<Role> roles = roleMapper.selectRoleNameByRoleIds(roleIdList);
                if(!CollectionUtils.isEmpty(roles)){
                    List<String> roleNames = roles.stream()
                            .map(Role::getRoleName)
                            .collect(Collectors.toList());
                    roleNameList.addAll(roleNames);
                }
                //权限列表
                List<RolePermission> rolePermissions = rolePermissionMapper.selectPermissionsIdByRoleIds(roleIdList);
                if(!CollectionUtils.isEmpty(rolePermissions)){
                    List<Long> permissionsIdList = rolePermissions.stream()
                            .map(RolePermission::getPermissionId)
                            .collect(Collectors.toList());
                    if(!CollectionUtils.isEmpty(permissionsIdList)){
                        List<Permission> permissions = permissionMapper.selectPermissionsNameByPermissionIds(permissionsIdList);
                        if(!CollectionUtils.isEmpty(permissions)){
                            List<String> permissionNames = permissions.stream()
                                    .map(Permission::getPermissionName)
                                    .collect(Collectors.toList());
                            permissionNameList.addAll(permissionNames);
                        }
                    }
                }
            }
        }
        return new LoginUserDetails(user, roleNameList, permissionNameList);
    }
}
