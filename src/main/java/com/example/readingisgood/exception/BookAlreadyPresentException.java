package com.example.readingisgood.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "this book was already saved")
public class BookAlreadyPresentException extends RuntimeException {
    public BookAlreadyPresentException(String isbn) {
        super("a book with ISBN [" + isbn + "] already exists");
    }
}
