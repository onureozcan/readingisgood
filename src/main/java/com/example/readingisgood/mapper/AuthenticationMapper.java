package com.example.readingisgood.mapper;

import com.example.readingisgood.enums.Role;
import com.example.readingisgood.model.User;
import com.example.readingisgood.pojo.auth.AuthenticationPayload;
import com.example.readingisgood.pojo.auth.CustomAuthentication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public Map<String, Object> toClaims(AuthenticationPayload payload) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", payload.getId());
        claims.put("name", payload.getName());
        claims.put("email", payload.getEmail());
        claims.put("roles", "[" + payload.getRoles()
                .stream()
                .map(Enum::name)
                .map(name -> "\"" + name + "\"")
                .collect(Collectors.joining(",")) + "]" // TODO: poor solution
        );
        return claims;
    }

    public AuthenticationPayload toPayload(User user) {
        return new AuthenticationPayload(
                user.getId(), user.getName(), user.getEmail(), List.of(Role.valueOf(user.getRole()))
        );
    }
}
