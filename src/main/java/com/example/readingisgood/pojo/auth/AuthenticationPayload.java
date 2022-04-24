package com.example.readingisgood.pojo.auth;

import com.example.readingisgood.enums.Role;

import java.util.List;

public class AuthenticationPayload {
    private String id;
    private String name;
    private String email;
    private List<Role> roles = List.of();

    public AuthenticationPayload() {
    }

    public AuthenticationPayload(String id, String name, String email, List<Role> roles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.roles = roles;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
