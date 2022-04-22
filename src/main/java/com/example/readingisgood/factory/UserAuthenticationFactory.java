package com.example.readingisgood.factory;

import com.example.readingisgood.exception.InvalidAuthenticationException;
import com.example.readingisgood.service.auth.JwtParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class UserAuthenticationFactory {

    @Autowired
    private JwtParser jwtParser;

    @Autowired
    private ObjectMapper objectMapper;

    public Authentication getAuthentication(HttpServletRequest request) {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.contains("Bearer ")) {
            String token = authHeader.replaceFirst("Bearer ", "");
            String payload = jwtParser.getPayload(token);

            try {
                objectMapper.readTree(payload);
            } catch (JsonProcessingException exception) {
                throw new InvalidAuthenticationException(exception);
            }
        }

        return null;
    }
}