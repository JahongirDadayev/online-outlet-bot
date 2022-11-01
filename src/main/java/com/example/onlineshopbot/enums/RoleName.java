package com.example.onlineshopbot.enums;

import org.springframework.security.core.GrantedAuthority;

public enum RoleName implements GrantedAuthority {
    ADMIN;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
