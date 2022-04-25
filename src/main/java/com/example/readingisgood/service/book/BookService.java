package com.example.readingisgood.service.book;

import com.example.readingisgood.dto.request.CreateBookRequest;
import com.example.readingisgood.dto.request.StockUpdateRequest;
import com.example.readingisgood.exception.BookAlreadyPresentException;
import com.example.readingisgood.exception.NegativeStockException;
import com.example.readingisgood.exception.NoSuchBookException;
import com.example.readingisgood.mapper.BookMapper;
import com.example.readingisgood.model.Book;
import com.example.readingisgood.repository.BookRepository;
import com.example.readingisgood.service.MessageProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${queue.stock-updated}")
    private String stockUpdatedQueueName;

    public Book getBookById(String id) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        if (bookOptional.isEmpty()) {
            throw new NoSuchBookException(id);
        }

        return bookOptional.get();
    }

    public Book createBook(CreateBookRequest request) {
        if (bookRepository.findById(request.getIsbn()).isPresent()) {
            throw new BookAlreadyPresentException(request.getIsbn());
        }
        return bookRepository.save(bookMapper.toBook(request));
    }

    public void updateStock(StockUpdateRequest request) {
        if (bookRepository.findById(request.getIsbn()).isEmpty()) {
            throw new NoSuchBookException(request.getIsbn());
        }
        try {
            messageProducer.produce(
                    stockUpdatedQueueName, request.getIsbn(), objectMapper.writeValueAsString(request)
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleStockUpdate(StockUpdateRequest request) throws NegativeStockException {
        Optional<Book> bookOptional = bookRepository.findById(request.getIsbn());
        if (bookOptional.isEmpty()) {
            throw new NoSuchBookException(request.getIsbn());
        }

        Book book = bookOptional.get();

        int finalCount = book.getCount() + request.getCount();
        if (finalCount < 0) {
            throw new NegativeStockException(request.getIsbn(), request.getCount(), book.getCount());
        }

        book.setCount(finalCount);
        bookRepository.save(book);
    }
}
