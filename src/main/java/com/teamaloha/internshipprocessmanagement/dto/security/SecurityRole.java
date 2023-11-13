package com.teamaloha.internshipprocessmanagement.dto.security;

import com.teamaloha.internshipprocessmanagement.enums.RoleEnum;
import org.springframework.security.core.GrantedAuthority;

public record SecurityRole(RoleEnum roleEnum) implements GrantedAuthority {

    @Override
    public String getAuthority() {
        return roleEnum.name();
    }
}
