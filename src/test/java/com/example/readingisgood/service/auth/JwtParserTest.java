package com.example.readingisgood.service.auth;

import io.jsonwebtoken.IncorrectClaimException;
import io.jsonwebtoken.MissingClaimException;
import io.jsonwebtoken.SignatureException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JwtParserTest {

    @Test
    public void shouldParseJwt() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJyZWFkaW5nLWlzLWdvb2QifQ.aVfOzDDzqxBd8jH1B_eH5GN5ojJk9oP5QQ9rVnDgR9c";
        String payload = JwtParser.getPayload(token);
    }

    @Test
    public void shouldFailToParseJwtWithDifferentSecret() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJyZWFkaW5nLWlzLW5vdC1nb29kIn0.gOeLn5cx1yagF3a3pkcrAyT7SCnY0ioR-6LRAPHkYmY";
        Assertions.assertThrows(SignatureException.class,() -> JwtParser.getPayload(token));
    }

    @Test
    public void shouldFailToParseJwtWithNoIssuer() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkYXRhIjoidGVzdCJ9.ZJefU2-jAMLVgDTW-GQM3KNiIMm1Em_tm4sCasBs1Yg";
        Assertions.assertThrows(MissingClaimException.class,() -> JwtParser.getPayload(token));
    }

    @Test
    public void shouldFailToParseJwtWithDifferentIssuer() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJyZWFkaW5nLWlzLW5vdC1nb29kIn0.ad7sSb9hnQzYNtSnjf8iy8L48yhpGFniT7EOTL5h8y0";
        Assertions.assertThrows(IncorrectClaimException.class,() -> JwtParser.getPayload(token));
    }
}
