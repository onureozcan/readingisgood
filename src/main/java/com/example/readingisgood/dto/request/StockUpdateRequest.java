package com.example.readingisgood.dto.request;

public class StockUpdateRequest {

    private String isbn;
    private int count;

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
