package com.xiaosa.securityhello.security;

import com.xiaosa.securityhello.domain.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class LoginUserDetails implements UserDetails {
    private User user;
    private List<String> roleNames;
    private List<String> permissionNames;
    private String loginIdentifier;
    public LoginUserDetails() {
    }

    public LoginUserDetails(User user, List<String> roleNames, List<String> permissionNames, String loginIdentifier) {
        this.user = user;
        this.roleNames = roleNames;
        this.permissionNames = permissionNames;
        this.loginIdentifier = loginIdentifier;
    }

    /**
     * 权限列表
     * 用户的权限信息
     * 1.角色 ROLE_ eg.ROLE_ADMIN
     * 2.权限
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        /*
         * 当前用户的权限信息
         * 1. 角色表权限   ROLE_  admin  ROLE_admin
         * 2. 权限表权限  del  add query edit
         */
        if(!CollectionUtils.isEmpty(roleNames)){
            for (String roleName : roleNames) {
                roleName = "ROLE_"+roleName;
                grantedAuthorities.add(new SimpleGrantedAuthority(roleName));
            }
        }
        if(!CollectionUtils.isEmpty(permissionNames)){
            for (String permissionName : permissionNames) {
                grantedAuthorities.add(new SimpleGrantedAuthority(permissionName));
            }
        }
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 根据登录时传入的数据：Long , phone
     * 在UserDetailsServiceImpl中查询用户信息
     * 最终user_id为凭据
     */
    @Override
    public String getUsername() {
        return loginIdentifier; // 返回用户实际输入的内容
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
