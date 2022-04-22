package com.example.readingisgood.mapper;

import com.example.readingisgood.pojo.auth.AuthenticationPayload;
import com.example.readingisgood.pojo.auth.CustomAuthentication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class AuthenticationMapper {
    public Authentication toAuthentication(AuthenticationPayload payload) {
        return new CustomAuthentication(
                payload.getId(), payload.getName(), payload.getEmail(),
                payload.getRoles().stream()
                        .map(Enum::name)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
        );
    }
}
