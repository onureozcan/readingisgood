package com.example.readingisgood.pojo.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public class CustomAuthentication implements Authentication {

    private final List<GrantedAuthority> roles;
    private final String id;
    private final String name;
    private final String email;

    public CustomAuthentication(String id, String name, String email, List<GrantedAuthority> roles) {
        this.roles = roles;
        this.id = id;
        this.name = name;
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return email;
    }

    @Override
    public Object getPrincipal() {
        return id;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public String getName() {
        return name;
    }
}
