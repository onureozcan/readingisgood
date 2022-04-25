package com.example.readingisgood.controller;

import com.example.readingisgood.dto.request.CreateBookRequest;
import com.example.readingisgood.dto.request.StockUpdateRequest;
import com.example.readingisgood.model.Book;
import com.example.readingisgood.service.book.BookService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/private/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping
    @ApiOperation("Creates a new book. Managers only")
    @PreAuthorize("hasRole('MANAGER')")
    public Book createBook(@RequestBody CreateBookRequest createBookRequest) {
        return bookService.createBook(createBookRequest);
    }

    @PostMapping("stock-update")
    @ApiOperation("Places a stock update request to process later. Managers only")
    @PreAuthorize("hasRole('MANAGER')")
    public void addToStock(@RequestBody StockUpdateRequest stockUpdateRequest) {
        bookService.updateStock(stockUpdateRequest);
    }
}
