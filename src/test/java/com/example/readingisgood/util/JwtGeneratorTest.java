package com.example.readingisgood.util;

import com.example.readingisgood.pojo.auth.AuthenticationPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JwtGeneratorTest {

    @Autowired
    private JwtGenerator jwtGenerator;

    @Autowired
    private JwtParser jwtParser;

    @Test
    public void shouldGenerateValidToken() throws JsonProcessingException {
        String jwt = jwtGenerator.generateJwt(
                new AuthenticationPayload()
        );
        jwtParser.getPayload(jwt);
    }
}
