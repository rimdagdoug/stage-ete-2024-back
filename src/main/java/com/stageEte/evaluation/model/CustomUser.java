package com.stageEte.evaluation.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomUser extends User {

    private final int userID;

    public CustomUser(String username, String password, boolean enabled, boolean accountNonExpired,
                      boolean credentialsNonExpired,
                      boolean accountNonLocked,
                      Collection<? extends GrantedAuthority> authorities, int userID) {
        super();
        this.userID = userID;
    }
}
