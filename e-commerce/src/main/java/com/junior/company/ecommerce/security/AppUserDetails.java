package com.junior.company.ecommerce.security;

import com.junior.company.ecommerce.model.AppUser;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@NoArgsConstructor
public class AppUserDetails implements UserDetails {

    private static final String ROLE_PREFIX = "ROLE_";
    private  Collection<? extends GrantedAuthority> authorities;
    private  String password;
    private  String username;
    private  boolean enabled;

    public AppUserDetails(AppUser appUser) {
        this.authorities = appUser.getRoles().stream()
                .map((role) -> new SimpleGrantedAuthority(ROLE_PREFIX + role.getName()))
                .collect(Collectors.toList());
        this.password = appUser.getPassword();
        this.username = appUser.getEmail();
        this.enabled = appUser.isEnabled();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
        return enabled;
    }
}
