package com.example.readingisgood.util;

import com.example.readingisgood.mapper.AuthenticationMapper;
import com.example.readingisgood.pojo.auth.AuthenticationPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.example.readingisgood.constant.JwtConstants.ISSUER;
import static com.example.readingisgood.constant.JwtConstants.KEY;

@Component
public class JwtGenerator {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthenticationMapper authenticationMapper;

    public String generateJwt(AuthenticationPayload payload) throws JsonProcessingException {
        return Jwts.builder()
                .setClaims(authenticationMapper.toClaims(payload))
                .setIssuer(ISSUER)
                .signWith(
                        SignatureAlgorithm.HS512, KEY
                ).compact();
    }
}
