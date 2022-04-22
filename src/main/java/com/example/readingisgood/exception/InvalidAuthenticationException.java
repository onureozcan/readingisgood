package com.example.readingisgood.exception;

import com.fasterxml.jackson.core.JsonParseException;

public class InvalidAuthenticationException extends RuntimeException {
    public InvalidAuthenticationException(Exception cause) {
        super("Could not create and authentication", cause);
    }
}
