package com.example.readingisgood.mapper;

import com.example.readingisgood.dto.request.CreateBookRequest;
import com.example.readingisgood.model.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public Book toBook(CreateBookRequest request) {
        Book book = new Book();
        book.setId(request.getIsbn());
        book.setName(request.getName());
        book.setAuthor(request.getAuthor());
        book.setPublishedAt(request.getPublishDate());
        return book;
    }
}
