package com.example.readingisgood.factory;

import com.example.readingisgood.pojo.auth.AuthenticationPayload;
import com.example.readingisgood.exception.InvalidAuthenticationPayloadException;
import com.example.readingisgood.mapper.AuthenticationMapper;
import com.example.readingisgood.pojo.auth.CustomAuthentication;
import com.example.readingisgood.util.JwtParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
public class UserAuthenticationFactory {

    @Autowired
    private JwtParser jwtParser;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthenticationMapper authenticationMapper;

    public Authentication getAuthentication(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.contains("Bearer ")) {
            String token = authHeader.replaceFirst("Bearer ", "");
            DefaultClaims payload = jwtParser.getPayload(token);
            try {
                AuthenticationPayload authenticationPayload = objectMapper.readValue(
                        payload.get("payload", String.class), AuthenticationPayload.class
                );
                return authenticationMapper.toAuthentication(authenticationPayload);
            } catch (JsonProcessingException exception) {
                throw new InvalidAuthenticationPayloadException(exception);
            }
        }
        return null;
    }
}