package com.example.readingisgood.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "order count cannot be negative")
public class OrderCountNegativeException extends RuntimeException {
}
