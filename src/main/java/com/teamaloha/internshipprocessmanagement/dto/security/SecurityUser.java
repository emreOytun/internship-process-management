package com.teamaloha.internshipprocessmanagement.dto.security;

import com.teamaloha.internshipprocessmanagement.dto.user.UserDto;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class SecurityUser implements UserDetails {

    private UserDto userDto;

    public SecurityUser(UserDto userDto) { this.userDto = userDto; }

    public UserDto getUserDto() {
        return userDto;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection authorityList = new ArrayList();
        authorityList.add(new SecurityRole(userDto.getRoleEnum()));
        return authorityList;
    }

    @Override
    public String getPassword() {
        return userDto.getPassword();
    }

    @Override
    public String getUsername() {
        return userDto.getMail();
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
