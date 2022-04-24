package com.example.readingisgood.repository;

import com.example.readingisgood.TestMongoDb;
import com.example.readingisgood.model.Book;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles(profiles = "test")
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @BeforeAll
    public static void beforeAll() throws IOException {
        TestMongoDb.start();
    }

    @AfterAll
    public static void afterAll() {
        TestMongoDb.stop();
    }

    @Test
    public void testCreateNewBook() {
        Book newBook = getNewBook();
        bookRepository.save(newBook);

        Book found = bookRepository.findById(newBook.getId()).orElseThrow();
        assertBook(newBook, found);
    }

    @Test
    public void testUpdateNewBook() {
        Book newBook = getNewBook();
        bookRepository.save(newBook);

        newBook.setName("Updated");
        newBook.setAuthor("Updated Author");
        newBook.setCount(10);

        bookRepository.save(newBook);

        Book found = bookRepository.findById(newBook.getId()).orElseThrow();
        assertBook(newBook, found);
    }

    private Book getNewBook() {
        Book book = new Book();
        book.setId("test isbn");
        book.setName("test book");
        book.setAuthor("test author");
        book.setCount(100);
        book.setPublishedAt(Instant.now().minus(Duration.ofDays(200)));
        return book;
    }

    private void assertBook(Book newBook, Book found) {
        assertAll(
                () -> assertNotNull(found),
                () -> assertEquals(newBook.getName(), found.getName()),
                () -> assertEquals(newBook.getId(), found.getId()),
                () -> assertEquals(newBook.getAuthor(), found.getAuthor()),
                () -> assertEquals(newBook.getPublishedAt(), found.getPublishedAt()),
                () -> assertEquals(newBook.getCount(), found.getCount())
        );
    }
}
