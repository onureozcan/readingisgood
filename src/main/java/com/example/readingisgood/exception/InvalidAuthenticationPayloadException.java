package com.example.readingisgood.exception;

public class InvalidAuthenticationPayloadException extends RuntimeException {
    public InvalidAuthenticationPayloadException(Exception cause) {
        super("Could not create an authentication", cause);
    }
}
