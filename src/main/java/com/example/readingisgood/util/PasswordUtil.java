package com.example.readingisgood.util;

import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class PasswordUtil {

    private final SecureRandom random = new SecureRandom();
    private final MessageDigest messageDigest;

    public PasswordUtil() throws NoSuchAlgorithmException {
        messageDigest = MessageDigest.getInstance("SHA-512");
    }

    public String generateSalt(int size) {
        byte[] array = new byte[7]; // length is bounded by 7
        random.nextBytes(array);
        return Base64.getEncoder().encodeToString(array);
    }

    public String hash(String salt, String password) {
        return Base64.getEncoder().encodeToString(
                messageDigest.digest((password + salt).getBytes(StandardCharsets.UTF_8))
        );
    }
}
