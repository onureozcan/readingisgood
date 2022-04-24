package com.example.readingisgood.constant;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class JwtConstants {
    public static final byte[] KEY = Base64.getEncoder().encode("secret".getBytes(StandardCharsets.UTF_8));
    public static final String ISSUER = "reading-is-good";
}
