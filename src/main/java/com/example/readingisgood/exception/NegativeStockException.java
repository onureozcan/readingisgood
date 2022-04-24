package com.example.readingisgood.exception;

public class NegativeStockException extends RuntimeException {

    public NegativeStockException(String isbn, int countRequested, int countCurrent) {
        super("Stocks of the book with id " + isbn
                + " cannot be updated with " + countRequested
                + " as it will make the current count " + countCurrent
                + " a negative number"
        );
    }
}
