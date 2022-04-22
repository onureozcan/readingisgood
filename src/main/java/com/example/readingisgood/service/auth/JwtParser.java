package com.example.readingisgood.service.auth;

import io.jsonwebtoken.Jwts;

import java.nio.charset.StandardCharsets;
import java.util.Base64;


public class JwtParser {
    private static final String KEY = "secret";
    private static final String ISSUER = "reading-is-good";

    public static String getPayload(String token) {
        return Jwts.parser()
                .setSigningKey(Base64.getEncoder().encode(KEY.getBytes(StandardCharsets.UTF_8)))
                .requireIssuer(ISSUER)
                .parse(token)
                .getBody()
                .toString();
    }
}