package com.example.readingisgood.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "there is no such order")
public class NoSuchOrderException extends RuntimeException {
    public NoSuchOrderException(String id) {
        super("an order with id [" + id + "] is not present in the db");
    }
}
