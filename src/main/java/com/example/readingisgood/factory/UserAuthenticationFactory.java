package com.example.readingisgood.factory;

import com.example.readingisgood.service.auth.JwtParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class UserAuthenticationFactory {

    @Autowired
    private JwtParser jwtParser;

    public Authentication getAuthentication(HttpServletRequest request) {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.contains("Bearer ")) {
            String token = authHeader.replaceFirst("Bearer ", "");
            String payload = jwtParser.getPayload(token);

        }

        return null;
    }
}