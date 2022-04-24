package com.example.readingisgood.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "there is no such book")
public class NoSuchBookException extends RuntimeException {
    public NoSuchBookException(String isbn) {
        super("a book with ISBN [" + isbn + "] is not present in the db");
    }
}
