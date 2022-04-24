package com.example.readingisgood.mapper;

import com.example.readingisgood.enums.Role;
import com.example.readingisgood.model.User;
import com.example.readingisgood.pojo.auth.AuthenticationPayload;
import com.example.readingisgood.pojo.auth.CustomAuthentication;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AuthenticationMapper {

    @Autowired
    private ObjectMapper objectMapper;

    public Authentication toAuthentication(AuthenticationPayload payload) {
        return new CustomAuthentication(
                payload.getId(), payload.getName(), payload.getEmail(),
                payload.getRoles().stream()
                        .map(Enum::name)
                        .map(it -> "ROLE_" + it)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
        );
    }

    public Map<String, Object> toClaims(AuthenticationPayload payload) {
        Map<String, Object> claims = new HashMap<>();
        try {
            claims.put("payload", objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return claims;
    }

    public AuthenticationPayload toPayload(User user) {
        return new AuthenticationPayload(
                user.getId(), user.getName(), user.getEmail(), List.of(Role.valueOf(user.getRole()))
        );
    }
}
