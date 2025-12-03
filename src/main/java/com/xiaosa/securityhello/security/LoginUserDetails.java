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
    public LoginUserDetails(User user) {
        this.user = user;
    }
    public LoginUserDetails() {
    }

    public LoginUserDetails(User user, List<String> roleNames, List<String> permissionNames) {
        this.user = user;
        this.roleNames = roleNames;
        this.permissionNames = permissionNames;
    }

    /**
     * 权限列表
     * 用户的权限信息
     * 1.角色 ROLE_ eg.ROLE_ADMIN
     * 2.权限
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if(!CollectionUtils.isEmpty(roleNames)){
            for (String roleName : roleNames) {
               roleName = "ROLE_" + roleName;
               authorities.add(new SimpleGrantedAuthority(roleName));
            }
        }
        if(!CollectionUtils.isEmpty(permissionNames)){
            for (String permissionName : permissionNames) {
                authorities.add(new SimpleGrantedAuthority(permissionName));
            }
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getPhone();
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
