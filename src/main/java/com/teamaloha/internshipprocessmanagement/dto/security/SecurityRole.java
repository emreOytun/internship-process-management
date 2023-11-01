package com.teamaloha.internshipprocessmanagement.dto.security;

import com.teamaloha.internshipprocessmanagement.enums.RoleEnum;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
public class SecurityRole implements GrantedAuthority {

    private final RoleEnum roleEnum;

    @Override
    public String getAuthority() {
        return roleEnum.getValue();
    }
}
