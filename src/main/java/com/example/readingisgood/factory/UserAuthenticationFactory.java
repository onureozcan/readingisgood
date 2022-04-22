package com.example.readingisgood.factory;

import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public class UserAuthenticationFactory {

    public static Authentication getAuthentication(HttpServletRequest request) {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.contains("Bearer ")) {
            String token = authHeader.replaceFirst("Bearer ", "");

        }

        return null;
    }
}