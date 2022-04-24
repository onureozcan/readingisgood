package com.example.readingisgood.exception;

public class UserAlreadyPresentException extends RuntimeException {

    public UserAlreadyPresentException(String email) {
        super("a user with email [" + email + "] already exists");
    }
}
