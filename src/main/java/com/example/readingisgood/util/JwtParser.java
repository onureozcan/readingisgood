package com.example.readingisgood.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.example.readingisgood.constant.JwtConstants.ISSUER;
import static com.example.readingisgood.constant.JwtConstants.KEY;

@Component
public class JwtParser {

    public DefaultClaims getPayload(String token) {
        return (DefaultClaims) Jwts.parser()
                .setSigningKey(KEY)
                .requireIssuer(ISSUER)
                .parse(token)
                .getBody();
    }
}
