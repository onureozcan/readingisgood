package com.example.readingisgood.service.book;

import com.example.readingisgood.dto.request.CreateBookRequest;
import com.example.readingisgood.dto.request.StockUpdateRequest;
import com.example.readingisgood.exception.BookAlreadyPresentException;
import com.example.readingisgood.exception.NegativeStockException;
import com.example.readingisgood.exception.NoSuchBookException;
import com.example.readingisgood.model.Book;
import com.example.readingisgood.repository.BookRepository;
import com.example.readingisgood.service.MessageProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BookServiceTest {

    @Autowired
    private BookService bookService;

    @MockBean
    private MessageProducer messageProducer;

    @MockBean
    private BookRepository bookRepository;

    @Value("${queue.stock-updated}")
    private String stockUpdatedQueueName;

    private final Book testBook = getTestBook();

    @BeforeEach
    public void before() {
        when(bookRepository.findById("some isbn")).thenReturn(
                Optional.of(testBook)
        );
    }

    @Test
    public void shouldCreateBook() {
        when(bookRepository.findById(testBook.getId())).thenReturn(
                Optional.empty()
        );

        CreateBookRequest request = getCreateRequest();
        bookService.createBook(request);

        ArgumentCaptor<Book> argumentCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository, times(1)).save(argumentCaptor.capture());

        assertBook(request, argumentCaptor.getValue());
    }

    @Test
    public void shouldFailToCreateBookIfPresent() {
        CreateBookRequest request = getCreateRequest();

        assertThrows(BookAlreadyPresentException.class, ()-> bookService.createBook(request));
    }

    private CreateBookRequest getCreateRequest() {
        CreateBookRequest request = new CreateBookRequest();
        request.setAuthor("test author");
        request.setName("test book");
        request.setIsbn("some isbn");
        request.setPublishDate(Instant.now().minus(Duration.ofDays(822)));
        return request;
    }

    @Test
    public void shouldProduceStockUpdateMessage() throws JsonProcessingException {
        Book testBook = getTestBook();

        bookService.updateStock(
                new StockUpdateRequest(testBook.getId(), 200)
        );

        verify(messageProducer, times(1)).produce(
                eq(stockUpdatedQueueName), eq(testBook.getId()), any()
        );
    }

    @Test
    public void shouldFailToProduceStockUpdateMessage() throws JsonProcessingException {
        when(bookRepository.findById(testBook.getId())).thenReturn(
                Optional.empty()
        );

        assertThrows(NoSuchBookException.class, () -> bookService.updateStock(
                new StockUpdateRequest(testBook.getId(), 200)
        ));

        verify(messageProducer, times(0)).produce(
                eq(stockUpdatedQueueName), eq(testBook.getId()), any()
        );
    }

    @Test
    public void shouldUpdateStock() {
        bookService.handleStockUpdate(
                new StockUpdateRequest(testBook.getId(), 100)
        );

        ArgumentCaptor<Book> argumentCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository, times(1)).save(argumentCaptor.capture());

        assertEquals(600, argumentCaptor.getValue().getCount());
    }

    @Test
    public void shouldFailToUpdateStockIfNegative() {
        assertThrows(NegativeStockException.class, () -> bookService.handleStockUpdate(
                new StockUpdateRequest(testBook.getId(), -800)
        ));

        verify(bookRepository, times(0)).save(any());
    }

    private Book getTestBook() {
        Book testBook = new Book();
        testBook.setId("some isbn");
        testBook.setName("some book");
        testBook.setAuthor("some author");
        testBook.setCount(500);
        testBook.setPublishedAt(Instant.now().minus(Duration.ofDays(250)));
        return testBook;
    }

    private void assertBook(CreateBookRequest expected, Book found) {
        assertAll(
                () -> assertEquals(expected.getName(), found.getName()),
                () -> assertEquals(expected.getIsbn(), found.getId()),
                () -> assertEquals(expected.getAuthor(), found.getAuthor()),
                () -> assertEquals(expected.getPublishDate(), found.getPublishedAt()),
                () -> assertEquals(0, found.getCount())
        );
    }
}
