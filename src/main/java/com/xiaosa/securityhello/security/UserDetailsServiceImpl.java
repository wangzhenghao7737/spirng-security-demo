package com.xiaosa.securityhello.security;

import com.xiaosa.securityhello.domain.*;
import com.xiaosa.securityhello.mapper.*;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
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
    /**
     * @param username
     * Long id , start at 20000000000
     * phone String
     * 20000000000
     * 13812341234
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (isValidPhone(username)) {
            return loadUserByPhone(username);
        } else if (isNumeric(username)) {
            User user = userMapper.selectByUserId(Long.parseLong(username));
            if (user == null) throw new UsernameNotFoundException("用户不存在");
            return buildLoginUserDetails(user, username);
        } else {
            // 可扩展：邮箱等
            throw new UsernameNotFoundException("不支持的用户名格式");
        }
    }



    private UserDetails loadUserByPhone(String phone) {
        User user = userMapper.selectByUserPhone(phone);
        if (user == null) {
            throw new UsernameNotFoundException("手机号未注册");
        }
        // 复用你原有的角色/权限加载逻辑
        return buildLoginUserDetails(user, phone);
    }
    // 新增私有方法，提取公共逻辑
    private LoginUserDetails buildLoginUserDetails(User user, String loginIdentifier) {
        List<String> roleNameList = new ArrayList<>();
        List<String> permissionNameList = new ArrayList<>();

        List<UserRole> userRoles = userRoleMapper.selectByUserId(user.getUserId());
        if (!CollectionUtils.isEmpty(userRoles)) {
            List<Long> roleIdList = userRoles.stream()
                    .map(UserRole::getRoleId)
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(roleIdList)) {
                List<Role> roles = roleMapper.selectRoleNameByRoleIds(roleIdList);
                if (!CollectionUtils.isEmpty(roles)) {
                    roleNameList.addAll(roles.stream().map(Role::getRoleName).toList());
                }
                List<RolePermission> rolePermissions = rolePermissionMapper.selectPermissionsIdByRoleIds(roleIdList);
                if (!CollectionUtils.isEmpty(rolePermissions)) {
                    List<Long> permissionsIdList = rolePermissions.stream()
                            .map(RolePermission::getPermissionId)
                            .collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(permissionsIdList)) {
                        List<Permission> permissions = permissionMapper.selectPermissionsNameByPermissionIds(permissionsIdList);
                        if (!CollectionUtils.isEmpty(permissions)) {
                            permissionNameList.addAll(permissions.stream().map(Permission::getPermissionName).toList());
                        }
                    }
                }
            }
        }
        return new LoginUserDetails(user, roleNameList, permissionNameList, loginIdentifier);
    }

    private boolean isNumeric(String str) {
        return str != null && str.matches("\\d+");
    }

    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("1[3-9]\\d{9}");
    }
}
