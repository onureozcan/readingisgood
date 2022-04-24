package com.example.readingisgood.util;

import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class PasswordUtil {

    private final SecureRandom random = new SecureRandom();
    private final MessageDigest messageDigest;

    public PasswordUtil() throws NoSuchAlgorithmException {
        messageDigest = MessageDigest.getInstance("SHA-512");
    }

    public String generateSalt(int size) {
        byte[] array = new byte[size];
        random.nextBytes(array);
        return Base64.getEncoder().encodeToString(array);
    }

    public String hash(String salt, String password) {
        return Base64.getEncoder().encodeToString(
                messageDigest.digest((password + salt).getBytes(StandardCharsets.UTF_8))
        );
    }

    public String generateFirstTimePassword(int size) {
        Random random = new Random();
        return IntStream.range(1, size)
                .mapToObj(it -> "" + (random.nextInt(9)))
                .collect(Collectors.joining(","));
    }
}
