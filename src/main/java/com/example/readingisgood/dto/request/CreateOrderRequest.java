package com.example.readingisgood.dto.request;

public class CreateOrderRequest {

    private String bookIsbn;
    private int count;

    public CreateOrderRequest() {
    }

    public CreateOrderRequest(String bookIsbn, int count) {
        this.bookIsbn = bookIsbn;
        this.count = count;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
