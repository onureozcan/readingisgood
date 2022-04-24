package com.example.readingisgood.service.book;

import com.example.readingisgood.dto.request.CreateBookRequest;
import com.example.readingisgood.exception.BookAlreadyPresentException;
import com.example.readingisgood.exception.UserAlreadyPresentException;
import com.example.readingisgood.mapper.BookMapper;
import com.example.readingisgood.model.Book;
import com.example.readingisgood.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookMapper bookMapper;

    public Book createBook(CreateBookRequest request) {
        if (bookRepository.findById(request.getIsbn()).isPresent()) {
            throw new BookAlreadyPresentException(request.getIsbn());
        }
        return bookRepository.save(bookMapper.toBook(request));
    }
}
