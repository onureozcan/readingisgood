package com.example.readingisgood.util;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class JwtParser {
    private static final String KEY = "secret";
    private static final String ISSUER = "reading-is-good";

    public String getPayload(String token) {
        return Jwts.parser()
                .setSigningKey(Base64.getEncoder().encode(KEY.getBytes(StandardCharsets.UTF_8)))
                .requireIssuer(ISSUER)
                .parse(token)
                .getBody()
                .toString();
    }
}
