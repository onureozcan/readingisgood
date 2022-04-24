package com.example.readingisgood.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserAlreadyPresentException extends RuntimeException {
    public UserAlreadyPresentException(String email) {
        super("a user with email [" + email + "] already exists");
    }
}
